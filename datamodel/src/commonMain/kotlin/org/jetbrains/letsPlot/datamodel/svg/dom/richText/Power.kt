/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.richText

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.BaselineShift
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement.Companion.BASELINE_SHIFT
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement.Companion.FONT_SIZE
import kotlin.math.roundToInt

data class Power(
    private val base: String,
    private val degree: String
) : Term {
    override fun toSvg(): List<SvgTSpanElement> {
        val baseTSpan = SvgTSpanElement(base)
        val degreeTSpan = SvgTSpanElement(degree)
        degreeTSpan.setAttribute(BASELINE_SHIFT, BaselineShift.SUPER.value)
        degreeTSpan.setAttribute(FONT_SIZE, "${(SUPERSCRIPT_SIZE_FACTOR * 100).roundToInt()}%")
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
        const val PATTERN = """\\\(\s*(?<base>\d+)\^(\{\s*)?(?<degree>-?\d+)(\s*\})?\s*\\\)"""
    }
}