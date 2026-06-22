/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.component

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipDefaults
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGraphicsElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

internal class PointerDecoration : SvgComponent() {
    private val pointer = SvgPathElement()

    override fun buildComponent() {
        add(pointer)
    }

    fun updateStyle(style: Style) {
        pointer.apply {
            val fill = if (style.fillColor == style.strokeColor) style.borderColor else style.fillColor
            fillColor().set(fill)
            strokeWidth().set(1.0)
            strokeColor().set(style.strokeColor)
        }
    }

    fun update(pointerCoord: DoubleVector, visible: Boolean) {
        if (visible) {
            pointer.d().set(trianglePointer(pointerCoord).build())
            SvgUtils.transformRotate(
                pointer,
                -2 * TooltipDefaults.ROTATION_ANGLE,
                pointerCoord.x,
                pointerCoord.y
            )
            pointer.visibility().set(SvgGraphicsElement.Visibility.VISIBLE)
        } else {
            pointer.visibility().set(SvgGraphicsElement.Visibility.HIDDEN)
        }
    }

    private fun trianglePointer(pointerCoord: DoubleVector) = SvgPathDataBuilder().apply {
        val xy = TRIANGLE_POINTS.map { it.add(pointerCoord) }
        moveTo(xy[0])
        xy.forEach(::lineTo)
        closePath()
    }

    internal data class Style(
        val fillColor: Color,
        val borderColor: Color,
        val strokeColor: Color
    )

    companion object {
        private val TRIANGLE_POINTS: List<DoubleVector> = run {
            val size = 8.0
            val height = size + 1.0
            listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(size / 2, height),
                DoubleVector(-size / 2, height)
            )
        }
    }

}
