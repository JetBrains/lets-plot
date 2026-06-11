/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.intern.util.TextMetricsEstimator.widthCalculator
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode.RichSpan.*
import org.jetbrains.letsPlot.datamodel.svg.dom.*

object RichText {
    val DEF_HORIZONTAL_ANCHOR = Text.HorizontalAnchor.LEFT
    const val HYPERLINK_ELEMENT_CLASS = "hyperlink-element"

    internal data class RenderedLine(
        val line: LineElement,
        val anchor: Text.HorizontalAnchor
    )

    fun toSvg(
        text: String,
        font: Font,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
        anchor: Text.HorizontalAnchor = DEF_HORIZONTAL_ANCHOR,
        initialX: Double? = null
    ): List<SvgElement> {
        return renderLines(text, font, wrapLength, maxLinesCount, markdown, anchor, initialX).map { it.line.element }
    }

    internal fun renderLines(
        text: String,
        font: Font,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
        anchor: Text.HorizontalAnchor = DEF_HORIZONTAL_ANCHOR,
        initialX: Double? = null
    ): List<RenderedLine> {
        val lines = parse(text, font, wrapLength, maxLinesCount, markdown)
        return render(lines, font, renderPlans = renderPlans(lines, anchor), initialX = initialX)
    }

    fun measure(
        text: String,
        font: Font,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
        lineInterval: Double = 0.0,
    ): MeasuredText {
        val (lineMetrics, width) = estimateTextLayoutAndWidth(text, font, wrapLength, maxLinesCount, markdown)
        return MeasuredText(layout = TextBlockLayout.fromLineBoxes(lineMetrics, lineInterval), width = width)
    }

    private fun estimateTextLayoutAndWidth(
        text: String,
        font: Font,
        wrapLength: Int,
        maxLinesCount: Int,
        markdown: Boolean,
    ): Pair<List<LineBoxMetrics>, Double> {
        val defaultMetrics = LineBoxMetrics.plainText(font)

        val lines = parse(text, font, wrapLength, maxLinesCount, markdown)
        if (lines.isEmpty()) {
            return listOf(defaultMetrics) to 0.0
        }
        val measuredLines = lines.map { line ->
            if (line.isEmpty()) {
                defaultMetrics to 0.0
            }
            else {
                val terms = line.mapNotNull { term -> term as? RichTextNode.RichSpan }
                LineBoxMetrics.mergeOnBaseline(
                    metrics = terms.map { term -> term.estimateLineLayoutMetrics(font) },
                    defaultIfEmpty = defaultMetrics
                ) to terms.sumOf { term -> term.estimateWidth(font) }
            }
        }
        val lineMetrics = measuredLines.map { it.first }
        val width = measuredLines.maxOf { it.second }
        return lineMetrics to width
    }

    private fun parse(
        text: String,
        font: Font,
        wrapLength: Int,
        maxLinesCount: Int,
        markdown: Boolean,
    ): List<List<RichTextNode>> {
        fun parse(nodes: List<RichTextNode>, parser: (String) -> List<RichTextNode>): List<RichTextNode> {
            return nodes.flatMap { node ->
                when (node) {
                    is RichTextNode.Text -> parser(node.text)
                    else -> listOf(node)
                }
            }
        }

        val terms = listOf(RichTextNode.Text(text))
            .let {
                if (markdown) {
                    parse(it, Markdown::parse)
                } else {
                    parse(it, Plaintext::parse)
                }
            }
            .let { parse(it, Latex(font)::parse) }
            .let { parseBreaks(it) }

        val lines = buildLines(terms)

        val wrappedLines = wrap(lines, wrapLength, maxLinesCount)

        return wrappedLines
    }

