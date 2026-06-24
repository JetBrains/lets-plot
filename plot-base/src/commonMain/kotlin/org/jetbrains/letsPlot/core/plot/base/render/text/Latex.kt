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
                is Token.ExplicitSpace -> nodes.add(SpaceNode(token.em, token.visualCharCount, level))
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
            // Other commands: a special symbol if known, else the literal command name.
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
        data class ExplicitSpace(val em: Double, val visualCharCount: Int = 1) : Token() {
            companion object {
                val QUAD = ExplicitSpace(1.0)
                val QQUAD = ExplicitSpace(2.0, visualCharCount = 2)
                val COMMA = ExplicitSpace(3.0 / 18.0)
                val COLON = ExplicitSpace(4.0 / 18.0)
                val SPACE = ExplicitSpace(6.0 / 18.0)
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
                            if (i < input.length && input[i] in ",: ") {
                                command.append(input[i])
                                i++
                            } else {
                                while (i < input.length && input[i].isLetter()) {
                                    command.append(input[i])
                                    i++
                                }
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

        // Advance in px from LatexVectorFont em-advances; must match renderVectorGroup().
        open fun vectorWidth(font: Font): Double = children.sumOf { it.vectorWidth(font) }

        open fun vectorMetrics(font: Font): LineBoxMetrics =
            LineBoxMetrics.mergeOnBaseline(
                metrics = children.map { it.vectorMetrics(font) },
                defaultIfEmpty = LineBoxMetrics.plainText(font)
            )

        // Renders into a group in the formula-local frame: x=0 left edge, y=0 baseline, ascenders y<0.
        // The caller composes by translating the returned group.
        abstract fun renderVectorGroup(color: Color?, font: Font): SvgGElement

        // Effective font size in pixels at this node's level.
        internal fun levelFontSize(font: Font): Double =
            font.size.toDouble() * INDEX_SIZE_FACTOR.pow(level)
    }

    // Wraps a parsed formula as a rich-text span. Measurement and rendering share the same em
    // advances (no drift); unsupported glyphs fall back to a <text> run in renderVectorGroup.
    internal inner class VectorLatexElement(val node: LatexNode) : RichTextNode.RichSpan() {
        override val visualCharCount: Int = node.visualCharCount

        override fun estimateWidth(font: Font): Double = node.vectorWidth(effective(font))

        override fun estimateLineLayoutMetrics(font: Font): LineBoxMetrics = node.vectorMetrics(effective(font))

        override fun render(context: RenderState, prefixWidth: Double): List<WrappedSvgElement<SvgElement>> {
            val effectiveFont = effective(this@Latex.font)
            val group = SvgGElement().apply {
                addClass(LatexVectorClasses.FORMULA_CLASS)
                children().add(node.renderVectorGroup(context.color, effectiveFont))

                // Invisible guide making the group's measured bbox equal the logical advance box:
                // downstream layout measures bbox, but glyph paths give tight ink bounds and omit spaces.
                // TODO: Remove once bbox consumers (TooltipBox) size formulas from analytic metrics instead of measured bbox.
                val formulaFont = effectiveFont
                val width = node.vectorWidth(formulaFont)
                if (width > 0.0) {
                    val metrics = node.vectorMetrics(formulaFont)
                    val top = -metrics.topToBaseline
                    val bottom = metrics.bottomToBaseline
                    val guide = SvgPathElement().apply {
                        addClass(VECTOR_BBOX_CLASS)
                        setAttribute("d", "M0 $top L$width $top L$width $bottom L0 $bottom Z")
                        // fill="none", not unset: a browser paints an unset <path> with the default
                        // black, covering the formula. "none" never paints yet still adds to the bbox.
                        fill().set(SvgColors.NONE)
                    }
                    children().add(guide)
                }
            }
            return listOf(group.wrap(x = prefixWidth))
        }
    }

    private inner class SpaceNode(private val emWidth: Double, override val visualCharCount: Int, level: Int) :
        LatexNode(emptyList(), level) {
        override fun vectorWidth(font: Font): Double = emWidth * levelFontSize(font)

        override fun vectorMetrics(font: Font): LineBoxMetrics {
            val sizePx = levelFontSize(font)
            return LineBoxMetrics(boxHeight = sizePx, topToBaseline = sizePx)
        }

        override fun renderVectorGroup(color: Color?, font: Font): SvgGElement = SvgGElement()
    }

    private inner class TextNode(val content: String, level: Int) : LatexNode(emptyList(), level) {
        override val visualCharCount: Int = content.length

        override fun vectorWidth(font: Font): Double =
            segments(font).sumOf { runAdvancePx(it, font) }

        override fun vectorMetrics(font: Font): LineBoxMetrics {
            val sizePx = levelFontSize(font)
            return LineBoxMetrics(boxHeight = sizePx, topToBaseline = sizePx)
        }

        override fun renderVectorGroup(color: Color?, font: Font): SvgGElement {
            val g = SvgGElement()
            val sizePx = levelFontSize(font)
            val unitsToPx = sizePx / LatexVectorFont.UPM.toDouble()
            var cursorPx = 0.0
            for (run in segments(font)) {
                if (run.supported) {
                    // Supported glyphs are painted as filled outlines, not text. In raster backends (ImageMagick)
                    // a filled outline looks a bit heavier than the same glyph as text at small sizes — known limitation, not a bug.
                    for (c in run.text) {
                        val glyph = LatexVectorFont.glyphOrNull(c, font.isBold, font.isItalic) ?: continue
                        if (glyph.pathData != null) {
                            val path = SvgPathElement().apply {
                                setAttribute("d", glyph.pathData)
                                // Bake a fill only when RenderState provides one; else leave it unset so the
                                // glyph inherits the effective text color (browser: line's CSS class;
                                // raster: ancestor group, see SvgPathAttrMapping).
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
                    // Draw at the same rounded level font runAdvancePx used to reserve this run's
                    // width, so drawn size == reserved width.
                    val fallbackFont = nodeFontAtLevel(font)
                    val textEl = SvgTextElement().apply {
                        addClass(VECTOR_TEXT_CLASS)
                        addTSpan(SvgTSpanElement(run.text))
                        // Formula-local baseline: x = cursor, y = 0; parents apply any sup/sub/fraction offset.
                        x().set(cursorPx)
                        y().set(0.0)
                        setAttribute(SvgTextContent.TEXT_ANCHOR, SVG_TEXT_ANCHOR_START)
                        // Bake the full font so inherited/outer styling can't re-size it.
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

        private fun segments(font: Font): List<Run> {
            val runs = mutableListOf<Run>()
            var start = 0
            while (start < content.length) {
                val supported = LatexVectorFont.isSupported(content[start], font.isBold, font.isItalic)
                var end = start + 1
                while (end < content.length && LatexVectorFont.isSupported(content[end], font.isBold, font.isItalic) == supported) {
                    end++
                }
                runs.add(Run(content.substring(start, end), supported))
                start = end
            }
            return runs
        }

        // Shared per-run advance (px) used by both vectorWidth and renderVectorGroup, so a box's
        // position never depends on how it's drawn. Supported: vector em-advances; unsupported: legacy estimator.
        private fun runAdvancePx(run: Run, font: Font): Double {
            return if (run.supported) {
                run.text.sumOf { LatexVectorFont.advanceEm(it, font.isBold, font.isItalic) } * levelFontSize(font)
            } else {
                widthCalculator(run.text, nodeFontAtLevel(font))
            }
        }

        // The level font used by the legacy text estimator and fallback <text> paint.
        private fun nodeFontAtLevel(font: Font): Font {
            val sizePx = max(1, (font.size * INDEX_SIZE_FACTOR.pow(level)).roundToInt())
            return Font(font.family, sizePx, font.isBold, font.isItalic)
        }

        // A maximal run of all-supported or all-unsupported chars. The supported-or-not decision
        // lives only here, so measurement and rendering walk identical runs and can't drift.
        private inner class Run(val text: String, val supported: Boolean)
    }

    private inner class GroupNode(children: List<LatexNode>, level: Int) : LatexNode(children, level) {
        override val visualCharCount: Int = children.sumOf { it.visualCharCount }

        override fun vectorWidth(font: Font): Double =
            children.sumOf { it.vectorWidth(font) } +
                    children.zipWithNext().sumOf { (left, right) -> interAtomGap(left, right, font) }

        override fun renderVectorGroup(color: Color?, font: Font): SvgGElement {
            val g = SvgGElement()
            var cursorPx = 0.0
            var previousChild: LatexNode? = null
            for (child in children) {
                previousChild?.let { cursorPx += interAtomGap(it, child, font) }
                val childGroup = child.renderVectorGroup(color, font)
                if (cursorPx != 0.0) {
                    childGroup.transform().set(SvgTransformBuilder().translate(cursorPx, 0.0).build())
                }
                g.children().add(childGroup)
                cursorPx += child.vectorWidth(font)
                previousChild = child
            }
            return g
        }

        // Fractions are custom vector boxes, not font glyphs, so they have no side bearings.
        // Add explicit glue only between real neighboring nodes.
        private fun interAtomGap(left: LatexNode, right: LatexNode, font: Font): Double {
            val hasFraction = left is FractionNode || right is FractionNode
            val hasExplicitSpace = left is SpaceNode || right is SpaceNode
            return if (hasFraction && !hasExplicitSpace) {
                FRACTION_SIDE_SPACING_EM * levelFontSize(font)
            } else {
                0.0
            }
        }
    }

    private inner class SuperscriptNode(val content: LatexNode, level: Int) : LatexNode(listOf(content), level) {
        override val visualCharCount: Int = content.visualCharCount
        override fun vectorWidth(font: Font): Double = content.vectorWidth(font)
        override fun vectorMetrics(font: Font): LineBoxMetrics =
            content.vectorMetrics(font).raisedBy(INDEX_RELATIVE_SHIFT * content.levelFontSize(font))

        override fun renderVectorGroup(color: Color?, font: Font): SvgGElement {
            val g = SvgGElement()
            val contentGroup = content.renderVectorGroup(color, font)
            val dyPx = -INDEX_RELATIVE_SHIFT * content.levelFontSize(font)
            contentGroup.transform().set(SvgTransformBuilder().translate(0.0, dyPx).build())
            g.children().add(contentGroup)
            return g
        }
    }

    private fun LineBoxMetrics.raisedBy(shift: Double): LineBoxMetrics {
        val newTop = topToBaseline + shift
        val newBottom = maxOf(0.0, bottomToBaseline - shift)
        return LineBoxMetrics(boxHeight = newTop + newBottom, topToBaseline = newTop)
    }

    private inner class SubscriptNode(val content: LatexNode, level: Int) : LatexNode(listOf(content), level) {
        override val visualCharCount: Int = content.visualCharCount
        override fun vectorWidth(font: Font): Double = content.vectorWidth(font)
        override fun vectorMetrics(font: Font): LineBoxMetrics =
            content.vectorMetrics(font).loweredBy(INDEX_RELATIVE_SHIFT * content.levelFontSize(font))

        override fun renderVectorGroup(color: Color?, font: Font): SvgGElement {
            val g = SvgGElement()
            val contentGroup = content.renderVectorGroup(color, font)
            val dyPx = INDEX_RELATIVE_SHIFT * content.levelFontSize(font)
            contentGroup.transform().set(SvgTransformBuilder().translate(0.0, dyPx).build())
            g.children().add(contentGroup)
            return g
        }
    }

    private fun LineBoxMetrics.loweredBy(shift: Double): LineBoxMetrics {
        val newBottom = bottomToBaseline + shift
        val newTop = maxOf(0.0, topToBaseline - shift)
        return LineBoxMetrics(boxHeight = newTop + newBottom, topToBaseline = newTop)
    }

    // Nested fractions are not supported: numerator/denominator must be non-fraction content.
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
        // Extra allowance below the numerator (em): the plain-text space below the baseline.
        private val numeratorBottomAllowance = 1.0 - TextMetricsEstimator.baselineRatio()
        // Shift the bar baseline below the line baseline by this (em), moving gap from the
        // numerator side to the denominator side without changing total fraction height.
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

        override fun renderVectorGroup(color: Color?, font: Font): SvgGElement {
            val g = SvgGElement()
            val em = levelFontSize(font)

            val numWidth = numerator.vectorWidth(font)
            val denomWidth = denominator.vectorWidth(font)
            val fractionWidthPx = max(numWidth, denomWidth)

            val numGroup = numerator.renderVectorGroup(color, font)
            val denomGroup = denominator.renderVectorGroup(color, font)

            val numShiftX = (fractionWidthPx - numWidth) / 2.0
            val denomShiftX = (fractionWidthPx - denomWidth) / 2.0
            val numShiftY = -(barGlyphOffset + fractionGap + numeratorBottomAllowance) * em
            val denomTopToBaseline = denominator.vectorMetrics(font).topToBaseline
            val denomShiftY = denomTopToBaseline + (fractionGap - barGlyphOffset) * em

            numGroup.transform().set(SvgTransformBuilder().translate(numShiftX, numShiftY).build())
            denomGroup.transform().set(SvgTransformBuilder().translate(denomShiftX, denomShiftY).build())

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
        private const val FRACTION_SIDE_SPACING_EM = 3.0 / 18.0 // TeX thin space, 3 mu.
        private const val INDEX_SIZE_FACTOR = 0.7
        private const val INDEX_RELATIVE_SHIFT = 0.4
        internal const val VECTOR_BBOX_CLASS = "lp-latex-vector-bbox"
        // Fallback <text> run for an unsupported glyph; its font is baked, so Label.applyStyle must not overwrite it.
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
        internal val SYMBOLS = GREEK_LETTERS + OPERATIONS + RELATIONS + MISCELLANEOUS
    }
}
