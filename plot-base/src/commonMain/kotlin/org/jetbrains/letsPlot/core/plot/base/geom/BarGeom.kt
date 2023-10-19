/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectangleTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectanglesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

open class BarGeom : GeomBase() {

    override fun rangeIncludesZero(aes: Aes<*>): Boolean = (aes == Aes.Y)

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx, clientRectByDataPoint(ctx))
        val tooltipHelper = RectangleTooltipHelper(pos, coord, ctx)
        val rectangles = mutableListOf<SvgNode>()
        if (coord.isLinear) {
            helper.createRectangles { aes, svgNode, rect ->
                rectangles.add(svgNode)
                tooltipHelper.addTarget(aes, rect)
            }
        } else {
            helper.createNonLinearRectangles { aes, svgNode, polygon ->
                rectangles.add(svgNode)
                tooltipHelper.addTarget(aes, polygon)
            }
        }
        rectangles.reverse() // TODO: why reverse?
        rectangles.forEach(root::add)
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun clientRectByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                val x = finiteOrNull(p.x()) ?: return null
                val y = finiteOrNull(p.y()) ?: return null
                val w = finiteOrNull(p.width()) ?: return null

                val width = w * ctx.getResolution(Aes.X)
                val origin = DoubleVector(x - width / 2, 0.0)
                val dimension = DoubleVector(width, y)
                return DoubleRectangle(origin, dimension)
            }

            return ::factory
        }
    }
}
