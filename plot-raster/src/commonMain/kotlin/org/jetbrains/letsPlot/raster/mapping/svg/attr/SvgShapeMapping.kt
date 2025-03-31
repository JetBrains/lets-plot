/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg.attr

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.raster.mapping.svg.SvgUtils.toColor
import org.jetbrains.letsPlot.raster.shape.Figure

internal abstract class SvgShapeMapping<TargetT : Figure> : SvgAttrMapping<TargetT>() {
    override fun setAttribute(target: TargetT, name: String, value: Any?) {
        when (name) {
            SvgShape.FILL.name -> target.fill = toColor(value)
            SvgShape.FILL_OPACITY.name -> target.fillOpacity = value!!.asFloat
            SvgShape.STROKE.name -> target.stroke = toColor(value)
            SvgShape.STROKE_OPACITY.name -> target.strokeOpacity = value!!.asFloat
            SvgShape.STROKE_WIDTH.name -> target.strokeWidth = value!!.asFloat
            SvgShape.STROKE_DASHOFFSET.name -> target.strokeDashOffset = value?.asFloat ?: 0f
            SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE -> {
                val strokeDashArray = (value as String).split(",").map(String::toDouble)
                target.strokeDashArray = strokeDashArray
            }

            else -> super.setAttribute(target, name, value)
        }
    }
}
