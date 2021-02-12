/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.legend.CompositeLegendKeyElementFactory
import jetbrains.datalore.plot.base.geom.legend.VLineLegendKeyElementFactory
import jetbrains.datalore.plot.base.geom.util.BarTooltipHelper
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.PointShapeSvg

class PointRangeGeom : GeomBase() {
    var fattenMidPoint: Double =
        DEF_FATTEN

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = CompositeLegendKeyElementFactory(
            VLineLegendKeyElementFactory(),
            PointLegendKeyElementFactory(DEF_FATTEN)
        )


    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = geomHelper.createSvgElementHelper()
        val dataPoints = aesthetics.dataPoints().filter { p -> rectangleByDataPoint(p, fattenMidPoint, coord) != null }

        for (p in GeomUtil.withDefined(dataPoints, Aes.X, Aes.Y, Aes.YMIN, Aes.YMAX)) {
            val x = p.x()!!
            val y = p.y()!!
            val ymin = p.ymin()!!
            val ymax = p.ymax()!!

            // vertical line
            val start = DoubleVector(x, ymin)
            val end = DoubleVector(x, ymax)
            if (coord.contains(start) && coord.contains(end)) {
                val line = helper.createLine(start, end, p)
                root.add(line)
            }
            // mid-point
            val location = geomHelper.toClient(DoubleVector(x, y), p)
            val shape = p.shape()!!
            val o = PointShapeSvg.create(shape, location, p, fattenMidPoint)
            root.add(wrap(o))
//            ctx.targetCollector.addPoint(
//                p.index(),
//                location,
//                shape.size(p) * fattenMidline / 2,
//                PointGeom.tooltipParams(p)
//            )
        }

        BarTooltipHelper.collectRectangleTargets(
            listOf(Aes.YMAX, Aes.YMIN),
            aesthetics, pos, coord, ctx,
            rectangleByDataPoint(fattenMidPoint, coord),
            { HintColorUtil.fromColor(it) }
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        const val DEF_FATTEN = 5.0

        fun rectangleByDataPoint(fatten: Double, coord: CoordinateSystem): (DataPointAesthetics) -> DoubleRectangle? {
            return { p -> rectangleByDataPoint(p, fatten, coord) }
        }

        fun rectangleByDataPoint(p: DataPointAesthetics, fatten: Double, coord: CoordinateSystem): DoubleRectangle? {
            var result: DoubleRectangle? = null
            if (p.defined(Aes.X) &&
                p.defined(Aes.Y)
            ) {
                val x = p.x()!!
                val y = p.y()!!

                val shape = p.shape()!!
                val shapeSize = shape.size(p) * fatten
                val strokeWidth = shape.strokeWidth(p)
                val width = shapeSize + strokeWidth

                val origin = DoubleVector(x - width / 2, y)
                val dimensions = DoubleVector(width, 0.0 )
                val rect = DoubleRectangle(origin, dimensions)
                if (coord.contains(rect)) {
                    result = rect
                }
            }
            return result
        }
    }
}
