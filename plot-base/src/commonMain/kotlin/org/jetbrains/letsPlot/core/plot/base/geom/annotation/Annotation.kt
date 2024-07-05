/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

class Annotation constructor(
    private val lines: List<LineSpec>,
    val textStyle: TextStyle,
    private val useCustomColor: Boolean
) {
    fun getAnnotationText(index: Int, ctx: PlotContext): String {
        return lines
            .mapNotNull { line -> line.getDataPoint(index, ctx) }
            .joinToString(transform = LineSpec.DataPoint::value, separator = "\n")
    }

    fun getTextColor(background: Color? = null): Color {
        return when {
            useCustomColor -> textStyle.color
            background == null -> textStyle.color
            else -> AnnotationUtil.chooseColor(background)
        }
    }
}