/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.values.Font
import kotlin.math.roundToInt

object RichText {
    fun enrichText(origin: SvgTextElement): SvgTextElement {
        // Process only the case when originally it was a plain text
        if (origin.children().size != 1) {
            return origin
        }
        val text = (origin.children()[0] as? SvgTextNode)?.textContent()?.get() ?: return origin
        val allTerms = extractTerms(text)
        val richTextElement = SvgTextElement()
        SvgUtils.copyAttributes(origin, richTextElement)
        allTerms.forEach { term ->
            term.toSvgElements().forEach(richTextElement::addTSpan)
        }
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

    fun getHeightStretchFactor(text: String): Double = extractTerms(text).maxOfOrNull(Term::heightStretchFactor) ?: 1.0

    private fun extractTerms(text: String): List<Term> {
        val powerTerms = extractPowerTerms(text)
        // If there will be another formula terms (like fractionTerms),
        // then join  all of them to the val formulaTerms,
        // sort it by PositionedTerm::range.first and use it further instead of powerTerms
        return if (powerTerms.isEmpty()) {
            listOf(Text(text))
        } else {
            val textTerms = extractTextTerms(text, powerTerms)
            (powerTerms + textTerms).sortedBy { it.range.first }.map(PositionedTerm::term)
        }
    }

    private fun extractPowerTerms(text: String): List<PositionedTerm> {
        return Power.REGEX.findAll(text).map { match ->
            val groups = match.groups as MatchNamedGroupCollection
            PositionedTerm(
                Power(groups["base"]!!.value, groups["degree"]!!.value),
                match.range
            )
        }.toList()
    }

    private fun extractTextTerms(text: String, formulaTerms: List<PositionedTerm>): List<PositionedTerm> {
        val prefixTextTerm = PositionedTerm(
            Text(text.substring(0, formulaTerms.first().range.first)),
            0 until formulaTerms.first().range.first
        )
        val infixTextTerms = formulaTerms.windowed(2).map { (term1, term2) ->
            PositionedTerm(
                Text(text.substring(term1.range.last + 1, term2.range.first)),
                term1.range.last + 1 until term2.range.first
            )
        }
        val postfixTextTerm = PositionedTerm(
            Text(text.substring(formulaTerms.last().range.last + 1, text.length)),
            formulaTerms.last().range.last + 1 until text.length
        )

        return (listOf(prefixTextTerm) + infixTextTerms + listOf(postfixTextTerm))
            .filter { !it.range.isEmpty() }
    }

    private class Text(private val text: String) : Term {
        override val heightStretchFactor = 1.0

        override fun toSvgElements(): List<SvgTSpanElement> {
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
        override val heightStretchFactor = 1.0 + SUPERSCRIPT_SIZE_FACTOR / 2.0

        override fun toSvgElements(): List<SvgTSpanElement> {
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
            private const val SUPERSCRIPT_SIZE_FACTOR = 0.75
            val REGEX = """\\\(\s*(?<base>\d+)\^(\{\s*)?(?<degree>-?\d+)(\s*\})?\s*\\\)""".toRegex()
        }
    }

    private interface Term {
        val heightStretchFactor: Double

        fun toSvgElements(): List<SvgTSpanElement>

        fun calculateWidth(widthCalculator: (String, Font) -> Double, font: Font): Double
    }

    private data class PositionedTerm(val term: Term, val range: IntRange)
}