package jetbrains.datalore.visualization.plot.gog.core.render.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.base.svg.SvgNode
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimGroup
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.CoordinateSystem
import jetbrains.datalore.visualization.plot.gog.core.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.GeomContext
import jetbrains.datalore.visualization.plot.gog.core.render.PositionAdjustment

import java.util.ArrayList
import java.util.function.BiConsumer
import java.util.function.Function

class RectanglesHelper(private val myAesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) : GeomHelper(pos, coord, ctx) {

    fun createRectangles(rectangleByDataPoint: (DataPointAesthetics) -> DoubleRectangle?): List<SvgNode> {
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

    fun iterateRectangleGeometry(rectangleByDataPoint: (DataPointAesthetics) -> DoubleRectangle,
                                 iterator: BiConsumer<DataPointAesthetics, DoubleRectangle>) {
        for (index in 0 until myAesthetics.dataPointCount()) {
            val p = myAesthetics.dataPointAt(index)
            iterator.accept(p, toClientRect(p, rectangleByDataPoint)!!)
        }
    }


    fun createSlimRectangles(rectangleByDataPoint: (DataPointAesthetics) -> DoubleRectangle): SvgSlimGroup {
        val pointCount = myAesthetics.dataPointCount()
        val group = SvgSlimElements.g(pointCount)

        for (index in 0 until pointCount) {
            val p = myAesthetics.dataPointAt(index)
            val clientRect = toClientRect(p, rectangleByDataPoint) ?: continue

            val slimShape = SvgSlimElements.rect(clientRect!!.left, clientRect!!.top, clientRect!!.width, clientRect!!.height)
            decorateSlimShape(slimShape, p)
            slimShape.appendTo(group)
        }

        return group
    }
}