    private fun wrap(lines: List<List<RichTextNode>>, wrapLength: Int, maxLinesCount: Int): List<List<RichTextNode>> {
        val wrappedLines = lines.flatMap { line -> wrapLine(line, wrapLength) }
        return when {
            maxLinesCount < 0 -> wrappedLines
            wrappedLines.size < maxLinesCount -> wrappedLines
            else -> wrappedLines.dropLast(wrappedLines.size - maxLinesCount) +
                    mutableListOf(mutableListOf(RichTextNode.Text("...")))
        }
    }

    private fun parseBreaks(terms: List<RichTextNode>): List<RichTextNode> {
        val result = mutableListOf<RichTextNode>()
        terms.forEach { term ->
            if (term is RichTextNode.Text) {
                val lines = term.text.split("\n")

                lines.forEachIndexed { i, line ->
                    val content = when {
                        lines.size == 1 -> line                    // no '\n' in this fragment
                        i == 0 -> line.trimEnd()                   // only trailing '\n' is adjacent
                        i == lines.lastIndex -> line.trimStart()   // only leading '\n' is adjacent
                        else -> line.trim()                        // sandwiched between '\n's
                    }
                    if (content.isNotEmpty()) {
                        result.add(RichTextNode.Text(content))
                    }

                    if (i != lines.lastIndex) {
                        result.add(RichTextNode.LineBreak)
                    }
                }
            } else {
                result.add(term)
            }
        }
        return result
    }

    private fun buildLines(terms: List<RichTextNode>): List<List<RichTextNode>> {
        if (terms.isEmpty()) {
            return emptyList()
        }

        var startNewLine = true
        val lines = mutableListOf<MutableList<RichTextNode>>()

        terms.forEach { term ->
            if (startNewLine) {
                lines.add(mutableListOf())
                startNewLine = false
            }

            if (term is RichTextNode.LineBreak) {
                startNewLine = true
            } else {
                val lastTerm = lines.last().lastOrNull()

                if (lastTerm is RichTextNode.Text && term is RichTextNode.Text) {
                    // merge adjacent text nodes
                    lines.last().removeLast()
                    lines.last().add(RichTextNode.Text(lastTerm.text + term.text))
                    return@forEach
                } else {
                    lines.last().add(term)
                }
            }
        }

        if (startNewLine) {
            lines.add(mutableListOf())
        }

        return lines
    }

    private fun wrapLine(line: List<RichTextNode>, wrapLength: Int = -1): List<List<RichTextNode>> {
        if (wrapLength <= 0) {
            return listOf(line)
        }

        val wrappedLines = mutableListOf(mutableListOf<RichTextNode>())
        line.forEach { term ->
            val availableSpace =
                wrapLength - wrappedLines.last().sumOf { (it as? RichTextNode.RichSpan)?.visualCharCount ?: 0 }
            when {
                term is RichTextNode.RichSpan && term.visualCharCount <= availableSpace -> wrappedLines.last().add(term)
                term is RichTextNode.RichSpan && term.visualCharCount <= wrapLength -> wrappedLines.add(mutableListOf(term)) // no need to split
                term !is RichTextNode.Text -> wrappedLines.add(mutableListOf(term)) // can't fit in one line, but can't split power or link
                else -> { // split text
                    wrappedLines.last().takeIf { availableSpace > 0 }
                        ?.add(RichTextNode.Text(term.text.take(availableSpace)))
                    wrappedLines += term.text
                        .drop(availableSpace)
                        .chunked(wrapLength)
                        .map { mutableListOf(RichTextNode.Text(it)) }
                }
            }
        }

        return wrappedLines
    }

    private data class LineRenderPlan(
        val lineAnchor: Text.HorizontalAnchor,
        val lineOriginShiftCoefficient: Double?
    )

