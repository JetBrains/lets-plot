/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.values.Font
import kotlin.math.roundToInt

object RichText {
    fun toSvg(text: String): SvgTextElement {
        val richTextElement = SvgTextElement()
        extractTerms(text)
            .flatMap(Term::toTSpanElements)
            .forEach(richTextElement::addTSpan)
        return richTextElement
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
        val powerTerms = Power.toPowerTerms(text)
        // If there will be another formula terms (like fractionTerms),
        // then join  all of them to the val formulaTerms,
        // sort it by PositionedTerm::range.first and use it further instead of powerTerms
        return if (powerTerms.isEmpty()) {
            listOf(Text(text))
        } else {
            val textTerms = extractTextTerms(text, powerTerms.map { it.range })
            (powerTerms + textTerms).sortedBy { it.range.first }.map(PositionedTerm::term)
        }
    }

    private fun extractTextTerms(text: String, gaps: List<IntRange>): List<PositionedTerm> {
        val firstTextTermPosition = IntRange(0, gaps.first().first - 1)
        val intermediateTextTermPositions = gaps.windowed(2).map { (prevGap, nextGap) ->
            IntRange(prevGap.last + 1, nextGap.first - 1)
        }
        val lastTextTermPosition = IntRange(gaps.last().last + 1, text.length - 1)
        val textTermPositions = listOf(firstTextTermPosition) + intermediateTextTermPositions + listOf(lastTextTermPosition)

        return textTermPositions
            .filter { !it.isEmpty() }
            .map { position -> PositionedTerm(Text(text.substring(position)), position) }
    }

    private class Text(private val text: String) : Term {
        override fun toTSpanElements(): List<SvgTSpanElement> {
            return listOf(SvgTSpanElement(text))
        }

        override fun calculateWidth(widthCalculator: (String, Font) -> Double, font: Font): Double {
            return widthCalculator(text, font)
        }
    }

    private class Power(
        private val base: String,
        private val degree: String
    ) : Term {
        override fun toTSpanElements(): List<SvgTSpanElement> {
            val baseTSpan = SvgTSpanElement(base)
            val degreeTSpan = SvgTSpanElement(degree)
            degreeTSpan.setAttribute(SvgTSpanElement.BASELINE_SHIFT, BaselineShift.SUPER.value)
            degreeTSpan.setAttribute(SvgTSpanElement.FONT_SIZE, "${(SUPERSCRIPT_SIZE_FACTOR * 100).roundToInt()}%")
            return listOf(baseTSpan, degreeTSpan)
        }

        override fun calculateWidth(widthCalculator: (String, Font) -> Double, font: Font): Double {
            val baseWidth = widthCalculator(base, font)
            val degreeFontSize = (font.size * SUPERSCRIPT_SIZE_FACTOR).roundToInt()
            val superscriptFont = Font(font.family, degreeFontSize, font.isBold, font.isItalic)
            val degreeWidth = widthCalculator(degree, superscriptFont)
            return baseWidth + degreeWidth
        }

        companion object {
            private const val SUPERSCRIPT_SIZE_FACTOR = 0.6
            val REGEX = """\\\(\s*(?<base>\d+)\^(\{\s*)?(?<degree>-?\d+)(\s*\})?\s*\\\)""".toRegex()

            fun toPowerTerms(text: String): List<PositionedTerm> {
                return REGEX.findAll(text).map { match ->
                    val groups = match.groups as MatchNamedGroupCollection
                    PositionedTerm(
                        Power(groups["base"]!!.value, groups["degree"]!!.value),
                        match.range
                    )
                }.toList()
            }
        }
    }

    private interface Term {
        fun toTSpanElements(): List<SvgTSpanElement>

        fun calculateWidth(widthCalculator: (String, Font) -> Double, font: Font): Double
    }

    private data class PositionedTerm(val term: Term, val range: IntRange)
}