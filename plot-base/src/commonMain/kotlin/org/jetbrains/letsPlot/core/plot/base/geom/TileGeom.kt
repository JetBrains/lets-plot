/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectTargetCollectorHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectanglesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint

/**
 * geom_tile uses the center of the tile and its size (x, y, width, height).
 */
open class TileGeom : GeomBase() {

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx, clientRectByDataPoint(ctx, geomHelper))
        val slimGroup = helper.createSlimRectangles()
        root.add(wrap(slimGroup))

        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TILE, ctx)
        RectTargetCollectorHelper(
            helper,
            TipLayoutHint.Kind.CURSOR_TOOLTIP,
            colorsByDataPoint
        )
            .collectTo(ctx.targetCollector)
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun clientRectByDataPoint(ctx: GeomContext, geomHelper: GeomHelper): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val x = p.x()
                val y = p.y()
                val w = p.width()
                val h = p.height()

                if (SeriesUtil.allFinite(x, y, w, h)) {
                    val width = w!! * ctx.getResolution(Aes.X)
                    val height = h!! * ctx.getResolution(Aes.Y)

                    val origin = DoubleVector(x!! - width / 2, y!! - height / 2)
                    val dimensions = DoubleVector(width, height)
                    geomHelper.toClient(DoubleRectangle(origin, dimensions), p)
                } else {
                    null
                }
            }
        }
    }
}