    private fun renderPlans(
        lines: List<List<RichTextNode>>,
        anchor: Text.HorizontalAnchor
    ): List<LineRenderPlan> {
        return lines.map { line ->
            val needsLocalFrame = line.any { term -> term is Latex.VectorLatexElement }
            if (!needsLocalFrame) {
                return@map LineRenderPlan(
                    lineAnchor = anchor,
                    lineOriginShiftCoefficient = null
                )
            }
            val originShiftCoefficient = when (anchor) {
                Text.HorizontalAnchor.LEFT -> 0.0
                Text.HorizontalAnchor.MIDDLE -> 0.5
                Text.HorizontalAnchor.RIGHT -> 1.0
            }
            // Fraction lines and vector-formula lines are rendered in a line-local coordinate frame
            // so all nested elements share a single origin. The block still honors the requested
            // anchor, but that anchor is resolved here to an explicit line origin shift.
            LineRenderPlan(
                lineAnchor = Text.HorizontalAnchor.LEFT,
                lineOriginShiftCoefficient = originShiftCoefficient
            )
        }
    }

    private fun render(
        lines: List<List<RichTextNode>>,
        font: Font,
        renderPlans: List<LineRenderPlan>,
        initialX: Double?
    ): List<RenderedLine> {
        val stack = mutableListOf(RenderState())
        return (lines zip renderPlans).map { (line, renderPlan) ->
            val svg = mutableListOf<SvgElement>()
            val lineWidth = line.sumOf { term -> (term as? RichTextNode.RichSpan)?.estimateWidth(font) ?: 0.0 }
            var prefixWidth = 0.0
            var isFirstRichSpanInLine = true
            // After a non-tspan element (e.g. a vector formula group), the next text run starts in
            // a fresh SvgTextElement and its first tspan must have x set explicitly.
            var spanContinuityBroken = false
            // Detect a contiguous text-like prefix (Text runs and/or hyperlinks) before the first
            // vector formula. If found, the whole prefix is end-anchored as a single SVG chunk so
            // its right edge meets the formula's left edge — eliminating the estimator-vs-host-font
            // gap for links and multi-run markdown prefixes, not just single plain-Text prefixes.
            val richSpans = line.filterIsInstance<RichTextNode.RichSpan>()
            val firstFormulaIdx = richSpans.indexOfFirst { it is Latex.VectorLatexElement }
            val prefixSpans = if (firstFormulaIdx > 0) richSpans.subList(0, firstFormulaIdx) else emptyList()
            val pinnablePrefix = prefixSpans.isNotEmpty() &&
                    prefixSpans.all { it is RichTextNode.Text || it is Hyperlink.HyperlinkElement }
            // x is constant for the whole line — hoist it so formulaLeftEdge can be pre-computed.
            val lineX = when {
                renderPlan.lineOriginShiftCoefficient == null -> null
                initialX == null && renderPlan.lineOriginShiftCoefficient == 0.0 -> null
                initialX == null -> -renderPlan.lineOriginShiftCoefficient * lineWidth
                else -> initialX - renderPlan.lineOriginShiftCoefficient * lineWidth
            }
            val formulaLeftEdge = (lineX ?: 0.0) + prefixSpans.sumOf { it.estimateWidth(font) }
            // Prefix SVG elements are gathered here so the pin can be applied after the loop.
            val prefixRenderedElements = if (pinnablePrefix) mutableListOf<SvgElement>() else null
            var prefixSpansProcessed = 0
            line.forEach { term ->
                when (term) {
                    is RichTextNode.StrongStart -> stack.add(stack.last().copy(isBold = true))
                    is RichTextNode.EmphasisStart -> stack.add(stack.last().copy(isItalic = true))
                    is RichTextNode.ColorStart -> stack.add(stack.last().copy(color = term.color))
                    is RichTextNode.StrongEnd,
                    is RichTextNode.EmphasisEnd,
                    is RichTextNode.ColorEnd -> stack.removeLast()

                    is RichTextNode.RichSpan -> {
                        // For complex lines (for example with fractions), the line may be anchored by
                        // shifting its explicit origin instead of by SVG `text-anchor`.
                        val x = lineX
                        val effectiveIsFirst = isFirstRichSpanInLine || spanContinuityBroken
                        val effectiveX = if (spanContinuityBroken) (x ?: 0.0) + prefixWidth else x
                        val termWidth = term.estimateWidth(font)
                        val rendered = term.render(stack.last(), prefixWidth, effectiveX, effectiveIsFirst)
                        if (pinnablePrefix && prefixSpansProcessed < prefixSpans.size) {
                            prefixRenderedElements!! += rendered
                            prefixSpansProcessed++
                        }
                        svg += rendered
                        prefixWidth += termWidth
                        isFirstRichSpanInLine = false
                        spanContinuityBroken = term is Latex.VectorLatexElement
                    }

                    is RichTextNode.LineBreak -> throw IllegalStateException("Line breaks should be parsed before rendering")
                }
            }
            // Post-loop pin: end-anchor the whole prefix chunk so its right edge meets the
            // formula's left edge. text-anchor=end goes on every prefix tspan; x=formulaLeftEdge
            // only on the first (which starts the SVG text chunk).
            if (pinnablePrefix && prefixRenderedElements != null) {
                val prefixTSpans = prefixRenderedElements.flatMap { el ->
                    when (el) {
                        is SvgTSpanElement -> listOf(el)
                        is SvgAElement -> listOf(el.children().single() as SvgTSpanElement)
                        else -> emptyList()
                    }
                }
                prefixTSpans.forEachIndexed { i, tspan ->
                    tspan.setAttribute(SvgTextContent.TEXT_ANCHOR, SvgConstants.SVG_TEXT_ANCHOR_END)
                    if (i == 0) {
                        tspan.setAttribute(SvgTextContent.X, formulaLeftEdge)
                    }
                }
            }
            RenderedLine(
                line = assembleLineElement(svg),
                anchor = renderPlan.lineAnchor
            )
        }
    }

