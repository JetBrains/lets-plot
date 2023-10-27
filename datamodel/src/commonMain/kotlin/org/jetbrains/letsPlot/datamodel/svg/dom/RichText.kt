/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.values.Font
import kotlin.math.roundToInt

object RichText {
    fun enrichText(origin: SvgTextElement): SvgTextElement {
        val text = (origin.children()[0] as? SvgTextNode)?.textContent()?.get() ?: return origin
        val allTerms = extractTerms(text)
        val richTextElement = SvgTextElement()
        SvgUtils.copyAttributes(origin, richTextElement)
        allTerms.forEach { term ->
            term.toSvg().forEach { tSpan ->
                richTextElement.addTSpan(tSpan)
            }
        }
        return richTextElement
    }

    fun getWidthCalculator(text: String, labelWidthCalculator: (String, Font) -> Double): (Font) -> Double {
        return { font ->
            extractTerms(text).sumOf { term ->
                term.getWidthCalculator(labelWidthCalculator)(font)
            }
        }
    }

    fun getHeight(text: String, labelHeight: Double): Double {
        return extractTerms(text).maxOfOrNull { term ->
            term.getHeight(labelHeight)
        } ?: 0.0
    }

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
        override fun toSvg(): List<SvgTSpanElement> {
            return listOf(SvgTSpanElement(text))
        }

        override fun getWidthCalculator(labelWidthCalculator: (String, Font) -> Double): (Font) -> Double {
            return { font ->
                labelWidthCalculator(text, font)
            }
        }

        override fun getHeight(labelHeight: Double): Double {
            return labelHeight
        }
    }

    private class Power(
        private val base: String,
        private val degree: String
    ) : Term {
        override fun toSvg(): List<SvgTSpanElement> {
            val baseTSpan = SvgTSpanElement(base)
            val degreeTSpan = SvgTSpanElement(degree)
            degreeTSpan.setAttribute(SvgTSpanElement.BASELINE_SHIFT, BaselineShift.SUPER.value)
            degreeTSpan.setAttribute(SvgTSpanElement.FONT_SIZE, "${(SUPERSCRIPT_SIZE_FACTOR * 100).roundToInt()}%")
            return listOf(baseTSpan, degreeTSpan)
        }

        override fun getWidthCalculator(labelWidthCalculator: (String, Font) -> Double): (Font) -> Double {
            return { font ->
                val baseWidth = labelWidthCalculator(base, font)
                val superscriptFont = Font(font.family, (font.size * SUPERSCRIPT_SIZE_FACTOR).roundToInt(), font.isBold, font.isItalic)
                val degreeWidth = labelWidthCalculator(degree, superscriptFont)
                baseWidth + degreeWidth
            }
        }

        override fun getHeight(labelHeight: Double): Double {
            return 3 * labelHeight / 2
        }

        companion object {
            const val SUPERSCRIPT_SIZE_FACTOR = 0.75
            val REGEX = """\\\(\s*(?<base>\d+)\^(\{\s*)?(?<degree>-?\d+)(\s*\})?\s*\\\)""".toRegex()
        }
    }

    private interface Term {
        fun toSvg(): List<SvgTSpanElement>

        fun getWidthCalculator(labelWidthCalculator: (String, Font) -> Double): (Font) -> Double

        fun getHeight(labelHeight: Double): Double
    }

    private data class PositionedTerm(val term: Term, val range: IntRange)
}