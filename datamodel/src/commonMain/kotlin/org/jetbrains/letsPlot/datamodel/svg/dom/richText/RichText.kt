/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.richText

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

class RichText private constructor(private val terms: List<Term>) {
    fun toSvgTextElement(origin: SvgTextElement): SvgTextElement {
        val richTextElement = SvgTextElement()
        SvgUtils.copyAttributes(origin, richTextElement)
        terms.forEach { term ->
            term.toSvg().forEach { tSpan ->
                richTextElement.addTSpan(tSpan)
            }
        }
        return richTextElement
    }

    fun getWidthCalculator(labelWidthCalculator: (String, Font) -> Double): (Font) -> Double {
        return { font ->
            terms.sumOf { term ->
                term.getWidthCalculator(labelWidthCalculator)(font)
            }
        }
    }

    fun getHeight(labelHeight: Double): Double {
        return terms.maxOf { term ->
            term.getHeight(labelHeight)
        }
    }

    data class PositionedTerm(val term: Term, val range: IntRange)

    companion object {
        fun fromText(text: String): RichText {
            val powerTerms = extractPowerTerms(text)
            // If there will be another formula terms (like fractionTerms),
            // then join  all of them to the val formulaTerms,
            // sort it by PositionedTerm::range.first and use it further instead of powerTerms
            val allTerms = if (powerTerms.isEmpty()) {
                listOf(Text(text))
            } else {
                val textTerms = extractTextTerms(text, powerTerms)
                (powerTerms + textTerms).sortedBy { it.range.first }.map(PositionedTerm::term)
            }
            return RichText(allTerms)
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
    }
}