    // If the line has only tspan-like children, wrap in a single SvgTextElement (unchanged shape).
    // Otherwise wrap in an SvgGElement with runs of tspan-like children grouped into SvgTextElement
    // siblings and any SvgGElement children kept as siblings — this is required for vector formulas,
    // since SVG <text> may not contain <g> or <path>.
    private fun assembleLineElement(svg: List<SvgElement>): LineElement {
        val anyGroup = svg.any { it is SvgGElement }
        if (!anyGroup) {
            return TextLine(SvgTextElement().apply { children().addAll(svg) })
        }
        val outer = SvgGElement()
        var currentText: SvgTextElement? = null
        for (el in svg) {
            if (el is SvgGElement) {
                currentText = null
                outer.children().add(el)
            } else {
                val tx = currentText ?: SvgTextElement().also {
                    currentText = it
                    outer.children().add(it)
                }
                tx.children().add(el)
            }
        }
        return GroupLine(outer)
    }

    internal sealed interface RichTextNode {
        object EmphasisStart : RichTextNode
        object EmphasisEnd : RichTextNode
        object StrongStart : RichTextNode
        object StrongEnd : RichTextNode
        object ColorEnd : RichTextNode
        object LineBreak : RichTextNode

        class ColorStart(val color: Color) : RichTextNode {
            override fun toString() = "ColorStart(color=$color)"
        }

        abstract class RichSpan : RichTextNode {
            abstract val visualCharCount: Int // in chars, used for line wrapping

            abstract fun estimateWidth(font: Font): Double
            abstract fun estimateLineLayoutMetrics(font: Font): LineBoxMetrics
            abstract fun render(context: RenderState, prefixWidth: Double): List<WrappedSvgElement<SvgElement>>

