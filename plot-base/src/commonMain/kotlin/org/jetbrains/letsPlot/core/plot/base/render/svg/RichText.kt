/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt

object RichText {
    fun toSvg(
        text: String,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1
    ): List<SvgTextElement> {
        val lines = parseText(text, wrapLength, maxLinesCount)

        return lines.map { line ->
            SvgTextElement().apply {
                line.flatMap(Term::svg).forEach(::addTSpan)
            }
        }
    }

    fun estimateWidth(
        text: String,
        font: Font,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        widthEstimator: (String, Font) -> Double,
    ): Double {
        return parseText(text, wrapLength, maxLinesCount)
            .maxOfOrNull { line -> line.sumOf { term -> term.estimateWidth(font, widthEstimator) } }
            ?: 0.0
    }

    private fun wrap(lines: List<List<Term>>, wrapLength: Int, maxLinesCount: Int): List<List<Term>> {
        val wrappedLines = lines.flatMap { line -> wrapLine(line, wrapLength) }
        return when {
            maxLinesCount < 0 -> wrappedLines
            wrappedLines.size < maxLinesCount -> wrappedLines
            else -> wrappedLines.dropLast(wrappedLines.size - maxLinesCount) + mutableListOf(mutableListOf(TextTerm("...")))
        }
    }

    private fun parseText(text: String, wrapLength: Int = -1, maxLinesCount: Int = -1): List<List<Term>> {
        val lines = text.split("\n")
            .map { line ->
                val specialTerms = FormulaTerm.parse(line) + LinkTerm.parse(line)
                if (specialTerms.isEmpty()) {
                    listOf(TextTerm(line))
                } else {
                    val textTerms = subtractRange(line.indices, specialTerms.map { (_, termLocation) -> termLocation })
                        .map { pos -> TextTerm(line.substring(pos)) to pos }
                    (specialTerms + textTerms)
                        .sortedBy { (_, termLocation) -> termLocation.first }
                        .map { (term, _) -> term }
                }
            }

        val wrappedLines = wrap(lines, wrapLength, maxLinesCount)
        return wrappedLines
    }

    private fun wrapLine(line: List<Term>, wrapLength: Int = -1): List<List<Term>> {
        if (wrapLength <= 0) {
            return listOf(line)
        }

        val wrappedLines = mutableListOf(mutableListOf<Term>())
        line.forEach { term ->
            val availableSpace = wrapLength - wrappedLines.last().sumOf(Term::visualCharCount)
            when {
                term.visualCharCount <= availableSpace -> wrappedLines.last().add(term)
                term.visualCharCount <= wrapLength -> wrappedLines.add(mutableListOf(term)) // no need to split
                term !is TextTerm -> wrappedLines.add(mutableListOf(term)) // can't fit in one line, but can't split power or link
                else -> { // split text
                    wrappedLines.last().takeIf { availableSpace > 0 }?.add(TextTerm(term.text.take(availableSpace)))
                    wrappedLines += term.text
                        .drop(availableSpace)
                        .chunked(wrapLength)
                        .map { mutableListOf(TextTerm(it)) }
                }
            }
        }

        return wrappedLines
    }

    private fun subtractRange(range: IntRange, toSubtract: List<IntRange>): List<IntRange> {
        val sortedToSubtract = toSubtract.sortedBy(IntRange::first)
        val firstRange = IntRange(range.first, sortedToSubtract.first().first - 1)
        val intermediateRanges = sortedToSubtract.windowed(2).map { (prevRange, nextRange) ->
            IntRange(prevRange.last + 1, nextRange.first - 1)
        }
        val lastRange = IntRange(sortedToSubtract.last().last + 1, range.last)

        return (listOf(firstRange) + intermediateRanges + listOf(lastRange)).filterNot(IntRange::isEmpty)
    }

