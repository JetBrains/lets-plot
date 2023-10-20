/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.formula

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

class Formula private constructor(private val terms: List<RichTerm>) {
    fun toSvgTextElement(origin: SvgTextElement): SvgTextElement {
        val formulaTextElement = SvgTextElement()
        SvgUtils.copyAttributes(origin, formulaTextElement)
        terms.forEach { (term, _) ->
            term.toSvg().forEach { tSpan ->
                formulaTextElement.addTSpan(tSpan)
            }
        }
        return formulaTextElement
    }

    fun getWidthCalculator(labelWidthCalculator: (String, Font) -> Double): (Font) -> Double {
        return { font ->
            terms.sumOf { (term, _) ->
                term.getWidthCalculator(labelWidthCalculator)(font)
            }
        }
    }

    fun getHeight(labelHeight: Double): Double {
        return terms.maxOf { (term, _) ->
            term.getHeight(labelHeight)
        }
    }

    data class RichTerm(val term: Term, val range: IntRange)

    companion object {
        fun fromText(text: String): Formula {
            val powerDegreeTerms: List<RichTerm> = PowerDegree.POWER_DEGREE_PATTERN.toRegex().findAll(text).map { match ->
                val groups = match.groups as MatchNamedGroupCollection
                RichTerm(
                    PowerDegree(groups["base"]!!.value, groups["degree"]!!.value),
                    match.range
                )
            }.toList()
            if (powerDegreeTerms.isEmpty()) {
                return Formula(listOf(RichTerm(Text(text), text.indices)))
            }
            val textTerms: List<RichTerm> = (listOf(
                RichTerm(
                    Text(text.substring(0, powerDegreeTerms.first().range.first)),
                    0 until powerDegreeTerms.first().range.first
                )
            ) + powerDegreeTerms.windowed(2).map { (term1, term2) ->
                RichTerm(
                    Text(text.substring(term1.range.last + 1, term2.range.first)),
                    term1.range.last + 1 until term2.range.first
                )
            } + listOf(
                RichTerm(
                    Text(text.substring(powerDegreeTerms.last().range.last + 1, text.length)),
                    powerDegreeTerms.last().range.last + 1 until text.length
                )
            )).filter { !it.range.isEmpty() }
            return Formula((powerDegreeTerms + textTerms).sortedBy { it.range.first })
        }
    }
}