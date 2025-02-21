/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement

object RichText {
    const val HYPERLINK_ELEMENT_CLASS = "hyperlink-element"

    fun toSvg(
        text: String,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
    ): List<SvgTextElement> {
        val lines = parseText(text, wrapLength, maxLinesCount, markdown)

        return lines.map { line ->
            SvgTextElement().apply {
                children().addAll(line.flatMap(Term::svg))
            }
        }
    }

    fun estimateWidth(
        text: String,
        font: Font,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1,
        markdown: Boolean = false,
        widthEstimator: (String, Font) -> Double,
    ): Double {
        return parseText(text, wrapLength, maxLinesCount, markdown)
            .maxOfOrNull { line -> line.sumOf { term -> term.estimateWidth(font, widthEstimator) } }
            ?: 0.0
    }

    private fun wrap(lines: List<List<Term>>, wrapLength: Int, maxLinesCount: Int): List<List<Term>> {
        val wrappedLines = lines.flatMap { line -> wrapLine(line, wrapLength) }
        return when {
            maxLinesCount < 0 -> wrappedLines
            wrappedLines.size < maxLinesCount -> wrappedLines
            else -> wrappedLines.dropLast(wrappedLines.size - maxLinesCount) + mutableListOf(mutableListOf(Text("...")))
        }
    }

    private fun parseText(text: String, wrapLength: Int = -1, maxLinesCount: Int = -1, markdown: Boolean = false): List<List<Term>> {
        fun render(str: List<Term>, parser: (String) -> List<Term>): List<Term> {
            return str.flatMap {
                when (it) {
                    is Text -> parser(it.text)
                    else -> listOf(it)
                }
            }
        }

        val terms = listOf(Text(text))
            .let {
                when (markdown) {
                    true -> render(it, Markdown::render)
                    false -> it
                }
            }
            .let { render(it, Latex::render) }
            .let { render(it, Hyperlink::render) }

        val lines = splitByNewLines(terms)

        val wrappedLines = wrap(lines, wrapLength, maxLinesCount)

        return wrappedLines
    }

    private fun splitByNewLines(terms: List<Term>): List<List<Term>> {
        val lines = mutableListOf<MutableList<Term>>(mutableListOf())
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

    internal fun fillTextTermGaps(text: String, specialTerms: List<Pair<Term, IntRange>>): List<Term> {
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

    private fun wrapLine(line: List<Term>, wrapLength: Int = -1): List<List<Term>> {
        if (wrapLength <= 0) {
            return listOf(line)
        }

        val wrappedLines = mutableListOf(mutableListOf<Term>())
        line.forEach { term ->
            val availableSpace = wrapLength - wrappedLines.last().sumOf(Term::visualCharCount)
            when {
                term.visualCharCount <= availableSpace -> wrappedLines.last().add(term)
                term.visualCharCount <= wrapLength -> wrappedLines.add(mutableListOf(term)) // no need to split
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
}
