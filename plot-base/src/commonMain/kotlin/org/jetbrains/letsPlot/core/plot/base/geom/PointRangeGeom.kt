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
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.VerticalGeomHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg

class PointRangeGeom : GeomBase() {
    var fattenMidPoint: Double = DEF_FATTEN
    private val verticalHelper = VerticalGeomHelper()

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

        for (p in aesthetics.dataPoints()) {
            val x = p.finiteOrNull(Aes.X) ?: continue
            val y = p.finiteOrNull(Aes.Y) ?: continue
            val ymin = p.finiteOrNull(Aes.YMIN) ?: continue
            val ymax = p.finiteOrNull(Aes.YMAX) ?: continue
            val shape = p.shape() ?: continue

            // vertical line
            val start = DoubleVector(x, ymin)
            val end = DoubleVector(x, ymax)
            val (svg) = helper.createLine(start, end, p, strokeScaler = AesScaling::lineWidth) ?: continue
            root.add(svg)

            // mid-point
            val location = geomHelper.toClient(DoubleVector(x, y), p)!!
            val o = PointShapeSvg.create(shape, location, p, fattenMidPoint)
            root.add(wrap(o))
        }

        verticalHelper.buildHints(
            listOf(Aes.YMIN, Aes.YMAX),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, fattenMidPoint),
            { HintColorUtil.colorWithAlpha(it) },
            colorMarkerMapper = colorsByDataPoint
        )
    }

    private fun clientRectByDataPoint(
        ctx: GeomContext,
        geomHelper: GeomHelper,
        fatten: Double
    ): (DataPointAesthetics) -> DoubleRectangle? {
        fun factory(p: DataPointAesthetics) : DoubleRectangle? {
            val x = p.finiteOrNull(Aes.X) ?: return null
            val y = p.finiteOrNull(Aes.Y) ?: return null
            val shape = p.shape() ?: return null

            val rect = DoubleRectangle(DoubleVector(x, y), DoubleVector.ZERO)
                .let { geomHelper.toClient(it, p) }!!

            val shapeSize = shape.size(p, fatten)
            val strokeWidth = shape.strokeWidth(p)
            val width = shapeSize + strokeWidth
            return GeomUtil.extendWidth(rect, width, ctx.flipped)
        }

        return ::factory
    }

    companion object {
        const val HANDLES_GROUPS = false
        const val DEF_FATTEN = 5.0
    }
}
