/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms.AdaptiveResampler.Companion.resample
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimGroup

class RectanglesHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val geometryFactory: (DataPointAesthetics) -> DoubleRectangle?
) : GeomHelper(pos, coord, ctx) {
    fun createNonLinearRectangles(handler: (DataPointAesthetics, SvgNode, List<DoubleVector>) -> Unit) {
        myAesthetics.dataPoints().forEach { p ->
            geometryFactory(p)?.let { rect ->
                val polyRect = resample(
                    precision = 0.5,
                    points = listOf(
                        DoubleVector(rect.left, rect.top),
                        DoubleVector(rect.right, rect.top),
                        DoubleVector(rect.right, rect.bottom),
                        DoubleVector(rect.left, rect.bottom),
                        DoubleVector(rect.left, rect.top)
                    )
                ) { toClient(it, p) }

                val svgPathData = SvgPathDataBuilder()
                polyRect.first().let(svgPathData::moveTo)
                polyRect.drop(1).forEach(svgPathData::lineTo)

                val svgPoly = SvgPathElement(svgPathData.build())
                decorate(svgPoly, p)
                handler(p, svgPoly, polyRect)
            }
        }
    }

    fun createRectangles(handler: (DataPointAesthetics, SvgNode, DoubleRectangle) -> Unit) {
        myAesthetics.dataPoints().forEach { p ->
            geometryFactory(p)?.let { rect ->
                val clientRect = toClient(rect, p) ?: return@let
                val svgRect = SvgRectElement(clientRect)
                decorate(svgRect, p)
                handler(p, svgRect, clientRect)
            }
        }
    }

    fun createRectangles(): MutableList<SvgNode> {
        val result = ArrayList<SvgNode>()

        for (index in 0 until myAesthetics.dataPointCount()) {
            val p = myAesthetics.dataPointAt(index)
            val clientRect = geometryFactory(p) ?: continue

            val svgRect = SvgRectElement(clientRect)
            decorate(svgRect, p)

            result.add(svgRect)
        }

        return result
    }

    fun iterateRectangleGeometry(
        iterator: (DataPointAesthetics, DoubleRectangle) -> Unit
    ) {
        for (index in 0 until myAesthetics.dataPointCount()) {
            val p = myAesthetics.dataPointAt(index)
            geometryFactory(p)?.let { rect ->
                iterator(p, rect)
            }
        }
    }

    fun createSlimRectangles(): SvgSlimGroup {
        val pointCount = myAesthetics.dataPointCount()
        val group = SvgSlimElements.g(pointCount)

        for (index in 0 until pointCount) {
            val p = myAesthetics.dataPointAt(index)
            val clientRect = geometryFactory(p) ?: continue

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
