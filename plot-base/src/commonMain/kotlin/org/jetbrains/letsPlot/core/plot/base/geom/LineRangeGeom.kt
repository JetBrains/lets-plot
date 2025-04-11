/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper.Companion.decorate
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.toLocation
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectangleTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement
import kotlin.math.max

class LineRangeGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = VLineGeom.LEGEND_KEY_ELEMENT_FACTORY

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
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.LINE_RANGE, ctx)
        val tooltipHelper = RectangleTooltipHelper(
            pos = pos,
            coord = coord,
            ctx = ctx,
            hintAesList = listOf(Aes.YMIN, Aes.YMAX),
            fillColorMapper = { HintColorUtil.colorWithAlpha(it) },
            colorMarkerMapper = colorsByDataPoint
        )
        for (p in aesthetics.dataPoints()) {
            val start = p.toLocation(Aes.X, Aes.YMIN) ?: continue
            val end = p.toLocation(Aes.X, Aes.YMAX) ?: continue

            helper.createLine(start, end, p)?.let { (svgElement, _) -> root.add(svgElement) }
        }
        // tooltip
        /*
          Unlike the cases of CrossBarGeom and ErrorBarGeom, it is inconvenient to use RectanglesHelper here.
          RectanglesHelper uses a geometry factory that returns rectangles in data coordinates, but clientRectByDataPoint() returns client coordinates.
          Otherwise it is difficult to correctly calculate the width of the rectangle bounding the geometry, especially when the geometry is rotated.
        */
        aesthetics.dataPoints().forEach { p ->
            clientRectByDataPoint(geomHelper)(p)?.let { clientRect ->
                val svgRect = SvgRectElement(clientRect)
                decorate(svgRect, p)
                tooltipHelper.addTarget(p, clientRect)
            }
        }
    }

    private fun clientRectByDataPoint(
        geomHelper: GeomHelper
    ): (DataPointAesthetics) -> DoubleRectangle? {
        fun factory(p: DataPointAesthetics): DoubleRectangle? {
            val x = p.finiteOrNull(Aes.X) ?: return null
            val ymin = p.finiteOrNull(Aes.YMIN) ?: return null
            val ymax = p.finiteOrNull(Aes.YMAX) ?: return null

            val height = ymax - ymin

            val rect = DoubleRectangle(DoubleVector(x, ymax - height / 2.0), DoubleVector.ZERO)
                .let { geomHelper.toClient(it, p) }!!
            val width = max(AesScaling.strokeWidth(p), MIN_TOOLTIP_RECTANGLE_WIDTH)

            return GeomUtil.extendWidth(rect, width, geomHelper.ctx.flipped)
        }

        return ::factory
    }

    companion object {
        const val HANDLES_GROUPS = false
        const val MIN_TOOLTIP_RECTANGLE_WIDTH = 2.0
    }
}
