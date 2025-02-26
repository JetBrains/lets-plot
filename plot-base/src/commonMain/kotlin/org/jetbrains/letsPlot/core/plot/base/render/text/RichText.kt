/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

object RichText {
    const val HYPERLINK_ELEMENT_CLASS = "hyperlink-element"

    fun toSvg(
        text: String,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
    ): List<SvgTextElement> {
        val lines = parse(text, wrapLength, maxLinesCount, markdown)
        val svgLines = render(lines)
        return svgLines
    }

    fun estimateWidth(
        text: String,
        font: Font,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
        widthEstimator: (String, Font) -> Double,
    ): Double {
        val lines = parse(text, wrapLength, maxLinesCount, markdown)
        val widths = lines.map { line ->
            line.sumOf { term -> (term as? Span)?.estimateWidth(font, widthEstimator) ?: 0.0 }
        }

        return widths.maxOrNull() ?: 0.0
    }

    private fun parse(text: String, wrapLength: Int = -1, maxLinesCount: Int = -1, markdown: Boolean = false): List<List<RichTextNode>> {
        fun parse(nodes: List<RichTextNode>, parser: (String) -> List<RichTextNode>): List<RichTextNode> {
            return nodes.flatMap { node ->
                when (node) {
                    is Text -> parser(node.text)
                    else -> listOf(node)
                }
            }
        }

        val terms = listOf(Text(text))
            .let { it.takeUnless { markdown } ?: parse(it, Markdown::parse) }
            .let { parse(it, Latex::parse) }
            .let { parse(it, Hyperlink::parse) }

        val lines = splitByNewLines(terms)

        val wrappedLines = wrap(lines, wrapLength, maxLinesCount)

        return wrappedLines
    }

    private fun wrap(lines: List<List<RichTextNode>>, wrapLength: Int, maxLinesCount: Int): List<List<RichTextNode>> {
        val wrappedLines = lines.flatMap { line -> wrapLine(line, wrapLength) }
        return when {
            maxLinesCount < 0 -> wrappedLines
            wrappedLines.size < maxLinesCount -> wrappedLines
            else -> wrappedLines.dropLast(wrappedLines.size - maxLinesCount) + mutableListOf(mutableListOf(Text("...")))
        }
    }

    private fun splitByNewLines(terms: List<RichTextNode>): List<List<RichTextNode>> {
        val lines = mutableListOf<MutableList<RichTextNode>>(mutableListOf())
        terms.forEach { term ->
            if (term is Text && "\n" in term.text) {
                val parts = term.text.split("\n")
                val currentLineEnding = parts.first()
                if (currentLineEnding.isNotEmpty()) { // starts with "\n" - do not add empty part to the line
                    lines.last().add(Text(currentLineEnding))
                }
                val newLines = parts.drop(1)
                newLines.forEach { lines.add(mutableListOf(Text(it))) }
            } else {
                lines.last().add(term)
            }
        }
        return lines
    }

    internal fun fillTextTermGaps(text: String, specialTerms: List<Pair<Span, IntRange>>): List<Span> {
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
            .map { pos -> Text(text.substring(pos)) to pos }
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
            val availableSpace = wrapLength - wrappedLines.last().sumOf { (it as? Span)?.visualCharCount ?: 0 }
            when {
                term is Span && term.visualCharCount <= availableSpace -> wrappedLines.last().add(term)
                term is Span && term.visualCharCount <= wrapLength -> wrappedLines.add(mutableListOf(term)) // no need to split
                term !is Text -> wrappedLines.add(mutableListOf(term)) // can't fit in one line, but can't split power or link
                else -> { // split text
                    wrappedLines.last().takeIf { availableSpace > 0 }?.add(Text(term.text.take(availableSpace)))
                    wrappedLines += term.text
                        .drop(availableSpace)
                        .chunked(wrapLength)
                        .map { mutableListOf(Text(it)) }
                }
            }
        }

        return wrappedLines
    }

    private fun render(lines: List<List<RichTextNode>>): List<SvgTextElement> {
        return lines.map { line ->
            SvgTextElement().apply {
                children().addAll(renderLine(line))
            }
        }
    }

    private fun renderLine(line: List<RichTextNode>): List<SvgElement> {
        val stack = mutableListOf(RenderState())
        val svg = mutableListOf<SvgElement>()
        line.forEach { term ->
            when (term) {
                is RichTextNode.StrongStart -> stack.add(stack.last().copy(isBold = true))
                is RichTextNode.EmphasisStart -> stack.add(stack.last().copy(isItalic = true))
                is RichTextNode.ColorStart -> stack.add(stack.last().copy(color = term.color))
                is RichTextNode.StrongEnd,
                is RichTextNode.EmphasisEnd,
                is RichTextNode.ColorEnd -> stack.removeLast()
                is Span -> svg += term.render(stack.last())
            }
        }

        return svg
    }

    interface RichTextNode {
        object EmphasisStart : RichTextNode
        object EmphasisEnd : RichTextNode
        object StrongStart : RichTextNode
        object StrongEnd : RichTextNode
        class ColorStart(val color: Color) : RichTextNode
        object ColorEnd : RichTextNode
    }

    internal interface Span : RichTextNode {
        val visualCharCount: Int // in chars, used for line wrapping

        fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double
        fun render(context: RenderState): List<SvgElement>
        fun render(): List<SvgElement> = render(RenderState())
    }

    internal class Text(
        val text: String,
    ) : Span {
        override val visualCharCount: Int = text.length

        override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
            return widthCalculator(text, font)
        }

        override fun render(context: RenderState): List<SvgElement> {
            val tSpan = SvgTSpanElement(text)
            context.apply(tSpan)
            return listOf(tSpan)
        }
    }
}