            // During the rendering process, the RichSpan is converted to collection of the RichSpanElement,
            // and then each of them is rendered to SVG element, taking into account the additional x parameter;
            // each resulting SVG element is a span-like element (SvgTSpanElement or SvgAElement with SvgTSpanElement as a child)
            fun render(context: RenderState, prefixWidth: Double, x: Double?, isFirstRichSpanInLine: Boolean): List<SvgElement> {
                return render(context, prefixWidth).mapIndexed { i, wrappedElement ->
                    wrappedElement.x = when {
                        // If wrappedElement.x == null than x should be defined only for the first span in the line
                        wrappedElement.x == null -> if (isFirstRichSpanInLine && i == 0) x else null
                        // If wrappedElement.x != null, it means that it should be shifted
                        else -> wrappedElement.x!! + (x ?: 0.0)
                    }
                    wrappedElement.updateSvgXAttribute().svg
                }
            }

            abstract class WrappedSvgElement<T : SvgElement>(val svg: T, var x: Double? = null) {
                abstract fun updateSvgXAttribute(): WrappedSvgElement<T>
            }

            class WrappedTSpanElement(svg: SvgTSpanElement, x: Double? = null) : WrappedSvgElement<SvgTSpanElement>(svg, x) {
                override fun updateSvgXAttribute(): WrappedSvgElement<SvgTSpanElement> {
                    x?.let { svg.setAttribute(SvgTextContent.X, it) }
                    return this
                }
            }

            class WrappedAElement(svg: SvgAElement, x: Double? = null) : WrappedSvgElement<SvgAElement>(svg, x) {
                override fun updateSvgXAttribute(): WrappedSvgElement<SvgAElement> {
                    val tSpan = svg.children().single() as SvgTSpanElement
                    x?.let { tSpan.setAttribute(SvgTextContent.X, it) }
                    return this
                }
            }

            // Wraps an SvgGElement (used for vector LaTeX formulas). Positioning is via a
            // translate transform on the group itself, since <g> has no x/y attributes.
            class WrappedGElement(svg: SvgGElement, x: Double? = null) : WrappedSvgElement<SvgGElement>(svg, x) {
                override fun updateSvgXAttribute(): WrappedSvgElement<SvgGElement> {
                    x?.let { svg.transform().set(SvgTransformBuilder().translate(it, 0.0).build()) }
                    return this
                }
            }
        }

        class Text(
            val text: String
        ) : RichSpan() {
            override val visualCharCount: Int = text.length

            override fun estimateWidth(font: Font): Double {
                return widthCalculator(text, font)
            }

            override fun estimateLineLayoutMetrics(font: Font): LineBoxMetrics {
                return LineBoxMetrics.plainText(font)
            }

            override fun render(context: RenderState, prefixWidth: Double): List<WrappedSvgElement<SvgElement>> {
                return SvgTSpanElement(text)
                    .apply(context::apply)
                    .wrap()
                    .let(::listOf)
            }

            override fun toString() = "Text(text='$text')"
        }
    }

    internal fun SvgTSpanElement.wrap(x: Double? = null): WrappedSvgElement<SvgElement> {
        @Suppress("UNCHECKED_CAST")
        return WrappedTSpanElement(this, x) as WrappedSvgElement<SvgElement>
    }

    internal fun SvgAElement.wrap(x: Double? = null): WrappedSvgElement<SvgElement> {
        @Suppress("UNCHECKED_CAST")
        return WrappedAElement(this, x) as WrappedSvgElement<SvgElement>
    }

    internal fun SvgGElement.wrap(x: Double? = null): WrappedSvgElement<SvgElement> {
        @Suppress("UNCHECKED_CAST")
        return WrappedGElement(this, x) as WrappedSvgElement<SvgElement>
    }

    internal fun SvgElement.wrap(x: Double? = null): WrappedSvgElement<SvgElement> {
        return when (this) {
            is SvgTSpanElement -> wrap(x)
            is SvgAElement -> wrap(x)
            is SvgGElement -> wrap(x)
            else -> throw IllegalArgumentException("Unsupported SVG element type: ${this::class.simpleName}")
        }
    }
}
