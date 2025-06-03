/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

object RichText {
    const val HYPERLINK_ELEMENT_CLASS = "hyperlink-element"

    fun toSvg(
        text: String,
        font: Font,
        widthCalculator: (String, Font) -> Double,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
    ): List<SvgTextElement> {
        val lines = parse(text, font, widthCalculator, wrapLength, maxLinesCount, markdown)
        val svgLines = render(lines)
        return svgLines
    }

    fun estimateWidth(
        text: String,
        font: Font,
        widthCalculator: (String, Font) -> Double,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
    ): Double {
        val lines = parse(text, font, widthCalculator, wrapLength, maxLinesCount, markdown)
        val widths = lines.map { line ->
            line.sumOf { term -> (term as? RichTextNode.Span)?.estimateWidth(font, widthCalculator) ?: 0.0 }
        }

        return widths.maxOrNull() ?: 0.0
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
        specialTerms: List<Pair<RichTextNode.Span, IntRange>>
    ): List<RichTextNode.Span> {
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
                wrapLength - wrappedLines.last().sumOf { (it as? RichTextNode.Span)?.visualCharCount ?: 0 }
            when {
                term is RichTextNode.Span && term.visualCharCount <= availableSpace -> wrappedLines.last().add(term)
                term is RichTextNode.Span && term.visualCharCount <= wrapLength -> wrappedLines.add(mutableListOf(term)) // no need to split
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

    private fun render(lines: List<List<RichTextNode>>): List<SvgTextElement> {
        val stack = mutableListOf(RenderState())
        val svgLines = lines.map { line ->
            val svg = mutableListOf<SvgElement>()
            val previousNodes = mutableListOf<RichTextNode.Span>()
            line.forEach { term ->
                when (term) {
                    is RichTextNode.StrongStart -> stack.add(stack.last().copy(isBold = true))
                    is RichTextNode.EmphasisStart -> stack.add(stack.last().copy(isItalic = true))
                    is RichTextNode.ColorStart -> stack.add(stack.last().copy(color = term.color))
                    is RichTextNode.StrongEnd,
                    is RichTextNode.EmphasisEnd,
                    is RichTextNode.ColorEnd -> stack.removeLast()

                    is RichTextNode.Span -> {
                        svg += term.render(stack.last(), previousNodes.toList())
                        previousNodes.add(term)
                    }
                }
            }
            svg
        }

        return svgLines.map { SvgTextElement().apply { children().addAll(it) } }
    }

    interface RichTextNode {
        object EmphasisStart : RichTextNode
        object EmphasisEnd : RichTextNode
        object StrongStart : RichTextNode
        object StrongEnd : RichTextNode
        object ColorEnd : RichTextNode
        object LineBreak : RichTextNode

        class ColorStart(val color: Color) : RichTextNode {
            override fun toString() = "ColorStart(color=$color)"
        }

        abstract class Span : RichTextNode {
            protected var x: Double? = null
            abstract val visualCharCount: Int // in chars, used for line wrapping

            abstract fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double
            abstract fun toSvg(context: RenderState, previousNodes: List<Span>): List<SvgElement>

            fun render(context: RenderState, previousNodes: List<Span>): List<SvgElement> {
                return toSvg(context, previousNodes).map { svgElement ->
                    svgElement.apply {
                        x?.let { setAttribute(SvgTextContent.X, x.toString()) }
                    }
                }
            }

            fun render(): List<SvgElement> = toSvg(RenderState(), emptyList())
        }

        class Text(
            val text: String
        ) : Span() {
            override val visualCharCount: Int = text.length

            override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
                return widthCalculator(text, font)
            }

            override fun toSvg(context: RenderState, previousNodes: List<Span>): List<SvgElement> {
                val tSpan = SvgTSpanElement(text)
                context.apply(tSpan)
                return listOf(tSpan)
            }

            override fun toString() = "Text(text='$text')"
        }
    }
}
