/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel

import org.jetbrains.letsPlot.commons.formatting.number.PowerFormat
import org.jetbrains.letsPlot.commons.intern.json.escape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

data class PowerDegree(
    private val sign: String,
    private val coefficient: String,
    private val base: String,
    private val degree: String,
    private val prefix: String,
    private val postfix: String
) {
    private val multiplier: String
        get() = if (coefficient.isEmpty()) {
            ""
        } else {
            "${coefficient}·"
        }

    fun toSvgTextElement(origin: SvgTextElement): SvgTextElement {
        return if (prefix.isEmpty() && postfix.isEmpty()) {
            toPrettyElement(origin)
        } else {
            toSimpleElement(origin)
        }
    }

    fun toPlainText(): String {
        return "$prefix$sign$multiplier$base$degree$postfix"
    }

    private fun toPrettyElement(origin: SvgTextElement): SvgTextElement {
        val baseTSpan = SvgTSpanElement("$sign$multiplier$base")
        val degreeTSpan = SvgTSpanElement(degree)
        degreeTSpan.setAttribute("baseline-shift", "super")
        degreeTSpan.setAttribute("font-size", "75%")
        val formulaTextElement = SvgTextElement()
        SvgUtils.copyAttributes(origin, formulaTextElement)
        formulaTextElement.addTSpan(baseTSpan)
        formulaTextElement.addTSpan(degreeTSpan)
        return formulaTextElement
    }

    private fun toSimpleElement(origin: SvgTextElement): SvgTextElement {
        val formulaTextElement = SvgTextElement()
        SvgUtils.copyAttributes(origin, formulaTextElement)
        formulaTextElement.addTextNode("$prefix$sign$multiplier$base^($degree)$postfix")
        return formulaTextElement
    }

    companion object {
        fun fromText(text: String): PowerDegree? {
            val powerDegreePattern = """^(?<prefix>.*)\\\(\s*(?<sign>-?)((?<coefficient>\d?)(\s*${PowerFormat.MULTIPLICATION_OPERATOR.escape()}\s*|\s+))?(?<base>\d+)\^(\{\s*)?(?<degree>-?\d+)(\s*\})?\s*\\\)(?<postfix>.*)${'$'}""".toRegex()
            val match = powerDegreePattern.find(text) ?: return null

            val groups = match.groups as MatchNamedGroupCollection
            val sign = groups["sign"]?.value ?: ""
            val coefficient = groups["coefficient"]?.value ?: ""
            val base = groups["base"]!!.value
            val degree = groups["degree"]!!.value
            val prefix = groups["prefix"]?.value ?: ""
            val postfix = groups["postfix"]?.value ?: ""
            return PowerDegree(sign, coefficient, base, degree, prefix, postfix)
        }
    }
}