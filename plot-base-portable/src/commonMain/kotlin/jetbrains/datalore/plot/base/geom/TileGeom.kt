/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.geom.util.RectTargetCollectorHelper
import jetbrains.datalore.plot.base.geom.util.RectanglesHelper
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil

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
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx)
        val slimGroup = helper.createSlimRectangles(
            clientRectByDataPoint(ctx, geomHelper)
        )
        root.add(wrap(slimGroup))

        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TILE, ctx)
        RectTargetCollectorHelper(
            helper,
            clientRectByDataPoint(ctx, geomHelper),
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
