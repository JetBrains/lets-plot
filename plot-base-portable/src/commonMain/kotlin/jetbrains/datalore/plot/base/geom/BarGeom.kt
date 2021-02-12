/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.BarTooltipHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.geom.util.RectanglesHelper
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil

open class BarGeom : GeomBase() {

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper =
            RectanglesHelper(aesthetics, pos, coord, ctx)
        val rectangles = helper.createRectangles(
            rectangleByDataPoint(
                ctx,
                coord
            )
        )
        rectangles.reverse()
        rectangles.forEach { root.add(it) }

//        RectTargetCollectorHelper(
//            helper,
//            rectangleByDataPoint(ctx),
//            { HintColorUtil.fromFill(it) })
//            .collectTo(ctx.targetCollector)
        BarTooltipHelper.collectRectangleTargets(
            emptyList(),
            aesthetics, pos, coord, ctx,
            rectangleByDataPoint(ctx, coord),
            { HintColorUtil.fromFill(it) }
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun rectangleByDataPoint(
            ctx: GeomContext,
            coord: CoordinateSystem
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                var result: DoubleRectangle? = null
                val x = p.x()
                val y = p.y()
                val w = p.width()
                if (SeriesUtil.allFinite(x, y, w)) {
                    val rect = GeomUtil.rectangleByDataPoint(p, ctx)
                    if (coord.contains(rect)) {
                        result = rect
                    }
                }
                result
            }
        }
    }
}
