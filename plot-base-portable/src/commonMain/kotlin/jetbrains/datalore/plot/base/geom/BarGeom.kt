/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.BarTooltipHelper
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil.extendWidth
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
        val geomHelper = GeomHelper(pos, coord, ctx)
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx)
        val rectangles = helper.createRectangles(
            clientRectByDataPoint(ctx, geomHelper, isHintRect = false)
        )
        rectangles.reverse()
        rectangles.forEach { root.add(it) }

        BarTooltipHelper.collectRectangleTargets(
            emptyList(),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = true),
            HintColorUtil::fillWithAlpha
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            isHintRect: Boolean
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val x = p.x()
                val y = p.y()
                val width = p.width()
                if (SeriesUtil.allFinite(x, y, width)) {
                    x!!; y!!
                    val w = width!! * ctx.getResolution(Aes.X)
                    val rect = if (isHintRect) {
                        val origin = DoubleVector(x - w / 2, y)
                        val dimension = DoubleVector(w, 0.0)
                        DoubleRectangle(origin, dimension)
                    } else {
                        val origin: DoubleVector
                        val dimensions: DoubleVector
                        if (y >= 0) {
                            origin = DoubleVector(x - w / 2, 0.0)
                            dimensions = DoubleVector(w, y)
                        } else {
                            origin = DoubleVector(x - w / 2, y)
                            dimensions = DoubleVector(w, -y)
                        }
                        DoubleRectangle(origin, dimensions)
                    }
                    geomHelper.toClient(rect, p)?.let { clientRect ->
                        if (clientRect.width < 2.0) extendWidth(clientRect, 2.0, ctx.flipped) else clientRect
                    }
                } else {
                    null
                }
            }
        }
    }
}
