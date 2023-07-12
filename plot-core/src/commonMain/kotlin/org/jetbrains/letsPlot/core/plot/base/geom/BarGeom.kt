/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.BarTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil.extendWidth
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectanglesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

open class BarGeom : GeomBase() {

    override fun rangeIncludesZero(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean = (aes == org.jetbrains.letsPlot.core.plot.base.Aes.Y)

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
                    val w = width!! * ctx.getResolution(org.jetbrains.letsPlot.core.plot.base.Aes.X)
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
