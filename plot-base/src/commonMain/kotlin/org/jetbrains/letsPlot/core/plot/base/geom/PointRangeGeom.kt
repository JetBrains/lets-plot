/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.legend.CompositeLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.legend.VLineLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.BarTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.extendWidth
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg

class PointRangeGeom : GeomBase() {
    var fattenMidPoint: Double = DEF_FATTEN

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
        helper.setStrokeAlphaEnabled(true)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.POINT_RANGE, ctx)

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y, Aes.YMIN, Aes.YMAX)) {
            val x = p.x()!!
            val y = p.y()!!
            val ymin = p.ymin()!!
            val ymax = p.ymax()!!

            // vertical line
            val start = DoubleVector(x, ymin)
            val end = DoubleVector(x, ymax)
            val line = helper.createLine(start, end, p, strokeScaler = AesScaling::lineWidth)
            if (line == null) continue
            root.add(line)

            // mid-point
            val location = geomHelper.toClient(DoubleVector(x, y), p)!!
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
            clientRectByDataPoint(ctx, geomHelper, fattenMidPoint),
            { HintColorUtil.colorWithAlpha(it) },
            colorMarkerMapper = colorsByDataPoint
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        const val DEF_FATTEN = 5.0

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            fatten: Double
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                if (p.defined(Aes.X) &&
                    p.defined(Aes.Y)
                ) {
                    val x = p.x()!!
                    val y = p.y()!!
                    val shape = p.shape()!!

                    val rect = geomHelper.toClient(
                        DoubleRectangle(DoubleVector(x, y), DoubleVector.ZERO),
                        p
                    )!!

                    val shapeSize = shape.size(p, fatten)
                    val strokeWidth = shape.strokeWidth(p)
                    val width = shapeSize + strokeWidth
                    extendWidth(rect, width, ctx.flipped)
                } else {
                    null
                }
            }
        }
    }
}
