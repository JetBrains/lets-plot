/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.svg.RichText.Term
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import kotlin.math.roundToInt

internal class LatexTerm(
    private val root: Node
) : Term {
    override val visualCharCount: Int = root.visualCharCount
    override val svg: List<SvgTSpanElement> = root.svg

    override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
        return root.estimateWidth(font, widthCalculator)
    }

    // Inner structure to construct a formula tree
    internal interface Node {
        val visualCharCount: Int
        val svg: List<SvgTSpanElement>

        fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double

        companion object {
            fun textToNode(text: String): Node? {
                val trimmedText = text.trim()
                for (operator in Operator.operators) {
                    // Try to apply operator to whole text
                    val operands = operator.parseEntire(trimmedText)
                    if (operands != null) {
                        return InnerNode(operator, operands.map { textToNode(it) ?: Leaf(it) })
                    }
                    // Try to apply operator to start of the text
                    val (start, end) = operator.parseStart(trimmedText) ?: continue
                    val endNode = textToNode(end) ?: continue // If other part is a Node - the string is a multiplication without explicit symbol (like "ab")
                    val startNode = InnerNode(operator, start.map { textToNode(it) ?: Leaf(it) })
                    return InnerNode(Operator.multiplicationOperatorWithoutSymbol, listOf(startNode, endNode))
                }
                return null // No operator is applicable to the text
            }
        }
    }

    private class InnerNode(val operator: Operator, val subnodes: List<Node>) : Node {
        override val visualCharCount: Int = operator.calcVisualCharCount(subnodes.sumOf { it.visualCharCount })
        override val svg: List<SvgTSpanElement> = operator.getSvg(subnodes.map { it.svg })

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return operator.estimateWidth(subnodes, font, widthCalculator)
        }
    }

    private class Leaf(text: String) : Node {
        private val trimmedText = text.trim()
        override val visualCharCount: Int = trimmedText.length
        override val svg: List<SvgTSpanElement> = listOf(SvgTSpanElement(trimmedText))

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(trimmedText, font)
        }
    }

    private class Operator(
        val parseEntire: (String) -> List<String>?, // return substrings, if the operator is applicable to the whole text. For example, +: "a+b" -> ["a", "b"]
        val parseStart: (String) -> Pair<List<String>, String>?, // return substrings and the rest of the text, if the operator is applicable to the start of the text. For example, +: "(a+b)c" -> (["a", "b"], "c")
        val calcVisualCharCount: (Int) -> Int, // calculate visual char count of the operator, based on the sum of visual char counts of the subnodes
        val getSvg: (List<List<SvgTSpanElement>>) -> List<SvgTSpanElement>, // construct svg, based on the svg of the subnodes
        val estimateWidth: (List<Node>, Font, (String, Font) -> Double) -> Double // estimate width of the operator, based on the widths of the subnodes
    ) {
        companion object {
            val operators: List<Operator> = listOf(
                getBracketsOperator(),
                getBinaryOperator('+'),
                getBinaryOperator('-'),
                getBinaryOperator('/'),
                getIndexOperator(true, "\\^"),
                getIndexOperator(false, "_"),
            )
            val multiplicationOperatorWithoutSymbol = Operator(
                parseEntire = { null }, // parsed specially in textToNode, because it doesn't have explicit symbol (like "ab")
                parseStart = { null },
                calcVisualCharCount = { it },
                getSvg = { it.flatten() },
                estimateWidth = { subnodes, font, widthCalculator ->
                    subnodes.sumOf { it.estimateWidth(font, widthCalculator)
                }
            })

            private fun getBracketsOperator(): Operator {
                fun bracketsClosedCorrectly(text: String): Boolean {
                    var brackets = 0
                    for (i in text.indices) {
                        when (text[i]) {
                            '(' -> brackets++
                            ')' -> if (brackets > 0) {
                                brackets--
                            } else {
                                return false
                            }
                        }
                    }
                    return true
                }
                fun parseEntire(text: String): List<String>? {
                    val trimmedText = text.trim()
                    if (trimmedText.length < 2) {
                        return null
                    }
                    val innerPart = trimmedText.substring(1, trimmedText.length - 1)
                    return if (trimmedText.startsWith("(") && trimmedText.endsWith(")") && bracketsClosedCorrectly(innerPart)) {
                        listOf(innerPart)
                    } else {
                        null
                    }
                }
                fun parseStart(text: String): Pair<List<String>, String>? {
                    val trimmedText = text.trim()
                    if (!trimmedText.startsWith("(") || !bracketsClosedCorrectly(trimmedText)) {
                        return null
                    }
                    var brackets = 0
                    for (i in trimmedText.indices) {
                        when (trimmedText[i]) {
                            '(' -> brackets++
                            ')' -> if (brackets > 1) {
                                brackets--
                            } else {
                                val innerPart = trimmedText.substring(1, i)
                                val rest = trimmedText.substring(i + 1)
                                return Pair(listOf(innerPart), rest)
                            }
                        }
                    }
                    return null
                }
                val calcVisualCharCount: (Int) -> Int = { it + 2 }
                val getSvg: (List<List<SvgTSpanElement>>) -> List<SvgTSpanElement> = { elements ->
                    val innerElements = elements[0]
                    listOf(SvgTSpanElement("(")) + innerElements + listOf(SvgTSpanElement(")"))
                }
                val estimateWidth: (List<Node>, Font, (String, Font) -> Double) -> Double = { subnodes, font, widthCalculator ->
                    val innerWidth = subnodes[0].estimateWidth(font, widthCalculator)
                    val bracketsWidth = widthCalculator("()", font)
                    innerWidth + bracketsWidth
                }
                return Operator(::parseEntire, ::parseStart, calcVisualCharCount, getSvg, estimateWidth)
            }

            private fun getBinaryOperator(operator: Char): Operator {
                fun parseEntire(text: String): List<String>? {
                    val brackets = mutableListOf<Char>()
                    for (i in text.indices) {
                        when (text[i]) {
                            '(' -> brackets.add(')')
                            '{' -> brackets.add('}')
                            ')', '}' -> if (brackets.isNotEmpty() && brackets.last() == text[i]) {
                                brackets.removeLast()
                            } else {
                                return null
                            }
                            operator -> if (brackets.isEmpty()) {
                                val leftOperand = text.substring(0, i)
                                val rightOperand = text.substring(i + 1)
                                if (leftOperand.trim().isNotEmpty() && rightOperand.trim().isNotEmpty()) {
                                    return listOf(text.substring(0, i), text.substring(i + 1))
                                }
                            }
                        }
                    }
                    return null
                }
                val calcVisualCharCount: (Int) -> Int = { it + 1 }
                val getSvg: (List<List<SvgTSpanElement>>) -> List<SvgTSpanElement> = { elements ->
                    val leftOperand = elements[0]
                    val rightOperand = elements[1]
                    leftOperand + listOf(SvgTSpanElement(operator.toString())) + rightOperand
                }
                val estimateWidth: (List<Node>, Font, (String, Font) -> Double) -> Double = { subnodes, font, widthCalculator ->
                    val leftOperandWidth = subnodes[0].estimateWidth(font, widthCalculator)
                    val rightOperandWidth = subnodes[1].estimateWidth(font, widthCalculator)
                    val plusWidth = widthCalculator(operator.toString(), font)
                    leftOperandWidth + plusWidth + rightOperandWidth
                }
                return Operator(::parseEntire, { null }, calcVisualCharCount, getSvg, estimateWidth)
            }

            private fun getIndexOperator(isSuperior: Boolean, symbol: String): Operator {
                val zeroWidthSpaceSymbol = "\u200B"
                val indentSymbol = " "
                val indentSizeFactor = 0.1
                val indexSizeFactor = 0.7
                val indexRelativeShift = 0.4
                val wordCharacters = "[a-zA-Z0-9${GREEK_LETTERS.values.joinToString("")}]"

                val regex = """\s*((?:-?${wordCharacters}+)*)$symbol(${wordCharacters}|\{[^$symbol\}]+\})\s*""".toRegex()
                val parseEntire: (String) -> List<String>? = { text ->
                    parseEntireByRegex(regex)(text)?.map { if (it.startsWith("{")) it.drop(1).dropLast(1) else it }
                }
                val parseStart: (String) -> Pair<List<String>, String>? = { text ->
                    parseStartByRegex(regex)(text)?.let { (start, end) ->
                        Pair(start.map { if (it.startsWith("{")) it.drop(1).dropLast(1) else it }, end)
                    }
                }
                val calcVisualCharCount: (Int) -> Int = { it }
                val getSvg: (List<List<SvgTSpanElement>>) -> List<SvgTSpanElement> = { elements ->
                    val shift = if (isSuperior) { "-" } else { "" }
                    val backShift = if (isSuperior) { "" } else { "-" }

                    val indentTSpan = SvgTSpanElement(indentSymbol).apply {
                        setAttribute(SvgTSpanElement.FONT_SIZE, "${indentSizeFactor}em")
                    }
                    val baseTSpanElements = elements[0]
                    val indexTSpanElements = elements[1].mapIndexed { i, element -> element.apply {
                        setAttribute(SvgTSpanElement.FONT_SIZE, "${indexSizeFactor}em")
                        if (i == 0) {
                            setAttribute(SvgTSpanElement.DY, "$shift${indexRelativeShift}em")
                        }
                    } }
                    // The following tspan element is used to restore the baseline after the index
                    // Restoring works only if there is some symbol after the index, so we use zeroWidthSpaceSymbol
                    // It could be considered as standard trick, see https://stackoverflow.com/a/65681504
                    // Attribute 'baseline-shift' is better suited for such usecase -
                    // it doesn't require to add an empty tspan at the end to restore the baseline (as 'dy').
                    // Sadly we can't use 'baseline-shift' as it is not supported by CairoSVG.
                    val restoreBaselineTSpan = SvgTSpanElement(zeroWidthSpaceSymbol).apply {
                        // Size of shift depends on the font size, and it should be equal to the superscript shift size
                        setAttribute(SvgTSpanElement.FONT_SIZE, "${indexSizeFactor}em")
                        setAttribute(SvgTSpanElement.DY, "$backShift${indexRelativeShift}em")
                    }

                    listOf(baseTSpanElements, listOf(indentTSpan), indexTSpanElements, listOf(restoreBaselineTSpan)).flatten()
                }
                val estimateWidth: (List<Node>, Font, (String, Font) -> Double) -> Double = { subnodes, font, widthCalculator ->
                    val baseWidth = subnodes[0].estimateWidth(font, widthCalculator)
                    val indexFontSize = (font.size * indexSizeFactor).roundToInt()
                    val indexFont = Font(font.family, indexFontSize, font.isBold, font.isItalic)
                    val indexWidth = subnodes[1].estimateWidth(indexFont, widthCalculator)
                    baseWidth + indexWidth
                }
                return Operator(parseEntire, parseStart, calcVisualCharCount, getSvg, estimateWidth)
            }

            private fun parseEntireByRegex(regex: Regex): (String) -> List<String>? {
                return { text ->
                    regex.matchEntire(text)?.let { match ->
                        match.groups.toList().drop(1).mapNotNull { it?.value }
                    }
                }
            }

            private fun parseStartByRegex(regex: Regex): (String) -> Pair<List<String>, String>? {
                return { text ->
                    ("^" + regex.pattern).toRegex().find(text)?.let { match ->
                        val start = match.groups.toList().drop(1).mapNotNull { it?.value }
                        val end = text.substring(match.range.last + 1)
                        Pair(start, end)
                    }
                }
            }
        }
    }

    companion object {
        val GREEK_LETTERS = mapOf(
            "Alpha" to "Α",
            "Beta" to "Β",
            "Gamma" to "Γ",
            "Delta" to "Δ",
            "Epsilon" to "Ε",
            "Zeta" to "Ζ",
            "Eta" to "Η",
            "Theta" to "Θ",
            "Iota" to "Ι",
            "Kappa" to "Κ",
            "Lambda" to "Λ",
            "Mu" to "Μ",
            "Nu" to "Ν",
            "Xi" to "Ξ",
            "Omicron" to "Ο",
            "Pi" to "Π",
            "Rho" to "Ρ",
            "Sigma" to "Σ",
            "Tau" to "Τ",
            "Upsilon" to "Υ",
            "Phi" to "Φ",
            "Chi" to "Χ",
            "Psi" to "Ψ",
            "Omega" to "Ω",
            "alpha" to "α",
            "beta" to "β",
            "gamma" to "γ",
            "delta" to "δ",
            "epsilon" to "ε",
            "zeta" to "ζ",
            "eta" to "η",
            "theta" to "θ",
            "iota" to "ι",
            "kappa" to "κ",
            "lambda" to "λ",
            "mu" to "μ",
            "nu" to "ν",
            "xi" to "ξ",
            "omicron" to "ο",
            "pi" to "π",
            "rho" to "ρ",
            "sigma" to "σ",
            "tau" to "τ",
            "upsilon" to "υ",
            "phi" to "φ",
            "chi" to "χ",
            "psi" to "ψ",
            "omega" to "ω",
        )
        val GREEK_LETTERS_REGEX = """\s*\\(?<letter>${GREEK_LETTERS.keys.joinToString("|")})\s*""".toRegex()

        fun parse(text: String): List<Pair<Term, IntRange>> {
            return extractFormulas(text).map { (formula, range) ->
                val text = GREEK_LETTERS_REGEX.replace(formula) { match ->
                    GREEK_LETTERS[match.groups["letter"]!!.value]!!
                }
                LatexTerm(Node.textToNode(text) ?: Leaf(text)) to range
            }.toList()
        }

        private fun extractFormulas(text: String): List<Pair<String, IntRange>> {
            val formulas = mutableListOf<Pair<String, IntRange>>()
            var formulaStart = 0
            for (i in text.indices.toList().dropLast(1)) {
                when (text.substring(i, i + 2)) {
                    "\\(" -> {
                        formulaStart = i + 2
                    }
                    "\\)" -> {
                        val formula = text.substring(formulaStart, i)
                        val range = IntRange(formulaStart - 2, i + 1)
                        formulas.add(Pair(formula, range))
                    }
                }
            }
            return formulas
        }
    }
}