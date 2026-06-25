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

        applyInlineFace(wrappedLines)

        return wrappedLines
    }

    // Inline face state is line-persistent, so spans after hard breaks inherit open markdown.
    // Inline face affects geometry, so it must be applied before measurement.
    private fun applyInlineFace(lines: List<List<RichTextNode>>) {
        var boldDepth = 0
        var italicDepth = 0
        lines.forEach { line ->
            line.forEach { term ->
                when (term) {
                    is RichTextNode.StrongStart -> boldDepth++
                    is RichTextNode.StrongEnd -> boldDepth--
                    is RichTextNode.EmphasisStart -> italicDepth++
                    is RichTextNode.EmphasisEnd -> italicDepth--
                    is RichTextNode.RichSpan -> {
                        term.inlineBold = boldDepth > 0
                        term.inlineItalic = italicDepth > 0
                    }
                    else -> {}
                }
            }
        }
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
        val lineOriginShiftCoefficient: Double?,
        val requestedAnchor: Text.HorizontalAnchor
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
                    lineOriginShiftCoefficient = null,
                    requestedAnchor = anchor
                )
            }
            val originShiftCoefficient = when (anchor) {
                Text.HorizontalAnchor.LEFT -> 0.0
                Text.HorizontalAnchor.MIDDLE -> 0.5
                Text.HorizontalAnchor.RIGHT -> 1.0
            }
            // Vector-formula lines render in a line-local frame so nested elements share one origin;
            // the requested anchor is resolved here to an explicit origin shift, not SVG text-anchor.
            LineRenderPlan(
                lineAnchor = Text.HorizontalAnchor.LEFT,
                lineOriginShiftCoefficient = originShiftCoefficient,
                requestedAnchor = anchor
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
            // A contiguous text-like prefix (Text/hyperlinks) before the first vector formula is
            // end-anchored to the formula's left edge, closing the estimator-vs-host-font gap.
            val richSpans = line.filterIsInstance<RichTextNode.RichSpan>()
            val firstFormulaIdx = richSpans.indexOfFirst { it is Latex.VectorLatexElement }
            val prefixSpans = if (firstFormulaIdx > 0) richSpans.subList(0, firstFormulaIdx) else emptyList()
            val pinnablePrefix = prefixSpans.isNotEmpty() &&
                    prefixSpans.all { it is RichTextNode.Text || it is Hyperlink.HyperlinkElement }
            val lastFormulaIdx = richSpans.indexOfLast { it is Latex.VectorLatexElement }
            val suffixSpans = if (lastFormulaIdx in 0 until richSpans.size - 1) {
                richSpans.subList(lastFormulaIdx + 1, richSpans.size)
            } else {
                emptyList()
            }
            val pinnableSuffix = suffixSpans.isNotEmpty() &&
                    suffixSpans.all { it is RichTextNode.Text || it is Hyperlink.HyperlinkElement }
            // x is constant for the whole line — hoist it so formulaLeftEdge can be pre-computed.
            val lineX = when {
                renderPlan.lineOriginShiftCoefficient == null -> null
                initialX == null && renderPlan.lineOriginShiftCoefficient == 0.0 -> null
                initialX == null -> -renderPlan.lineOriginShiftCoefficient * lineWidth
                else -> initialX - renderPlan.lineOriginShiftCoefficient * lineWidth
            }
            val formulaLeftEdge = (lineX ?: 0.0) + prefixSpans.sumOf { it.estimateWidth(font) }
            val lineRightEdge = (lineX ?: 0.0) + lineWidth
            // Prefix SVG elements are gathered here so the pin can be applied after the loop.
            val prefixRenderedElements = if (pinnablePrefix) mutableListOf<SvgElement>() else null
            val suffixRenderedElements = if (pinnableSuffix) mutableListOf<SvgElement>() else null
            var richSpanIndex = 0
            line.forEach { term ->
                when (term) {
                    is RichTextNode.StrongStart -> stack.add(stack.last().copy(isBold = true))
                    is RichTextNode.EmphasisStart -> stack.add(stack.last().copy(isItalic = true))
                    is RichTextNode.ColorStart -> stack.add(stack.last().copy(color = term.color))
                    is RichTextNode.StrongEnd,
                    is RichTextNode.EmphasisEnd,
                    is RichTextNode.ColorEnd -> stack.removeLast()

                    is RichTextNode.RichSpan -> {
                        val effectiveIsFirst = isFirstRichSpanInLine || spanContinuityBroken
                        val effectiveX = if (spanContinuityBroken && term !is Latex.VectorLatexElement) {
                            (lineX ?: 0.0) + prefixWidth
                        } else {
                            lineX
                        }
                        val termWidth = term.estimateWidth(font)
                        val rendered = term.render(stack.last(), prefixWidth, effectiveX, effectiveIsFirst)
                        if (pinnablePrefix && richSpanIndex < firstFormulaIdx) {
                            prefixRenderedElements!! += rendered
                        }
                        if (pinnableSuffix && richSpanIndex > lastFormulaIdx) {
                            suffixRenderedElements!! += rendered
                        }
                        svg += rendered
                        prefixWidth += termWidth
                        isFirstRichSpanInLine = false
                        spanContinuityBroken = term is Latex.VectorLatexElement
                        richSpanIndex++
                    }

                    is RichTextNode.LineBreak -> throw IllegalStateException("Line breaks should be parsed before rendering")
                }
            }
            fun pinTSpans(elements: List<SvgElement>, x: Double) {
                val tSpans = elements.flatMap { el ->
                    when (el) {
                        is SvgTSpanElement -> listOf(el)
                        is SvgAElement -> listOf(el.children().single() as SvgTSpanElement)
                        else -> emptyList()
                    }
                }
                tSpans.forEachIndexed { i, tspan ->
                    tspan.setAttribute(SvgTextContent.TEXT_ANCHOR, SvgConstants.SVG_TEXT_ANCHOR_END)
                    if (i == 0) {
                        tspan.setAttribute(SvgTextContent.X, x)
                    }
                }
            }
            // Pin the prefix flush to the formula: text-anchor=end on every prefix tspan,
            // x=formulaLeftEdge on the first one (which starts the SVG text chunk).
            if (pinnablePrefix &&
                renderPlan.requestedAnchor != Text.HorizontalAnchor.LEFT &&
                prefixRenderedElements != null) {
                pinTSpans(prefixRenderedElements, formulaLeftEdge)
            }
            if (pinnableSuffix &&
                renderPlan.requestedAnchor == Text.HorizontalAnchor.RIGHT &&
                suffixRenderedElements != null) {
                pinTSpans(suffixRenderedElements, lineRightEdge)
            }
            RenderedLine(
                line = assembleLineElement(svg),
                anchor = renderPlan.lineAnchor
            )
        }
    }

    // Pure-tspan lines become one SvgTextElement. Lines containing an SvgGElement (vector formula)
    // become an SvgGElement of <text> runs + <g> siblings, since SVG <text> can't hold <g>/<path>.
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

            var inlineBold: Boolean = false
            var inlineItalic: Boolean = false

            protected fun effective(base: Font): Font =
                Font(base.family, base.size, base.isBold || inlineBold, base.isItalic || inlineItalic)

            abstract fun estimateWidth(font: Font): Double
            abstract fun estimateLineLayoutMetrics(font: Font): LineBoxMetrics
            abstract fun render(context: RenderState, prefixWidth: Double): List<WrappedSvgElement<SvgElement>>

            // Renders each wrapped element to SVG, applying the x offset. Results are span-like
            // (SvgTSpanElement / SvgAElement) or a vector-formula SvgGElement.
            fun render(context: RenderState, prefixWidth: Double, x: Double?, isFirstRichSpanInLine: Boolean): List<SvgElement> {
                return render(context, prefixWidth).mapIndexed { i, wrappedElement ->
                    wrappedElement.x = when {
                        // x == null: only the line's first span gets an x.
                        wrappedElement.x == null -> if (isFirstRichSpanInLine && i == 0) x else null
                        // x != null: shift the existing x by the line origin.
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
                return widthCalculator(text, effective(font))
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
