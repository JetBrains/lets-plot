/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.component

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipDefaults
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCircleElement

internal class CircleDecoration : SvgComponent() {
    private val circleIndicators = mutableListOf<SvgCircleElement>()
    private var circleIndicatorSpecs = emptyList<CircleIndicator>()

    override fun buildComponent() {
    }

    fun updateStyle(circleIndicators: List<CircleIndicator>) {
        circleIndicatorSpecs = circleIndicators
    }

    fun update(tooltipCoord: DoubleVector, visible: Boolean) {
        if (visible) {
            updateCircleIndicatorCount(circleIndicatorSpecs.size)
            circleIndicators
                .zip(circleIndicatorSpecs)
                .forEach { (circle, spec) ->
                    val coord = spec.coord.subtract(tooltipCoord)
                    circle.apply {
                        cx().set(coord.x)
                        cy().set(coord.y)
                        r().set(TooltipDefaults.DATA_POINT_MARKER_RADIUS)
                        fillColor().set(spec.fillColor)
                        strokeColor().set(spec.strokeColor)
                        strokeWidth().set(TooltipDefaults.DATA_POINT_MARKER_STROKE_WIDTH)
                    }
                }
        } else {
            updateCircleIndicatorCount(0)
        }
    }

    private fun updateCircleIndicatorCount(count: Int) {
        while (circleIndicators.size > count) {
            rootGroup.children().remove(circleIndicators.removeLast())
        }
        while (circleIndicators.size < count) {
            SvgCircleElement().also { circle ->
                add(circle)
                circleIndicators.add(circle)
            }
        }
    }
    internal data class CircleIndicator(
        val coord: DoubleVector,
        val fillColor: Color,
        val strokeColor: Color
    )
}
