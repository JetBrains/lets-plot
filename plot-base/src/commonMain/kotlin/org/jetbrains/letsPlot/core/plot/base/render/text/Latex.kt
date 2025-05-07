/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.fillTextTermGaps
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import kotlin.math.pow
import kotlin.math.roundToInt

internal object Latex {
    fun parse(text: String): List<RichTextNode> {
        val formulas = extractFormulas(text).map { (formula, range) ->
            val text = formula.replace("-", "−") // Use minus sign instead of hyphen
            LatexElement(parse(Token.tokenize(text))) to range
        }.toList()

        return fillTextTermGaps(text, formulas)
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
    private val MISCELLANEOUS = mapOf(
        "infty" to "∞",
    )
    private val SYMBOLS = GREEK_LETTERS + OPERATIONS + RELATIONS + MISCELLANEOUS

    private fun parse(tokens: Sequence<Token>): RichTextNode.Span {
        return parseGroup(tokens.iterator(), level = 0)
    }

    private fun parseGroup(iterator: Iterator<Token>, level: Int): GroupNode {
        val nodes = mutableListOf<RichTextNode.Span>()
        while (iterator.hasNext()) {
            when (val token = iterator.next()) {
                is Token.Command -> nodes.add(parseCommand(token, iterator, level))
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

    private fun parseSupOrSub(iterator: Iterator<Token>, level: Int): RichTextNode.Span {
        return when (val nextToken = iterator.next()) {
            is Token.OpenBrace -> parseGroup(iterator, level)
            is Token.Text -> TextNode(nextToken.content)
            is Token.Command -> parseCommand(nextToken, iterator, level)
            else -> throw IllegalArgumentException("Unexpected token after superscript or subscript")
        }
    }

    private fun parseCommand(token: Token.Command, iterator: Iterator<Token>, level: Int): RichTextNode.Span {
        return when (token.name) {
            // TODO: Refactor
            "frac" -> {
                val numeratorOpenBrace = iterator.next()
                require(numeratorOpenBrace is Token.OpenBrace) { "Expected '{' after '\\frac'" }
                val numerator = parseGroup(iterator, level)
                val denominatorOpenBrace = iterator.next()
                require(denominatorOpenBrace is Token.OpenBrace) { "Expected '{' after '\\frac{...}'" }
                val denominator = parseGroup(iterator, level)
                FractionNode(numerator, denominator)
            }
            // For other commands, we just replace the command with its name if it's not a special symbol
            else -> TextNode(SYMBOLS.getOrElse(token.name) { "\\${token.name}" })
        }
    }

    private fun getSvgForIndexNode(content: RichTextNode.Span, level: Int, isSuperior: Boolean, ctx: RenderState): List<SvgElement> {
        val (shift, backShift) = if (isSuperior) {
            "-" to ""
        } else {
            "" to "-"
        }

        val indentTSpan = SvgTSpanElement(INDENT_SYMBOL).apply {
            setAttribute(SvgTextContent.FONT_SIZE, "${INDENT_SIZE_FACTOR}em")
        }
        val indexSize = INDEX_SIZE_FACTOR.pow(level + 1)
        val indexTSpanElements = content.render(ctx).mapIndexed { i, element ->
            element.apply {
                if (getAttribute(SvgTextContent.FONT_SIZE).get() == null) {
                    setAttribute(SvgTextContent.FONT_SIZE, "${indexSize}em")
                }
                if (i == 0) {
                    setAttribute(SvgTextContent.TEXT_DY, "$shift${INDEX_RELATIVE_SHIFT}em")
                }
            }
        }
        // The following 'tspan' element is used to restore the baseline after the index
        // Restoring works only if there is some symbol after the index, so we use ZERO_WIDTH_SPACE_SYMBOL
        // It could be considered as standard trick, see https://stackoverflow.com/a/65681504
        // Attribute 'baseline-shift' is better suited for such use case -
        // it doesn't require to add an empty 'tspan' at the end to restore the baseline (as 'dy').
        // Sadly we can't use 'baseline-shift' as it is not supported by CairoSVG.
        val restoreBaselineTSpan = SvgTSpanElement(ZERO_WIDTH_SPACE_SYMBOL).apply {
            // Size of shift depends on the font size, and it should be equal to the superscript shift size
            setAttribute(SvgTextContent.FONT_SIZE, "${indexSize}em")
            setAttribute(SvgTextContent.TEXT_DY, "$backShift${INDEX_RELATIVE_SHIFT}em")
        }

        return listOf(ctx.apply(indentTSpan)) + indexTSpanElements + ctx.apply(restoreBaselineTSpan)
    }

    private fun estimateWidthForIndexNode(
        content: RichTextNode.Span,
        level: Int,
        font: Font,
        widthCalculator: (String, Font) -> Double
    ): Double {
        val indexFontSize = (font.size * INDEX_SIZE_FACTOR.pow(level + 1)).roundToInt()
        val indexFont = Font(font.family, indexFontSize, font.isBold, font.isItalic)
        return content.estimateWidth(indexFont, widthCalculator)
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

                        ControlSymbol.SUPERSCRIPT -> {
                            yield(Superscript)
                            i++
                        }

                        ControlSymbol.SUBSCRIPT -> {
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

    private class LatexElement(
        private val node: RichTextNode.Span
    ) : RichTextNode.Span {
        override val visualCharCount: Int = node.visualCharCount

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
            node.estimateWidth(font, widthCalculator)

        override fun render(context: RenderState): List<SvgElement> {
            return node.render(context)
        }
    }

    data class TextNode(val content: String) : RichTextNode.Span {
        override val visualCharCount: Int = content.length
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(content, font)
        }

        override fun render(context: RenderState): List<SvgElement> {
            return listOf(context.apply(SvgTSpanElement(content)))
        }
    }

    data class GroupNode(val children: List<RichTextNode.Span>) : RichTextNode.Span {
        override val visualCharCount: Int = children.sumOf { it.visualCharCount }
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return children.sumOf { it.estimateWidth(font, widthCalculator) }
        }

        override fun render(context: RenderState): List<SvgElement> {
            return children.flatMap { it.render(context) }
        }
    }

    data class SuperscriptNode(val content: RichTextNode.Span, val level: Int) : RichTextNode.Span {
        override val visualCharCount: Int = content.visualCharCount
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
            estimateWidthForIndexNode(content, level, font, widthCalculator)

        override fun render(context: RenderState): List<SvgElement> {
            return getSvgForIndexNode(content, level, isSuperior = true, ctx = context)
        }
    }

    data class SubscriptNode(val content: RichTextNode.Span, val level: Int) : RichTextNode.Span {
        override val visualCharCount: Int = content.visualCharCount
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
            estimateWidthForIndexNode(content, level, font, widthCalculator)

        override fun render(context: RenderState): List<SvgElement> {
            return getSvgForIndexNode(content, level, isSuperior = false, ctx = context)
        }
    }

    data class FractionNode(val numerator: RichTextNode.Span, val denominator: RichTextNode.Span) : RichTextNode.Span {
        override val visualCharCount: Int = numerator.visualCharCount + denominator.visualCharCount // TODO
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return numerator.estimateWidth(font, widthCalculator) + denominator.estimateWidth(font, widthCalculator) // TODO
        }

        override fun render(context: RenderState): List<SvgElement> {
            val result = numerator.render(context)
            return numerator.render(context) + denominator.render(context) // TODO
        }
    }
}