    private class TextTerm(
        val text: String,
    ) : Term {
        override val visualCharCount: Int = text.length
        override val svg: List<SvgTSpanElement> = listOf(SvgTSpanElement(text))

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }
    }

    private class LinkTerm private constructor(
        private val text: String,
        private val href: String,
    ) : Term {
        override val visualCharCount: Int = text.length
        override val svg: List<SvgTSpanElement> = listOf(
            SvgTSpanElement(text).apply {
                fillColor().set(Colors.forName("blue")) // TODO: do not hardcode color
                setAttribute("lp-href", href)
            })

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }

        companion object {
            private val anchorTagRegex = "<a\\s+[^>]*href=\"(?<href>[^\"]*)\"[^>]*>(?<text>[^<]*)</a>".toRegex()

            fun parse(text: String): List<Pair<Term, IntRange>> {
                return anchorTagRegex.findAll(text)
                    .map { match ->
                        val (href, label) = match.destructured
                        LinkTerm(label, href) to match.range
                    }.toList()
            }
        }
    }

    private class FormulaTerm(
        private val root: Leaf
    ) : Term {
        override val visualCharCount: Int = root.visualCharCount
        override val svg: List<SvgTSpanElement> = root.svg

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return root.estimateWidth(font, widthCalculator)
        }

        private class InnerLeaf(val operator: Operator, val subleafs: List<Leaf>) : Leaf {
            override val visualCharCount: Int = operator.calcVisualCharCount(subleafs.sumOf { it.visualCharCount })
            override val svg: List<SvgTSpanElement> = operator.getSvg(subleafs.map { it.svg })

            override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
                return operator.estimateWidth(subleafs, font, widthCalculator)
            }
        }

        private class TerminalLeaf(val body: String) : Leaf {
            override val visualCharCount: Int = body.length
            override val svg: List<SvgTSpanElement> = listOf(SvgTSpanElement(body))

            override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
                return widthCalculator(body, font)
            }
        }

        private interface Leaf : Term {
            companion object {
                fun strToLeaf(body: String): Leaf {
                    val operators: List<Operator> = Operator.operators
                    return operators.mapNotNull { operator ->
                        operator.parse(body.trim())?.map { strToLeaf(it) }?.let { leafs ->
                            InnerLeaf(operator, leafs)
                        }
                    }.firstOrNull() ?: TerminalLeaf(body.trim())
                }
            }
        }

        private class Operator(
            val parse: (String) -> List<String>?,
            val calcVisualCharCount: (Int) -> Int,
            val getSvg: (List<List<SvgTSpanElement>>) -> List<SvgTSpanElement>,
            val estimateWidth: (List<Leaf>, Font, (String, Font) -> Double) -> Double
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

                private fun getBracketsOperator(): Operator {
                    val parse: (String) -> List<String>? = { text ->
                        val regex = """\s*\(([^\)]+)\)\s*""".toRegex()
                        regex.matchEntire(text)?.let { match ->
                            val t = match.groups.toList().drop(1)
                            t.mapNotNull { it?.value }
                        }
                    }
                    val calcVisualCharCount: (Int) -> Int = { it + 2 }
                    val getSvg: (List<List<SvgTSpanElement>>) -> List<SvgTSpanElement> = { elements ->
                        val innerElements = elements[0]
                        listOf(SvgTSpanElement("(")) + innerElements + listOf(SvgTSpanElement(")"))
                    }
                    val estimateWidth: (List<Leaf>, Font, (String, Font) -> Double) -> Double = { subleafs, font, widthCalculator ->
                        val innerWidth = subleafs[0].estimateWidth(font, widthCalculator)
                        val bracketsWidth = widthCalculator("()", font)
                        innerWidth + bracketsWidth
                    }
                    return Operator(parse, calcVisualCharCount, getSvg, estimateWidth)
                }

                private fun getBinaryOperator(operator: Char): Operator {
                    fun parse(text: String): List<String>? {
                        var brackets = 0
                        var curlyBrackets = 0
                        for (i in text.indices) {
                            when (text[i]) {
                                '(' -> brackets++
                                ')' -> brackets--
                                '{' -> curlyBrackets++
                                '}' -> curlyBrackets--
                                operator -> if (brackets == 0 && curlyBrackets == 0) {
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
                    val estimateWidth: (List<Leaf>, Font, (String, Font) -> Double) -> Double = { subleafs, font, widthCalculator ->
                        val leftOperandWidth = subleafs[0].estimateWidth(font, widthCalculator)
                        val rightOperandWidth = subleafs[1].estimateWidth(font, widthCalculator)
                        val plusWidth = widthCalculator(operator.toString(), font)
                        leftOperandWidth + plusWidth + rightOperandWidth
                    }
                    return Operator(::parse, calcVisualCharCount, getSvg, estimateWidth)
                }

                private fun getIndexOperator(isSuperior: Boolean, symbol: String): Operator {
                    val zeroWidthSpaceSymbol = "\u200B"
                    val indentSymbol = " "
                    val indentSizeFactor = 0.1
                    val indexSizeFactor = 0.7
                    val indexRelativeShift = 0.4
                    val wordCharacters = "[a-zA-Z0-9${GREEK_LETTERS.values.joinToString()}]"

                    val parse: (String) -> List<String>? = { text ->
                        val regex = """\s*((?:-?${wordCharacters}+)*)$symbol\{?([^$symbol\}]+)\}?\s*""".toRegex()
                        regex.matchEntire(text)?.let { match ->
                            match.groups.toList().drop(1).mapNotNull { it?.value }
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
                    val estimateWidth: (List<Leaf>, Font, (String, Font) -> Double) -> Double = { subleafs, font, widthCalculator ->
                        val baseWidth = subleafs[0].estimateWidth(font, widthCalculator)
                        val indexFontSize = (font.size * indexSizeFactor).roundToInt()
                        val indexFont = Font(font.family, indexFontSize, font.isBold, font.isItalic)
                        val indexWidth = subleafs[1].estimateWidth(indexFont, widthCalculator)
                        baseWidth + indexWidth
                    }
                    return Operator(parse, calcVisualCharCount, getSvg, estimateWidth)
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
                    val body = GREEK_LETTERS_REGEX.replace(formula) { match ->
                        GREEK_LETTERS[match.groups["letter"]!!.value]!!
                    }
                    FormulaTerm(Leaf.strToLeaf(body.trim())) to range
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

    private interface Term {
        val visualCharCount: Int // in chars, used for line wrapping
        val svg: List<SvgTSpanElement>

        fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double
    }
}