/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.slim.SvgSlimElements
import jetbrains.datalore.vis.svg.slim.SvgSlimGroup

class RectanglesHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext
) : GeomHelper(pos, coord, ctx) {

    fun createRectangles(rectangleByDataPoint: (DataPointAesthetics) -> DoubleRectangle?): MutableList<SvgNode> {
        val result = ArrayList<SvgNode>()

        for (index in 0 until myAesthetics.dataPointCount()) {
            val p = myAesthetics.dataPointAt(index)
            val clientRect = toClientRect(p, rectangleByDataPoint) ?: continue

            val svgRect = SvgRectElement(clientRect)
            decorate(svgRect, p)

            result.add(svgRect)
        }

        return result
    }

    fun iterateRectangleGeometry(
        rectangleByDataPoint: (DataPointAesthetics) -> DoubleRectangle?,
        iterator: (DataPointAesthetics, DoubleRectangle) -> Unit
    ) {
        for (index in 0 until myAesthetics.dataPointCount()) {
            val p = myAesthetics.dataPointAt(index)
            val rect = toClientRect(p, rectangleByDataPoint)

            rect?.let {
                iterator(p, rect)
            }
        }
    }


    fun createSlimRectangles(rectangleByDataPoint: (DataPointAesthetics) -> DoubleRectangle?): SvgSlimGroup {
        val pointCount = myAesthetics.dataPointCount()
        val group = SvgSlimElements.g(pointCount)

        for (index in 0 until pointCount) {
            val p = myAesthetics.dataPointAt(index)
            val clientRect = toClientRect(p, rectangleByDataPoint) ?: continue

            val slimShape = SvgSlimElements.rect(clientRect.left, clientRect.top, clientRect.width, clientRect.height)
            decorateSlimShape(
                slimShape,
                p
            )
            slimShape.appendTo(group)
        }

        return group
    }
}
