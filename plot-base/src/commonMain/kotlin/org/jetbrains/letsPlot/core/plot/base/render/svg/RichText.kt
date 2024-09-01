/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt

object RichText {
    fun toSvg(
        text: String,
        wrapLength: Int = -1,
        maxLinesCount: Int = -1
    ): List<SvgTextElement> {
        val lines = text.split("\n")
            .map(::extractTerms)
            .map { termsLine -> wrap(termsLine, wrapLength, maxLinesCount) }

        return lines.flatten()
    }

    private fun wrap(termsLine: List<Term>, wrapLength: Int = -1, maxLinesCount: Int = -1): List<SvgTextElement> {
        if (wrapLength <= 0) {
            val svgText = SvgTextElement().apply {
                addTSpan(termsLine.flatMap(Term::svg))
            }
            return listOf(svgText)
        }

        val wrappedLines = mutableListOf(mutableListOf<Term>())
        termsLine.forEach { term ->
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

        return when {
            maxLinesCount < 0 -> wrappedLines
            wrappedLines.size < maxLinesCount -> wrappedLines
            else -> wrappedLines.dropLast(wrappedLines.size - maxLinesCount) + mutableListOf(mutableListOf(Text("...")))
        }
            .map { line -> line.flatMap(Term::svg) }
            .map { tspanElements ->
                SvgTextElement().apply {
                    addTSpan(tspanElements)
                }
            }
    }


    fun enrichWidthCalculator(widthCalculator: (String, Font) -> Double): (String, Font) -> Double {
        fun enrichedWidthCalculator(text: String, font: Font): Double {
            return extractTerms(text).sumOf { term ->
                term.calculateWidth(widthCalculator, font)
            }
        }
        return ::enrichedWidthCalculator
    }

    private fun extractTerms(text: String): List<Term> {
        val specialTerms = Power.toPowerTerms(text) + Link.parse(text)
        return if (specialTerms.isEmpty()) {
            listOf(Text(text))
        } else {
            val textTerms = subtractRange(text.indices, specialTerms.map { (_, position) -> position })
                .map { position -> Text(text.substring(position)) to position }
            (specialTerms + textTerms)
                .sortedBy { (_, position) -> position.first }
                .map { (term, _) -> term }
        }
    }

    private fun subtractRange(range: IntRange, toSubtract: List<IntRange>): List<IntRange> {
        val sortedToSubtract = toSubtract.sortedBy(IntRange::first)
        val firstRange = IntRange(range.first, sortedToSubtract.first().first - 1)
        val intermediateRanges = sortedToSubtract.windowed(2).map { (prevRange, nextRange) ->
            IntRange(prevRange.last + 1, nextRange.first - 1)
        }
        val lastRange = IntRange(sortedToSubtract.last().last + 1, range.last)

        return (listOf(firstRange) + intermediateRanges + listOf(lastRange)).filterNot(IntRange::isEmpty)
    }

    private class Text(
        val text: String,
    ) : Term {
        override val visualCharCount: Int = text.length
        override val svg: List<SvgTSpanElement> = listOf(SvgTSpanElement(text))

        override fun calculateWidth(widthCalculator: (String, Font) -> Double, font: Font): Double {
            return widthCalculator(text, font)
        }
    }

    private class Link private constructor(
        private val text: String,
        private val href: String,
    ) : Term {
        override val visualCharCount: Int = text.length
        override val svg: List<SvgTSpanElement> = listOf(
            SvgTSpanElement(text).apply {
                fillColor().set(Colors.forName("blue")) // TODO: do not hardcode color
                setAttribute("lp-href", href)
            })

        override fun calculateWidth(widthCalculator: (String, Font) -> Double, font: Font): Double {
            return widthCalculator(text, font)
        }

        companion object {
            private val aTagRegex = "<a\\s+[^>]*href=\"([^\"]*)\"[^>]*>([^<]*)<\\/a>".toRegex()

            fun parse(text: String): List<Pair<Term, IntRange>> {
                val res = aTagRegex.findAll(text).map { tag ->
                    val (href, label) = tag.destructured
                    Link(label, href) to tag.range
                }.toList()
                return res
            }
        }
    }

    private class Power(
        private val base: String,
        private val degree: String,
    ) : Term {
        override val visualCharCount: Int = base.length + degree.length
        override val svg: List<SvgTSpanElement>

        init {
            val baseTSpan = SvgTSpanElement(base)
            val indentTSpan = SvgTSpanElement(INDENT_SYMBOL).apply {
                setAttribute(SvgTSpanElement.FONT_SIZE, "${INDENT_SIZE_FACTOR}em")
            }
            val degreeTSpan = SvgTSpanElement(degree).apply {
                setAttribute(SvgTSpanElement.FONT_SIZE, "${SUPERSCRIPT_SIZE_FACTOR}em")
                setAttribute(SvgTSpanElement.DY, "-${SUPERSCRIPT_RELATIVE_SHIFT}em")
            }
            // The following tspan element is used to restore the baseline after the degree
            // Restoring works only if there is some symbol after the degree, so we use ZERO_WIDTH_SPACE_SYMBOL
            // It could be considered as standard trick, see https://stackoverflow.com/a/65681504
            // Attribute 'baseline-shift' is better suited for such usecase -
            // it doesn't require to add an empty tspan at the end to restore the baseline (as 'dy').
            // Sadly we can't use 'baseline-shift' as it is not supported by CairoSVG.
            val restoreBaselineTSpan = SvgTSpanElement(ZERO_WIDTH_SPACE_SYMBOL).apply {
                // Size of shift depends on the font size, and it should be equal to the superscript shift size
                setAttribute(SvgTSpanElement.FONT_SIZE, "${SUPERSCRIPT_SIZE_FACTOR}em")
                setAttribute(SvgTSpanElement.DY, "${SUPERSCRIPT_RELATIVE_SHIFT}em")
            }

            svg = listOf(baseTSpan, indentTSpan, degreeTSpan, restoreBaselineTSpan)
        }

        override fun calculateWidth(widthCalculator: (String, Font) -> Double, font: Font): Double {
            val baseWidth = widthCalculator(base, font)
            val degreeFontSize = (font.size * SUPERSCRIPT_SIZE_FACTOR).roundToInt()
            val superscriptFont = Font(font.family, degreeFontSize, font.isBold, font.isItalic)
            val degreeWidth = widthCalculator(degree, superscriptFont)
            return baseWidth + degreeWidth
        }

        companion object {
            private const val ZERO_WIDTH_SPACE_SYMBOL = "\u200B"
            private const val INDENT_SYMBOL = " "
            private const val INDENT_SIZE_FACTOR = 0.1
            private const val SUPERSCRIPT_SIZE_FACTOR = 0.7
            private const val SUPERSCRIPT_RELATIVE_SHIFT = 0.4
            val REGEX = """\\\(\s*(?<base>\d+)\^(\{\s*)?(?<degree>-?\d+)(\s*\})?\s*\\\)""".toRegex()

            fun toPowerTerms(text: String): List<Pair<Term, IntRange>> {
                return REGEX.findAll(text).map { match ->
                    val groups = match.groups as MatchNamedGroupCollection
                    Power(groups["base"]!!.value, groups["degree"]!!.value) to match.range
                }.toList()
            }
        }
    }

    private interface Term {
        val visualCharCount: Int // in chars, used for line wrapping
        val svg: List<SvgTSpanElement>

        fun calculateWidth(widthCalculator: (String, Font) -> Double, font: Font): Double
    }
}