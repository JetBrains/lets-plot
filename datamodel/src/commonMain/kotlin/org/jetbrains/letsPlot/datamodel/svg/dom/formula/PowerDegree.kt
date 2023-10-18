/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.formula

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import kotlin.math.roundToInt

data class PowerDegree(
    private val base: String,
    private val degree: String
) : Term {
    override fun toSvg(): List<SvgTSpanElement> {
        val baseTSpan = SvgTSpanElement(base)
        val degreeTSpan = SvgTSpanElement(degree)
        degreeTSpan.setAttribute("baseline-shift", "super")
        degreeTSpan.setAttribute("font-size", "${(SUPERSCRIPT_SIZE_FACTOR * 100).roundToInt()}%")
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

    companion object {
        const val SUPERSCRIPT_SIZE_FACTOR = 0.75
        const val POWER_DEGREE_PATTERN = """\\\(\s*(?<base>\d+)\^(\{\s*)?(?<degree>-?\d+)(\s*\})?\s*\\\)"""
    }
}