/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimGroup

class RectanglesHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext
) : GeomHelper(pos, coord, ctx) {

    fun createRectangles(clientRectByDataPoint: (DataPointAesthetics) -> DoubleRectangle?): MutableList<SvgNode> {
        val result = ArrayList<SvgNode>()

        for (index in 0 until myAesthetics.dataPointCount()) {
            val p = myAesthetics.dataPointAt(index)
            val clientRect = clientRectByDataPoint(p) ?: continue

            val svgRect = SvgRectElement(clientRect)
            decorate(svgRect, p)

            result.add(svgRect)
        }

        return result
    }

    fun iterateRectangleGeometry(
        clientRectByDataPoint: (DataPointAesthetics) -> DoubleRectangle?,
        iterator: (DataPointAesthetics, DoubleRectangle) -> Unit
    ) {
        for (index in 0 until myAesthetics.dataPointCount()) {
            val p = myAesthetics.dataPointAt(index)
            clientRectByDataPoint(p)?.let { rect ->
                iterator(p, rect)
            }
        }
    }

    fun createSlimRectangles(clientRectByDataPoint: (DataPointAesthetics) -> DoubleRectangle?): SvgSlimGroup {
        val pointCount = myAesthetics.dataPointCount()
        val group = SvgSlimElements.g(pointCount)

        for (index in 0 until pointCount) {
            val p = myAesthetics.dataPointAt(index)
            val clientRect = clientRectByDataPoint(p) ?: continue

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
