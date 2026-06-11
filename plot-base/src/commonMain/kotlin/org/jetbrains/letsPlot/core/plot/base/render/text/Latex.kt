/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator
import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator.widthCalculator
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.wrap
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_ANCHOR_START
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

internal class Latex(
    private val font: Font
) {
    internal fun parse(text: String): List<RichTextNode> {
        val formulas = extractFormulas(text).map { (formula, range) ->
            val prettyFormula = formula.replace("-", "−") // Use minus sign instead of hyphen
            val node = parse(Token.tokenize(prettyFormula))
            val wrapper: RichTextNode.RichSpan = VectorLatexElement(node)
            wrapper to range
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
        return parseGroup(tokens.iterator(), level = 0)
    }

    private fun parseGroup(iterator: Iterator<Token>, level: Int): GroupNode {
        val nodes = mutableListOf<LatexNode>()
        while (iterator.hasNext()) {
            when (val token = iterator.next()) {
                is Token.Command -> nodes.add(parseCommand(token, iterator, level))
                is Token.OpenBrace -> nodes.add(parseGroup(iterator, level))
                is Token.CloseBrace -> break
                is Token.Superscript -> nodes.add(SuperscriptNode(parseSupOrSub(iterator, level + 1), level))
                is Token.Subscript -> nodes.add(SubscriptNode(parseSupOrSub(iterator, level + 1), level))
                is Token.Text -> nodes.add(TextNode(token.content, level))
                is Token.Space -> continue
                is Token.ExplicitSpace -> nodes.add(TextNode(token.space, level))
            }
        }
        return GroupNode(nodes, level)
    }

    private fun parseSupOrSub(iterator: Iterator<Token>, level: Int): LatexNode {
        return when (val nextToken = iterator.next()) {
            is Token.OpenBrace -> parseGroup(iterator, level)
            is Token.Text -> TextNode(nextToken.content, level)
            is Token.Command -> parseCommand(nextToken, iterator, level)
            else -> throw IllegalArgumentException("Unexpected token after superscript or subscript")
        }
    }

    private fun parseCommand(token: Token.Command, iterator: Iterator<Token>, level: Int): LatexNode {
        fun parseNArgs(n: Int): List<LatexNode> {
            val args = mutableListOf<LatexNode>()
            repeat(n) {
                require(iterator.next() is Token.OpenBrace) { "The formula cannot be parsed because the opening bracket '{' after the '${token.name}' command is missing" }
                if (!iterator.hasNext()) {
                    throw IllegalArgumentException("Expected $n arguments for command '${token.name}'")
                }
                val arg = parseGroup(iterator, level)
                args.add(arg)
            }
            return args
        }

        return when (token.name) {
            Token.Command.FRACTION -> {
                val (numerator, denominator) = parseNArgs(2)
                FractionNode(numerator, denominator, level)
            }
            // For other commands, we just replace the command with its name if it's not a special symbol
            else -> TextNode(SYMBOLS.getOrElse(token.name) { "\\${token.name}" }, level)
        }
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

    internal abstract inner class LatexNode(val children: List<LatexNode>, protected val level: Int) {
        abstract val visualCharCount: Int

        // Vector advance in pixels, using only LatexVectorFont em-advances. Drift-free against
        // the corresponding renderVectorGroup() output.
        open fun vectorWidth(font: Font): Double = children.sumOf { it.vectorWidth(font) }

        // Vertical line-box metrics in pixels for the vector rendering.
        open fun vectorMetrics(font: Font): LineBoxMetrics =
            LineBoxMetrics.mergeOnBaseline(
                metrics = children.map { it.vectorMetrics(font) },
                defaultIfEmpty = LineBoxMetrics.plainText(font)
            )

        // Render this node into an SvgGElement. All coordinates inside are pixels in the formula's
        // local frame: x=0 is the left edge, y=0 is the line baseline; ascenders are y<0.
        // The caller composes by translating the returned group.
        abstract fun renderVectorGroup(color: Color?): SvgGElement

        // Effective font size in pixels at this node's level.
        internal fun levelFontSize(font: Font): Double =
            font.size.toDouble() * INDEX_SIZE_FACTOR.pow(level)
    }

    // The formula wrapper placed into a rich-text line. Width comes from the same em advances used
    // to render the glyph paths, so there is no drift between measurement and rendering; unsupported
    // glyphs fall back per-box to a <text> run inside renderVectorGroup.
    internal inner class VectorLatexElement(val node: LatexNode) : RichTextNode.RichSpan() {
        override val visualCharCount: Int = node.visualCharCount

        override fun estimateWidth(font: Font): Double = node.vectorWidth(font)

        override fun estimateLineLayoutMetrics(font: Font): LineBoxMetrics = node.vectorMetrics(font)

        override fun render(context: RenderState, prefixWidth: Double): List<WrappedSvgElement<SvgElement>> {
            val group = SvgGElement().apply {
                addClass(VECTOR_FORMULA_CLASS)
                children().add(node.renderVectorGroup(context.color))

                // Invisible bbox guide. Downstream layout measures the rendered SVG bbox, not
                // estimateWidth. Glyph paths provide tight ink bounds and omit space glyphs, while
                // formula positioning uses the logical advance width. This guide makes the group
                // bbox match that advance box without painting anything.
                val formulaFont = this@Latex.font
                val width = node.vectorWidth(formulaFont)
                if (width > 0.0) {
                    val metrics = node.vectorMetrics(formulaFont)
                    val top = -metrics.topToBaseline
                    val bottom = metrics.bottomToBaseline
                    val guide = SvgPathElement().apply {
                        addClass(VECTOR_BBOX_CLASS)
                        setAttribute("d", "M0 $top L$width $top L$width $bottom L0 $bottom Z")
                        // Measured but not painted. Set fill="none" explicitly rather than leaving
                        // it unset: the raster renderer skips fill-less paths, but a browser paints
                        // a <path> with no fill using the SVG default (black / inherited text color),
                        // which would cover the formula. "none" is non-painting in every renderer
                        // while still contributing to the bbox that downstream layout measures.
                        fill().set(SvgColors.NONE)
                    }
                    children().add(guide)
                }
            }
            return listOf(group.wrap(x = prefixWidth))
        }
    }

    private inner class TextNode(val content: String, level: Int) : LatexNode(emptyList(), level) {
        override val visualCharCount: Int = content.length

        // A maximal run of characters that are all vector-supported or all unsupported. The
        // "supported vs not" decision lives only here (per the mental model), so both measurement
        // and rendering walk the same runs and can never drift apart.
        private inner class Run(val text: String, val supported: Boolean)

        private fun segments(): List<Run> {
            val runs = mutableListOf<Run>()
            var start = 0
            while (start < content.length) {
                val supported = LatexVectorFont.isSupported(content[start])
                var end = start + 1
                while (end < content.length && LatexVectorFont.isSupported(content[end]) == supported) {
                    end++
                }
                runs.add(Run(content.substring(start, end), supported))
                start = end
            }
            return runs
        }

        // The level font used by the legacy text estimator and fallback <text> paint.
        private fun nodeFontAtLevel(font: Font): Font {
            // Minor approximation: level font size is rounded to Int here, matching the legacy estimator; could be unified later.
            val sizePx = max(1, (font.size * INDEX_SIZE_FACTOR.pow(level)).roundToInt())
            return Font(font.family, sizePx, font.isBold, font.isItalic)
        }

        // The single shared per-run advance (px) used by BOTH vectorWidth and renderVectorGroup, so
        // box positions never depend on how a box is drawn. Supported runs sum vector em-advances
        // (identical to before); unsupported runs use the legacy text estimator at the level font.
        private fun runAdvancePx(run: Run, font: Font): Double {
            return if (run.supported) {
                run.text.sumOf { LatexVectorFont.advanceEm(it) } * levelFontSize(font)
            } else {
                widthCalculator(run.text, nodeFontAtLevel(font))
            }
        }

        override fun vectorWidth(font: Font): Double =
            segments().sumOf { runAdvancePx(it, font) }

        override fun vectorMetrics(font: Font): LineBoxMetrics {
            val sizePx = levelFontSize(font)
            return LineBoxMetrics(boxHeight = sizePx, topToBaseline = sizePx)
        }

        override fun renderVectorGroup(color: Color?): SvgGElement {
            val g = SvgGElement()
            val font = this@Latex.font
            val sizePx = levelFontSize(font)
            val unitsToPx = sizePx / LatexVectorFont.UPM.toDouble()
            var cursorPx = 0.0
            for (run in segments()) {
                if (run.supported) {
                    for (c in run.text) {
                        val glyph = LatexVectorFont.glyphOrNull(c) ?: continue
                        if (glyph.pathData != null) {
                            val path = SvgPathElement().apply {
                                setAttribute("d", glyph.pathData)
                                // Bake an explicit fill only when the surrounding RenderState provides one
                                // (markdown / \color spans, geom_text/label, tooltips). Otherwise leave the
                                // fill unset so the glyph inherits the effective text color: in browser SVG
                                // from the stylesheet class on the line element, in raster from the class
                                // resolved on the line's ancestor group (see SvgPathAttrMapping).
                                if (color != null) fillColor().set(color)
                                transform().set(
                                    SvgTransformBuilder()
                                        .translate(cursorPx, 0.0)
                                        .scale(unitsToPx)
                                        .build()
                                )
                            }
                            g.children().add(path)
                        }
                        cursorPx += glyph.advanceEm * sizePx
                    }
                } else {
                    // One <text> run per unsupported segment; not merged across sibling nodes. Could be optimized later.
                    // Draw at the same rounded level font the estimator used to reserve this run's width
                    // (runAdvancePx -> widthCalculator(nodeFontAtLevel)), so drawn size == reserved width.
                    val fallbackFont = nodeFontAtLevel(font)
                    val textEl = SvgTextElement().apply {
                        addClass(VECTOR_TEXT_CLASS)
                        addTSpan(SvgTSpanElement(run.text))
                        // Formula-local baseline: x = cursor, y = 0. Parent groups carry any
                        // sup/sub/fraction vertical offset by transforming this whole group.
                        x().set(cursorPx)
                        y().set(0.0)
                        setAttribute(SvgTextContent.TEXT_ANCHOR, SVG_TEXT_ANCHOR_START)
                        // Bake the full font explicitly so this element is self-contained and not
                        // re-sized by inherited/outer styling (see Label.applyStyle, Step 5). The
                        // size is the level font size; family / weight / italic come from the formula.
                        setAttribute(SvgTextContent.FONT_SIZE, "${fallbackFont.size}px")
                        setAttribute(SvgTextContent.FONT_FAMILY, fallbackFont.family.name)
                        if (fallbackFont.isBold) setAttribute(SvgTextContent.FONT_WEIGHT, "bold")
                        if (fallbackFont.isItalic) setAttribute(SvgTextContent.FONT_STYLE, "italic")
                        // Like the glyph paths: bake a fill only when explicitly provided, else inherit.
                        if (color != null) fillColor().set(color)
                    }
                    g.children().add(textEl)
                    cursorPx += runAdvancePx(run, font)
                }
            }
            return g
        }
    }

    private inner class GroupNode(children: List<LatexNode>, level: Int) : LatexNode(children, level) {
        override val visualCharCount: Int = children.sumOf { it.visualCharCount }

        override fun renderVectorGroup(color: Color?): SvgGElement {
            val g = SvgGElement()
            val font = this@Latex.font
            var cursorPx = 0.0
            for (child in children) {
                val childGroup = child.renderVectorGroup(color)
                if (cursorPx != 0.0) {
                    childGroup.transform().set(SvgTransformBuilder().translate(cursorPx, 0.0).build())
                }
                g.children().add(childGroup)
                cursorPx += child.vectorWidth(font)
            }
            return g
        }
    }

    // Shift a line box up by `shift` (for a superscript): the top grows and the bottom shrinks
    // (clamped at 0), mirroring the renderer's negative dy. Preserves LineBoxMetrics invariants.
    private fun LineBoxMetrics.raisedBy(shift: Double): LineBoxMetrics {
        val newTop = topToBaseline + shift
        val newBottom = maxOf(0.0, bottomToBaseline - shift)
        return LineBoxMetrics(boxHeight = newTop + newBottom, topToBaseline = newTop)
    }

    // Shift a line box down by `shift` (for a subscript): the bottom grows and the top shrinks
    // (clamped at 0), mirroring the renderer's positive dy. Preserves LineBoxMetrics invariants.
    private fun LineBoxMetrics.loweredBy(shift: Double): LineBoxMetrics {
        val newBottom = bottomToBaseline + shift
        val newTop = maxOf(0.0, topToBaseline - shift)
        return LineBoxMetrics(boxHeight = newTop + newBottom, topToBaseline = newTop)
    }

    private inner class SuperscriptNode(val content: LatexNode, level: Int) : LatexNode(listOf(content), level) {
        override val visualCharCount: Int = content.visualCharCount
        override fun vectorWidth(font: Font): Double = content.vectorWidth(font)
        override fun vectorMetrics(font: Font): LineBoxMetrics =
            content.vectorMetrics(font).raisedBy(INDEX_RELATIVE_SHIFT * content.levelFontSize(font))

        override fun renderVectorGroup(color: Color?): SvgGElement {
            val g = SvgGElement()
            val contentGroup = content.renderVectorGroup(color)
            val dyPx = -INDEX_RELATIVE_SHIFT * content.levelFontSize(this@Latex.font)
            contentGroup.transform().set(SvgTransformBuilder().translate(0.0, dyPx).build())
            g.children().add(contentGroup)
            return g
        }
    }

    private inner class SubscriptNode(val content: LatexNode, level: Int) : LatexNode(listOf(content), level) {
        override val visualCharCount: Int = content.visualCharCount
        override fun vectorWidth(font: Font): Double = content.vectorWidth(font)
        override fun vectorMetrics(font: Font): LineBoxMetrics =
            content.vectorMetrics(font).loweredBy(INDEX_RELATIVE_SHIFT * content.levelFontSize(font))

        override fun renderVectorGroup(color: Color?): SvgGElement {
            val g = SvgGElement()
            val contentGroup = content.renderVectorGroup(color)
            val dyPx = INDEX_RELATIVE_SHIFT * content.levelFontSize(this@Latex.font)
            contentGroup.transform().set(SvgTransformBuilder().translate(0.0, dyPx).build())
            g.children().add(contentGroup)
            return g
        }
    }

    // Nested fractions are not supported: numerator and denominator are assumed to contain non-fraction content.
    internal inner class FractionNode(
        private val numerator: LatexNode,
        private val denominator: LatexNode,
        level: Int
    ) : LatexNode(listOf(numerator, denominator), level) {
        private val barGlyphOffset = 0.25
        // Clearance between the bar and the nearest edge of numerator/denominator (when barBaselineShift == 0).
        private val fractionGap = 0.01
        // Bar rectangle thickness, em.
        private val barThickness = 0.06
        // Extra allowance below the numerator, in em, equal to the nominal
        // plain-text space below the baseline in the current layout model.
        private val numeratorBottomAllowance = 1.0 - TextMetricsEstimator.baselineRatio()
        // Shift the bar baseline below the original line baseline by this amount (em). Redistributes
        // gap from the numerator side to the denominator side without changing total fraction height.
        private val barBaselineShift = 0.1

        override val visualCharCount: Int = max(numerator.visualCharCount, denominator.visualCharCount)

        override fun vectorWidth(font: Font): Double =
            max(numerator.vectorWidth(font), denominator.vectorWidth(font))

        override fun vectorMetrics(font: Font): LineBoxMetrics {
            val numMetrics = numerator.vectorMetrics(font)
            val denomMetrics = denominator.vectorMetrics(font)
            val em = levelFontSize(font)
            val numBaselineShift = (barGlyphOffset + fractionGap + numeratorBottomAllowance) * em
            val denomBaselineShift = denomMetrics.topToBaseline + (fractionGap - barGlyphOffset) * em
            val topToBaseline = numMetrics.topToBaseline + numBaselineShift
            return LineBoxMetrics(
                boxHeight = topToBaseline + denomMetrics.bottomToBaseline + denomBaselineShift,
                topToBaseline = topToBaseline
            )
        }

        override fun renderVectorGroup(color: Color?): SvgGElement {
            val g = SvgGElement()
            val font = this@Latex.font
            val em = levelFontSize(font)

            val numWidth = numerator.vectorWidth(font)
            val denomWidth = denominator.vectorWidth(font)
            val fractionWidthPx = max(numWidth, denomWidth)

            val numGroup = numerator.renderVectorGroup(color)
            val denomGroup = denominator.renderVectorGroup(color)

            val numShiftX = (fractionWidthPx - numWidth) / 2.0
            val denomShiftX = (fractionWidthPx - denomWidth) / 2.0
            // Vertical positioning mirrors legacy FractionNode metrics.
            val numShiftY = -(barGlyphOffset + fractionGap + numeratorBottomAllowance) * em
            val denomTopToBaseline = denominator.vectorMetrics(font).topToBaseline
            val denomShiftY = denomTopToBaseline + (fractionGap - barGlyphOffset) * em

            numGroup.transform().set(SvgTransformBuilder().translate(numShiftX, numShiftY).build())
            denomGroup.transform().set(SvgTransformBuilder().translate(denomShiftX, denomShiftY).build())

            // Bar: rectangle centered at y = (-barGlyphOffset + barBaselineShift) em, matching
            // the visual position of the legacy en-dash glyph at its baseline shift.
            val barCenterY = (-barGlyphOffset + barBaselineShift) * em
            val barHalfThick = barThickness / 2.0 * em
            val barTop = barCenterY - barHalfThick
            val barBottom = barCenterY + barHalfThick
            val barPath = SvgPathElement().apply {
                setAttribute(
                    "d",
                    "M0 $barTop L$fractionWidthPx $barTop L$fractionWidthPx $barBottom L0 $barBottom Z"
                )
                // Like the glyph paths: bake a fill only when explicitly provided, else inherit.
                if (color != null) fillColor().set(color)
            }

            g.children().add(numGroup)
            g.children().add(denomGroup)
            g.children().add(barPath)
            return g
        }
    }

    internal fun fillTextTermGaps(
        text: String,
        specialTerms: List<Pair<RichTextNode.RichSpan, IntRange>>
    ): List<RichTextNode.RichSpan> {
        fun subtractRange(range: IntRange, toSubtract: List<IntRange>): List<IntRange> {
            if (toSubtract.isEmpty()) {
                return listOf(range)
            }

            val sortedToSubtract = toSubtract.sortedBy(IntRange::first)
            val firstRange = IntRange(range.first, sortedToSubtract.first().first - 1)
            val intermediateRanges = sortedToSubtract.windowed(2).map { (prevRange, nextRange) ->
                IntRange(prevRange.last + 1, nextRange.first - 1)
            }
            val lastRange = IntRange(sortedToSubtract.last().last + 1, range.last)

            return (listOf(firstRange) + intermediateRanges + listOf(lastRange)).filterNot(IntRange::isEmpty)
        }

        val textTerms = subtractRange(text.indices, specialTerms.map { (_, termLocation) -> termLocation })
            .map { pos -> RichTextNode.Text(text.substring(pos)) to pos }
        return (specialTerms + textTerms)
            .sortedBy { (_, termLocation) -> termLocation.first }
            .map { (term, _) -> term }
    }


    companion object {
        private const val INDEX_SIZE_FACTOR = 0.7
        private const val INDEX_RELATIVE_SHIFT = 0.4
        internal const val VECTOR_FORMULA_CLASS = "lp-latex-vector-formula"
        internal const val VECTOR_BBOX_CLASS = "lp-latex-vector-bbox"
        // Marks a fallback <text> run emitted for an unsupported glyph; its font is baked
        // explicitly so Label.applyStyle must not overwrite it (see Step 5).
        internal const val VECTOR_TEXT_CLASS = "lp-latex-vector-text"

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
        // If you add symbols here, also update the AWT `latex symbols` visual test and https://lets-plot.org/python/pages/latex.html.
        private val SYMBOLS = GREEK_LETTERS + OPERATIONS + RELATIONS + MISCELLANEOUS
    }
}
