/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode.RichSpan.WrappedSvgElement
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode.RichSpan.WrappedAElement
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText.RichTextNode.RichSpan.WrappedTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

object RichText {
    val DEF_HORIZONTAL_ANCHOR = Text.HorizontalAnchor.LEFT
    const val HYPERLINK_ELEMENT_CLASS = "hyperlink-element"

    fun toSvg(
        text: String,
        font: Font,
        widthCalculator: (String, Font) -> Double,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
        anchor: Text.HorizontalAnchor = DEF_HORIZONTAL_ANCHOR,
        initialX: Double = 0.0
    ): List<SvgTextElement> {
        val lines = parse(text, font, widthCalculator, wrapLength, maxLinesCount, markdown)
        val svgLines = render(lines, font, widthCalculator, anchorCoefficients = anchorCoefficients(lines, anchor), initialX = initialX)
        return svgLines
    }

    fun estimateMaxWidth(
        text: String,
        font: Font,
        widthCalculator: (String, Font) -> Double,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
    ): Double {
        val lines = parse(text, font, widthCalculator, wrapLength, maxLinesCount, markdown)
        val widths = lines.map { line ->
            line.sumOf { term -> (term as? RichTextNode.RichSpan)?.estimateWidth(font, widthCalculator) ?: 0.0 }
        }

        return widths.maxOrNull() ?: 0.0
    }

    fun estimateHeights(
        text: String,
        font: Font,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
    ): List<Double> {
        val lines = parse(text, font, { _, _ -> 0.0 }, wrapLength, maxLinesCount, markdown)
        if (lines.isEmpty()) {
            return listOf(RichTextNode.Text("").estimateHeight(font))
        }
        return lines.map { line ->
            line.maxOf { term -> (term as? RichTextNode.RichSpan)?.estimateHeight(font) ?: 0.0 }
        }
    }

    private fun parse(
        text: String,
        font: Font,
        widthCalculator: (String, Font) -> Double,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false
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
            .let { it.takeUnless { markdown } ?: parse(it, Markdown::parse) }
            .let { parse(it, Latex(font, widthCalculator)::parse) }
            .let { parse(it, Hyperlink::parse) }
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
            else -> wrappedLines.dropLast(wrappedLines.size - maxLinesCount) + mutableListOf(
                mutableListOf(
                    RichTextNode.Text(
                        "..."
                    )
                )
            )
        }
    }

    private fun parseBreaks(terms: List<RichTextNode>): List<RichTextNode> {
        val result = mutableListOf<RichTextNode>()
        terms.forEach { term ->
            if (term is RichTextNode.Text) {
                val lines = term.text.split("\n")

                lines.forEachIndexed { i, line ->
                    if (line.isNotEmpty()) {
                        result.add(RichTextNode.Text(line))
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
                lines.last().add(term)
            }
        }

        if (startNewLine) {
            lines.add(mutableListOf())
        }

        return lines
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

    private fun anchorCoefficients(
        lines: List<List<RichTextNode>>,
        anchor: Text.HorizontalAnchor
    ): List<Double?> {
        return lines.map { line ->
            val containsLatexFractionNode = line.any { term ->
                term is Latex.LatexElement && term.node.flatListOfAllDescendants().any { latexNode ->
                    latexNode is Latex.FractionNode
                }
            }
            // The coefficient is dependent on the anchor but only if there is a fraction node in the line
            if (!containsLatexFractionNode) {
                return@map null
            }
            when (anchor) {
                Text.HorizontalAnchor.LEFT -> null // no shift needed when text is left-aligned
                Text.HorizontalAnchor.MIDDLE -> 0.5
                Text.HorizontalAnchor.RIGHT -> 1.0
            }
        }
    }

    private fun render(
        lines: List<List<RichTextNode>>,
        font: Font,
        widthCalculator: (String, Font) -> Double,
        anchorCoefficients: List<Double?>,
        initialX: Double
    ): List<SvgTextElement> {
        val stack = mutableListOf(RenderState())
        val svgLines = (lines zip anchorCoefficients).map { (line, anchorCoefficient) ->
            val svg = mutableListOf<SvgElement>()
            val prefix = mutableListOf<RichTextNode.RichSpan>()
            val lineWidth = line.sumOf { term -> (term as? RichTextNode.RichSpan)?.estimateWidth(font, widthCalculator) ?: 0.0 }
            var isFirstRichSpanInLine = true
            line.forEach { term ->
                when (term) {
                    is RichTextNode.StrongStart -> stack.add(stack.last().copy(isBold = true))
                    is RichTextNode.EmphasisStart -> stack.add(stack.last().copy(isItalic = true))
                    is RichTextNode.ColorStart -> stack.add(stack.last().copy(color = term.color))
                    is RichTextNode.StrongEnd,
                    is RichTextNode.EmphasisEnd,
                    is RichTextNode.ColorEnd -> stack.removeLast()

                    is RichTextNode.RichSpan -> {
                        // Based on the whole line the `anchorCoefficient` was calculated
                        // and if it is not null, it means that the line contains [at least] a fraction node,
                        // and then we need to add x attribute to the first tspan in the line with shift,
                        // that corresponds to the anchorCoefficient.
                        val x = anchorCoefficient?.let { initialX - it * lineWidth }
                        svg += term.render(stack.last(), prefix.toList(), x, isFirstRichSpanInLine)
                        prefix.add(term)
                        isFirstRichSpanInLine = false
                    }

                    is RichTextNode.LineBreak -> throw IllegalStateException("Line breaks should be parsed before rendering")
                }
            }
            svg
        }

        return svgLines.map { SvgTextElement().apply { children().addAll(it) } }
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

            abstract fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double
            abstract fun estimateHeight(font: Font): Double // TODO: Add default behavior and use it for all descendants
            abstract fun render(context: RenderState, prefix: List<RichSpan>): List<WrappedSvgElement<SvgElement>>

            // During the rendering process, the RichSpan is converted to collection of the RichSpanElement,
            // and then each of them is rendered to SVG element, taking into account the additional x parameter;
            // each resulting SVG element is a span-like element (SvgTSpanElement or SvgAElement with SvgTSpanElement as a child)
            fun render(context: RenderState, prefix: List<RichSpan>, x: Double?, isFirstRichSpanInLine: Boolean): List<SvgElement> {
                return render(context, prefix).mapIndexed { i, wrappedElement ->
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
        }

        class Text(
            val text: String
        ) : RichSpan() {
            override val visualCharCount: Int = text.length

            override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double =
                widthCalculator(text, font)

            override fun estimateHeight(font: Font): Double =
                font.size.toDouble()

            override fun render(context: RenderState, prefix: List<RichSpan>): List<WrappedSvgElement<SvgElement>> {
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

    internal fun SvgElement.wrap(x: Double? = null): WrappedSvgElement<SvgElement> {
        return when (this) {
            is SvgTSpanElement -> wrap(x)
            is SvgAElement -> wrap(x)
            else -> throw IllegalArgumentException("Unsupported SVG element type: ${this::class.simpleName}")
        }
    }
}
