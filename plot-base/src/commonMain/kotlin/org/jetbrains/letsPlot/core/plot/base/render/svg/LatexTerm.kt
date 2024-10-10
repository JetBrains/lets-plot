/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.svg.RichText.Term
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import kotlin.collections.plus
import kotlin.math.pow
import kotlin.math.roundToInt

internal class LatexTerm(
    private val node: Node
) : Term {
    override val visualCharCount: Int = node.visualCharCount
    override val svg: List<SvgTSpanElement> = node.svg

    override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
        node.estimateWidth(font, widthCalculator)

    companion object {
        fun parse(text: String): List<Pair<Term, IntRange>> {
            return extractFormulas(text).map { (formula, range) ->
                val text = formula.replace("-", "−") // Use minus sign instead of hyphen
                LatexTerm(Node.parse(Token.tokenize(text))) to range
            }.toList()
        }

        private fun extractFormulas(text: String): List<Pair<String, IntRange>> {
            val formulas = mutableListOf<Pair<String, IntRange>>()
            var formulaStart = 0
            for (i in 0 until text.length - 1) {
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

internal open class Token {
    data class Command(val name: String) : Token()
    object OpenBrace : Token()
    object CloseBrace : Token()
    object Superscript : Token()
    object Subscript : Token()
    object Space : Token()
    data class ExplicitSpace(val space: String) : Token() {
        companion object {
            val QUAD = ExplicitSpace(" ")
            val QQUAD = ExplicitSpace("  ")
            val COMMA = ExplicitSpace(" ")
            val COLON = ExplicitSpace(" ")
            val SPACE = ExplicitSpace(" ")
        }
    }
    data class Text(val content: String) : Token()

    private enum class ControlSymbol(val symbol: Char) {
        BACKSLASH('\\'),
        OPEN_BRACE('{'),
        CLOSE_BRACE('}'),
        SUPERSCRIPT('^'),
        SUBSCRIPT('_'),
        SPACE(' ');

        companion object {
            fun fromChar(char: Char): ControlSymbol? {
                return entries.find { it.symbol == char }
            }

            fun isSupOrSub(char: Char): Boolean {
                return fromChar(char) in listOf(SUPERSCRIPT, SUBSCRIPT)
            }
        }
    }

    companion object {
        fun tokenize(input: String): Sequence<Token> = sequence {
            var i = 0
            while (i < input.length) {
                val controlSymbol = ControlSymbol.fromChar(input[i])
                if (controlSymbol == null) {
                    val text = StringBuilder()
                    if (i > 0 && ControlSymbol.isSupOrSub(input[i - 1])) {
                        text.append(input[i])
                        i++
                    } else {
                        while (i < input.length && ControlSymbol.fromChar(input[i]) == null) {
                            text.append(input[i])
                            i++
                        }
                    }
                    yield(Text(text.toString()))
                    continue
                }
                when (controlSymbol) {
                    ControlSymbol.BACKSLASH -> {
                        val command = StringBuilder()
                        i++
                        while (i < input.length && (input[i].isLetter() || (command.isEmpty() && input[i] in ",: "))) {
                            command.append(input[i])
                            i++
                        }
                        when (command.toString()) {
                            "quad" -> yield(ExplicitSpace.QUAD)
                            "qquad" -> yield(ExplicitSpace.QQUAD)
                            "," -> yield(ExplicitSpace.COMMA)
                            ":" -> yield(ExplicitSpace.COLON)
                            " " -> yield(ExplicitSpace.SPACE)
                            else -> yield(Command(command.toString()))
                        }
                    }
                    ControlSymbol.OPEN_BRACE -> {
                        yield(OpenBrace)
                        i++
                    }
                    ControlSymbol.CLOSE_BRACE -> {
                        yield(CloseBrace)
                        i++
                    }
                    ControlSymbol.SUPERSCRIPT ->{
                        yield(Superscript)
                        i++
                    }
                    ControlSymbol.SUBSCRIPT ->{
                        yield(Subscript)
                        i++
                    }
                    ControlSymbol.SPACE -> {
                        yield(Space)
                        i++
                    }
                }
            }
        }
    }
}

internal abstract class Node {
    abstract val visualCharCount: Int
    abstract val svg: List<SvgTSpanElement>
    abstract fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double

    data class TextNode(val content: String) : Node() {
        override val visualCharCount: Int = content.length
        override val svg: List<SvgTSpanElement> = listOf(SvgTSpanElement(content))
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(content, font)
        }
    }
    data class GroupNode(val children: List<Node>) : Node() {
        override val visualCharCount: Int = children.sumOf { it.visualCharCount }
        override val svg: List<SvgTSpanElement> = children.flatMap { it.svg }
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return children.sumOf { it.estimateWidth(font, widthCalculator) }
        }
    }
    data class SuperscriptNode(val content: Node, val level: Int) : Node() {
        override val visualCharCount: Int = content.visualCharCount
        override val svg: List<SvgTSpanElement> = getSvgForIndexNode(content, level, isSuperior = true)
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
            estimateWidthForIndexNode(content, level, font, widthCalculator)
    }
    data class SubscriptNode(val content: Node, val level: Int) : Node() {
        override val visualCharCount: Int = content.visualCharCount
        override val svg: List<SvgTSpanElement> = getSvgForIndexNode(content, level, isSuperior = false)
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
            estimateWidthForIndexNode(content, level, font, widthCalculator)
    }

    companion object {
        private const val ZERO_WIDTH_SPACE_SYMBOL = "\u200B"
        private const val INDENT_SYMBOL = " "
        private const val INDENT_SIZE_FACTOR = 0.1
        private const val INDEX_SIZE_FACTOR = 0.7
        private const val INDEX_RELATIVE_SHIFT = 0.4

        private val GREEK_LETTERS = mapOf(
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
        private val OPERATIONS = mapOf(
            "pm" to "±",
            "mp" to "∓",
            "times" to "×",
            "div" to "÷",
            "cdot" to "·",
        )
        private val RELATIONS = mapOf(
            "leq" to "≤",
            "geq" to "≥",
            "neq" to "≠",
        )
        private val SYMBOLS = GREEK_LETTERS + OPERATIONS + RELATIONS

        fun parse(tokens: Sequence<Token>): Node {
            return parseGroup(tokens.iterator(), level = 0)
        }

        private fun parseGroup(iterator: Iterator<Token>, level: Int): GroupNode {
            val nodes = mutableListOf<Node>()
            while (iterator.hasNext()) {
                val token = iterator.next()
                when (token) {
                    is Token.Command -> nodes.add(parseCommand(token)) // For now, we just replace the command with its name if it's not a special symbol
                    is Token.OpenBrace -> nodes.add(parseGroup(iterator, level))
                    is Token.CloseBrace -> break
                    is Token.Superscript -> nodes.add(SuperscriptNode(parseSupOrSub(iterator, level + 1), level))
                    is Token.Subscript -> nodes.add(SubscriptNode(parseSupOrSub(iterator, level + 1), level))
                    is Token.Text -> nodes.add(TextNode(token.content))
                    is Token.Space -> continue
                    is Token.ExplicitSpace -> nodes.add(TextNode(token.space))
                }
            }
            return GroupNode(nodes)
        }

        private fun parseSupOrSub(iterator: Iterator<Token>, level: Int): Node {
            return when (val nextToken = iterator.next()) {
                is Token.OpenBrace -> parseGroup(iterator, level)
                is Token.Text -> TextNode(nextToken.content)
                is Token.Command -> parseCommand(nextToken)
                else -> throw IllegalArgumentException("Unexpected token after superscript or subscript")
            }
        }

        private fun parseCommand(token: Token.Command): Node {
            // For now, we just replace the command with its name if it's not a special symbol
            return TextNode(SYMBOLS.getOrElse(token.name) { "\\${token.name}" })
        }

        private fun getSvgForIndexNode(content: Node, level: Int, isSuperior: Boolean): List<SvgTSpanElement> {
            val (shift, backShift) = if (isSuperior) { "-" to "" } else { "" to "-" }

            val indentTSpan = SvgTSpanElement(INDENT_SYMBOL).apply {
                setAttribute(SvgTSpanElement.FONT_SIZE, "${INDENT_SIZE_FACTOR}em")
            }
            val indexSize = INDEX_SIZE_FACTOR.pow(level + 1)
            val indexTSpanElements = content.svg.mapIndexed { i, element -> element.apply {
                if (getAttribute(SvgTSpanElement.FONT_SIZE).get() == null) {
                    setAttribute(SvgTSpanElement.FONT_SIZE, "${indexSize}em")
                }
                if (i == 0) {
                    setAttribute(SvgTSpanElement.DY, "$shift${INDEX_RELATIVE_SHIFT}em")
                }
            } }
            // The following 'tspan' element is used to restore the baseline after the index
            // Restoring works only if there is some symbol after the index, so we use ZERO_WIDTH_SPACE_SYMBOL
            // It could be considered as standard trick, see https://stackoverflow.com/a/65681504
            // Attribute 'baseline-shift' is better suited for such use case -
            // it doesn't require to add an empty 'tspan' at the end to restore the baseline (as 'dy').
            // Sadly we can't use 'baseline-shift' as it is not supported by CairoSVG.
            val restoreBaselineTSpan = SvgTSpanElement(ZERO_WIDTH_SPACE_SYMBOL).apply {
                // Size of shift depends on the font size, and it should be equal to the superscript shift size
                setAttribute(SvgTSpanElement.FONT_SIZE, "${indexSize}em")
                setAttribute(SvgTSpanElement.DY, "$backShift${INDEX_RELATIVE_SHIFT}em")
            }

            return listOf(indentTSpan) + indexTSpanElements + restoreBaselineTSpan
        }

        private fun estimateWidthForIndexNode(content: Node, level: Int, font: Font, widthCalculator: (String, Font) -> Double): Double {
            val indexFontSize = (font.size * INDEX_SIZE_FACTOR.pow(level + 1)).roundToInt()
            val indexFont = Font(font.family, indexFontSize, font.isBold, font.isItalic)
            return content.estimateWidth(indexFont, widthCalculator)
        }
    }
}