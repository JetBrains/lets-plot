/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode.RichSvgElement
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.fillTextTermGaps
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

internal class Latex(
    private val font: Font,
    private val widthCalculator: (String, Font) -> Double
) {
    fun parse(text: String): List<RichTextNode> {
        val formulas = extractFormulas(text).map { (formula, range) ->
            val prettyFormula = formula.replace("-", "−") // Use minus sign instead of hyphen
            LatexElement(parse(Token.tokenize(prettyFormula))) to range
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

    private fun parse(tokens: Sequence<Token>): LatexNode {
        return parseGroup(tokens.iterator(), level = 0, emptyList())
    }

    private fun parseGroup(iterator: Iterator<Token>, level: Int, previousLatexNodes: List<LatexNode>): GroupNode {
        val nodes = mutableListOf<LatexNode>()
        while (iterator.hasNext()) {
            when (val token = iterator.next()) {
                is Token.Command -> nodes.add(parseCommand(token, iterator, level, previousLatexNodes + nodes.toList()))
                is Token.OpenBrace -> nodes.add(parseGroup(iterator, level, previousLatexNodes + nodes.toList()))
                is Token.CloseBrace -> break
                is Token.Superscript -> nodes.add(SuperscriptNode(parseSupOrSub(iterator, level + 1, previousLatexNodes + nodes.toList()), level))
                is Token.Subscript -> nodes.add(SubscriptNode(parseSupOrSub(iterator, level + 1, previousLatexNodes + nodes.toList()), level))
                is Token.Text -> nodes.add(TextNode(token.content))
                is Token.Space -> continue
                is Token.ExplicitSpace -> nodes.add(TextNode(token.space))
            }
        }
        return GroupNode(nodes)
    }

    private fun parseSupOrSub(iterator: Iterator<Token>, level: Int, previousLatexNodes: List<LatexNode>): LatexNode {
        return when (val nextToken = iterator.next()) {
            is Token.OpenBrace -> parseGroup(iterator, level, previousLatexNodes)
            is Token.Text -> TextNode(nextToken.content)
            is Token.Command -> parseCommand(nextToken, iterator, level, previousLatexNodes)
            else -> throw IllegalArgumentException("Unexpected token after superscript or subscript")
        }
    }

    private fun parseCommand(token: Token.Command, iterator: Iterator<Token>, level: Int, previousLatexNodes: List<LatexNode>): LatexNode {
        fun parseNArgs(n: Int): List<LatexNode> {
            val args = mutableListOf<LatexNode>()
            repeat(n) {
                require(iterator.next() is Token.OpenBrace) { "The formula cannot be parsed because the opening bracket '{' after the '${token.name}' command is missing" }
                if (!iterator.hasNext()) {
                    throw IllegalArgumentException("Expected $n arguments for command '${token.name}'")
                }
                val arg = parseGroup(iterator, level, previousLatexNodes + args)
                args.add(arg)
            }
            return args
        }

        return when (token.name) {
            Token.Command.FRACTION -> {
                val (numerator, denominator) = parseNArgs(2)
                FractionNode(numerator, denominator)
            }
            // For other commands, we just replace the command with its name if it's not a special symbol
            else -> TextNode(SYMBOLS.getOrElse(token.name) { "\\${token.name}" })
        }
    }

    private fun getSvgForIndexNode(content: LatexNode, level: Int, isSuperior: Boolean, ctx: RenderState, previousNodes: List<RichTextNode.Span>): List<RichSvgElement> {
        val (shift, backShift) = if (isSuperior) {
            "-" to ""
        } else {
            "" to "-"
        }

        val indentTSpan = RichSvgElement(ctx.apply(SvgTSpanElement(INDENT_SYMBOL).apply {
            setAttribute(SvgTextContent.FONT_SIZE, "${INDENT_SIZE_FACTOR}em")
        }))
        val indexSize = INDEX_SIZE_FACTOR.pow(level + 1)
        // It is an analog of restoreBaselineTSpan, but for the initial shifting
        // This is necessary for more complex formulas in which the index starts from another shift
        val setBaselineTSpan = RichSvgElement(ctx.apply(SvgTSpanElement(ZERO_WIDTH_SPACE_SYMBOL).apply {
            // Size of shift depends on the font size, and it should be equal to the superscript/subscript shift size
            setAttribute(SvgTextContent.FONT_SIZE, "${indexSize}em")
            setAttribute(SvgTextContent.TEXT_DY, "$shift${INDEX_RELATIVE_SHIFT}em")
        }))
        val indexTSpanElements = content.toSvg(ctx, previousNodes).mapIndexed { i, richElement ->
            richElement.element.apply {
                if (getAttribute(SvgTextContent.FONT_SIZE).get() == null) {
                    setAttribute(SvgTextContent.FONT_SIZE, "${indexSize}em")
                }
            }.let { RichSvgElement(it, richElement.x) }
        }
        // The following 'tspan' element is used to restore the baseline after the index
        // Restoring works only if there is some symbol after the index, so we use ZERO_WIDTH_SPACE_SYMBOL
        // It could be considered as standard trick, see https://stackoverflow.com/a/65681504
        // Attribute 'baseline-shift' is better suited for such use case -
        // it doesn't require to add an empty 'tspan' at the end to restore the baseline (as 'dy').
        // Sadly we can't use 'baseline-shift' as it is not supported by CairoSVG.
        val restoreBaselineTSpan = RichSvgElement(ctx.apply(SvgTSpanElement(ZERO_WIDTH_SPACE_SYMBOL).apply {
            // Size of shift depends on the font size, and it should be equal to the superscript/subscript shift size
            setAttribute(SvgTextContent.FONT_SIZE, "${indexSize}em")
            setAttribute(SvgTextContent.TEXT_DY, "$backShift${INDEX_RELATIVE_SHIFT}em")
        }))

        return listOf(indentTSpan, setBaselineTSpan) + indexTSpanElements + restoreBaselineTSpan
    }

    private fun estimateWidthForIndexNode(content: LatexNode, level: Int): Double {
        val indexFontSize = (font.size * INDEX_SIZE_FACTOR.pow(level + 1)).roundToInt()
        val indexFont = Font(font.family, indexFontSize, font.isBold, font.isItalic)
        return content.estimateWidth(indexFont, widthCalculator)
    }


    private open class Token {
        data class Command(val name: String) : Token() {
            companion object {
                const val FRACTION = "frac"
            }
        }
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

    internal abstract inner class LatexNode(val children: List<LatexNode>) : RichTextNode.Span() {
        fun flatListOfAllChildren(): List<LatexNode> {
            fun getChildrenRecursively(nodes: List<LatexNode>): List<LatexNode> {
                return nodes.flatMap { listOf(it) + getChildrenRecursively(it.children) }
            }
            return listOf(this) + getChildrenRecursively(children)
        }
    }

    internal inner class LatexElement(val node: LatexNode) : RichTextNode.Span() {
        override val visualCharCount: Int = node.visualCharCount

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
            node.estimateWidth(font, widthCalculator)

        override fun toSvg(context: RenderState, previousNodes: List<RichTextNode.Span>): List<RichSvgElement> {
            return node.toSvg(context, previousNodes)
        }
    }

    private inner class TextNode(val content: String) : LatexNode(emptyList()) {
        override val visualCharCount: Int = content.length
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(content, font)
        }

        override fun toSvg(context: RenderState, previousNodes: List<RichTextNode.Span>): List<RichSvgElement> {
            return listOf(RichSvgElement(context.apply(SvgTSpanElement(content))))
        }
    }

    private inner class GroupNode(children: List<LatexNode>) : LatexNode(children) {
        override val visualCharCount: Int = children.sumOf { it.visualCharCount }
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return children.sumOf { it.estimateWidth(font, widthCalculator) }
        }

        override fun toSvg(context: RenderState, previousNodes: List<RichTextNode.Span>): List<RichSvgElement> {
            val richElements = mutableListOf<RichSvgElement>()
            val previousLatexNodes = mutableListOf<LatexNode>()
            for (child in children) {
                richElements.addAll(child.toSvg(context, previousNodes + previousLatexNodes.toList()))
                previousLatexNodes.add(child)
            }
            return richElements
        }
    }

    private inner class SuperscriptNode(val content: LatexNode, val level: Int) : LatexNode(listOf(content)) {
        override val visualCharCount: Int = content.visualCharCount
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
            estimateWidthForIndexNode(content, level)

        override fun toSvg(context: RenderState, previousNodes: List<RichTextNode.Span>): List<RichSvgElement> {
            return getSvgForIndexNode(content, level, isSuperior = true, ctx = context, previousNodes = previousNodes)
        }
    }

    private inner class SubscriptNode(val content: LatexNode, val level: Int) : LatexNode(listOf(content)) {
        override val visualCharCount: Int = content.visualCharCount
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
            estimateWidthForIndexNode(content, level)

        override fun toSvg(context: RenderState, previousNodes: List<RichTextNode.Span>): List<RichSvgElement> {
            return getSvgForIndexNode(content, level, isSuperior = false, ctx = context, previousNodes = previousNodes)
        }
    }

    internal inner class FractionNode(
        private val numerator: LatexNode,
        private val denominator: LatexNode
    ) : LatexNode(listOf(numerator, denominator)) {
        override val visualCharCount: Int = max(numerator.visualCharCount, denominator.visualCharCount)
        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return max(numerator.estimateWidth(font, widthCalculator), denominator.estimateWidth(font, widthCalculator))
        }

        override fun toSvg(context: RenderState, previousNodes: List<RichTextNode.Span>): List<RichSvgElement> {
            val prefixWidth = previousNodes.sumOf { it.estimateWidth(font, widthCalculator) }
            val fractionWidth = estimateWidth(font, widthCalculator)
            val fractionCenter = prefixWidth + fractionWidth / 2.0
            val fractionBarWidth = TextNode(FRACTION_BAR_SYMBOL).estimateWidth(font, widthCalculator)
            val fractionBarLength = (fractionWidth / fractionBarWidth).roundToInt()
            val numeratorTSpanElements = numerator.toSvg(context, previousNodes).mapIndexed { i, richElement ->
                richElement.element.apply {
                    setAttribute(SvgTextContent.TEXT_ANCHOR, "middle")
                    if (i == 0) {
                        setAttribute(SvgTextContent.TEXT_DY, "-${FRACTION_RELATIVE_SHIFT}em")
                    }
                }.let { RichSvgElement(it, x = if (i == 0) { fractionCenter } else { richElement.x }) }
            }
            val denominatorTSpanElements = denominator.toSvg(context, previousNodes).mapIndexed { i, richElement ->
                richElement.element.apply {
                    setAttribute(SvgTextContent.TEXT_ANCHOR, "middle")
                    if (i == 0) {
                        setAttribute(SvgTextContent.TEXT_DY, "${2 * FRACTION_RELATIVE_SHIFT}em")
                    }
                }.let { RichSvgElement(it, x = if (i == 0) { fractionCenter } else { richElement.x }) }
            }
            val fractionBarTSpanElement = RichSvgElement(context.apply(SvgTSpanElement(FRACTION_BAR_SYMBOL.repeat(fractionBarLength)).apply {
                setAttribute(SvgTextContent.TEXT_DY, "-${FRACTION_RELATIVE_SHIFT}em")
                setAttribute(SvgTextContent.TEXT_ANCHOR, "middle")
            }), x = fractionCenter)
            val restoreBaselineTSpan = RichSvgElement(context.apply(SvgTSpanElement(ZERO_WIDTH_SPACE_SYMBOL).apply {
                setAttribute(SvgTextContent.TEXT_ANCHOR, "start")
            }), x = prefixWidth + fractionWidth)
            return numeratorTSpanElements + denominatorTSpanElements + listOf(fractionBarTSpanElement, restoreBaselineTSpan)
        }
    }

    companion object {
        private const val ZERO_WIDTH_SPACE_SYMBOL = "\u200B"
        private const val INDENT_SYMBOL = " "
        private const val INDENT_SIZE_FACTOR = 0.1
        private const val INDEX_SIZE_FACTOR = 0.7
        private const val INDEX_RELATIVE_SHIFT = 0.4
        private const val FRACTION_RELATIVE_SHIFT = 0.5
        private const val FRACTION_BAR_SYMBOL = "–"

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
    }
}
