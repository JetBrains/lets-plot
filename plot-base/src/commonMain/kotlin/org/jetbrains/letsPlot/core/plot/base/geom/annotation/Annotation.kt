/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.annotation

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.tooltip.LineSpec
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

@Suppress("RedundantConstructorKeyword")
class Annotation constructor(
    private val lines: List<LineSpec>,
    val textStyle: TextStyle,
    private val useCustomColor: Boolean,
    private val useLayerColor: Boolean
) {
    fun getAnnotationText(index: Int, ctx: PlotContext): String {
        return lines
            .mapNotNull { line -> line.getDataPoint(index, ctx) }
            .joinToString(transform = LineSpec.DataPoint::value, separator = "\n")
    }

    fun getTextColor(layerColor: Color?, layerFill: Color?): Color {
        return when {
            useLayerColor -> layerColor ?: textStyle.color
            useCustomColor -> textStyle.color
            layerFill == null -> textStyle.color
            else -> AnnotationUtil.chooseColor(layerFill)
        }
    }
}