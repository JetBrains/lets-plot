/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.BarTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.CrossBarHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class CrossBarGeom : GeomBase() {
    var fattenMidline: Double = 2.5

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        CrossBarHelper.buildBoxes(
            root, aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = false)
        )
        CrossBarHelper.buildMidlines(root, aesthetics, ctx, geomHelper, fattenMidline)
        BarTooltipHelper.collectRectangleTargets(
            listOf(org.jetbrains.letsPlot.core.plot.base.Aes.YMAX, org.jetbrains.letsPlot.core.plot.base.Aes.YMIN),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = true),
            { HintColorUtil.colorWithAlpha(it) }
        )
    }

    companion object {
        const val HANDLES_GROUPS = false

        private val LEGEND_FACTORY = CrossBarHelper.legendFactory(false)

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            isHintRect: Boolean
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val rect = if (!isHintRect &&
                    p.defined(org.jetbrains.letsPlot.core.plot.base.Aes.X) &&
                    p.defined(org.jetbrains.letsPlot.core.plot.base.Aes.YMIN) &&
                    p.defined(org.jetbrains.letsPlot.core.plot.base.Aes.YMAX) &&
                    p.defined(org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH)
                ) {
                    val x = p.x()!!
                    val ymin = p.ymin()!!
                    val ymax = p.ymax()!!
                    val width = p.width()!! * ctx.getResolution(org.jetbrains.letsPlot.core.plot.base.Aes.X)

                    val origin = DoubleVector(x - width / 2, ymin)
                    val dimensions = DoubleVector(width, ymax - ymin)
                    DoubleRectangle(origin, dimensions)
                } else if (isHintRect &&
                    p.defined(org.jetbrains.letsPlot.core.plot.base.Aes.X) &&
                    p.defined(org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE) &&
                    p.defined(org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH)
                ) {
                    val x = p.x()!!
                    val middle = p.middle()!!
                    val width = p.width()!! * ctx.getResolution(org.jetbrains.letsPlot.core.plot.base.Aes.X)

                    val origin = DoubleVector(x - width / 2, middle)
                    val dimensions = DoubleVector(width, 0.0)
                    DoubleRectangle(origin, dimensions)
                } else {
                    null
                }

                rect?.let { geomHelper.toClient(it, p) }
            }
        }
    }
}
