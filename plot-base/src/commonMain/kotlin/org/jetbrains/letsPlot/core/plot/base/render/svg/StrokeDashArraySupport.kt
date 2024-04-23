/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

/**
 * The counterpart of SVG 'stroke-dasharray' attribute but
 * length of alternating dashes and gaps
 * is defined as multiples of line width
 */
object StrokeDashArraySupport {
    fun apply(element: SvgShape, strokeWidth: Double, lineType: LineType) {
        val dashArray = toStrokeDashArray(strokeWidth, lineType)
        if (dashArray != null) {
            element.strokeDashArray().set(dashArray)
        }
    }

    fun apply(element: SvgSlimShape, strokeWidth: Double, lineType: LineType) {
        val dashArray = toStrokeDashArray(strokeWidth, lineType)
        if (dashArray != null) {
            element.setStrokeDashArray(dashArray)
        }
    }

    private fun toStrokeDashArray(strokeWidth: Double, lineType: LineType): String? {
        if (lineType.isBlank || lineType.isSolid) {
            return null
        }
        val sb = StringBuilder()
        lineType.dashArray.forEach { relativeLength ->
            val length = relativeLength * strokeWidth
            if (sb.isNotEmpty()) {
                sb.append(',')
            }
            sb.append(length.toString())
        }
        return sb.toString()
    }
}
