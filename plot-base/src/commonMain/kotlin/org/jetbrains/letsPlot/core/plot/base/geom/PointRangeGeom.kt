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
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper.Companion.decorate
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectangleTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

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
        val tooltipHelper = RectangleTooltipHelper(
            pos = pos,
            coord = coord,
            ctx = ctx,
            hintAesList = listOf(Aes.YMIN, Aes.YMAX),
            fillColorMapper = { HintColorUtil.colorWithAlpha(it) },
            colorMarkerMapper = colorsByDataPoint
        )

        for (p in aesthetics.dataPoints()) {
            val x = p.finiteOrNull(Aes.X) ?: continue
            val ymin = p.finiteOrNull(Aes.YMIN) ?: continue
            val ymax = p.finiteOrNull(Aes.YMAX) ?: continue
            val shape = p.shape() ?: continue

            // vertical line
            val start = DoubleVector(x, ymin)
            val end = DoubleVector(x, ymax)
            val (svg) = helper.createLine(start, end, p, strokeScaler = AesScaling::lineWidth) ?: continue
            root.add(svg)

            // mid-point
            val y = p.finiteOrNull(Aes.Y) ?: continue
            val location = geomHelper.toClient(DoubleVector(x, y), p)!!
            val o = PointShapeSvg.create(shape, location, p, fattenMidPoint)
            root.add(wrap(o))
        }
        // tooltip
        /*
          Unlike the cases of CrossBarGeom and ErrorBarGeom, it is inconvenient to use RectanglesHelper here.
          RectanglesHelper uses a geometry factory that returns rectangles in data coordinates, but clientRectByDataPoint() returns client coordinates.
          Otherwise it is difficult to correctly calculate the width of the rectangle bounding the geometry, especially when the geometry is rotated.
        */
        aesthetics.dataPoints().forEach { p ->
            clientRectByDataPoint(geomHelper, fattenMidPoint)(p)?.let { clientRect ->
                val svgRect = SvgRectElement(clientRect)
                decorate(svgRect, p)
                tooltipHelper.addTarget(p, clientRect)
            }
        }
    }

    private fun clientRectByDataPoint(
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
            return GeomUtil.extendWidth(rect, width, geomHelper.ctx.flipped)
        }

        return ::factory
    }

    companion object {
        const val HANDLES_GROUPS = false
        const val DEF_FATTEN = 5.0
    }
}
