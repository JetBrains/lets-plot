/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectangleTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectanglesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class RectGeom : GeomBase() {

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx, ::clientRectByDataPoint)
        val tooltipHelper = RectangleTooltipHelper(pos, coord, ctx)
        if (coord.isLinear) {
            helper.createRectangles() { aes, svgNode, rect ->
                root.add(svgNode)
                tooltipHelper.addTarget(aes, rect)
            }
        } else {
            helper.createNonLinearRectangles() { aes, svgNode, polygon ->
                root.add(svgNode)
                tooltipHelper.addTarget(aes, polygon)
            }
        }
    }

    companion object {

        //rectangle groups are used in geom_livemap
        const val HANDLES_GROUPS = true

        private fun clientRectByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
            val xmin = finiteOrNull(p.xmin()) ?: return null
            val xmax = finiteOrNull(p.xmax()) ?: return null
            val ymin = finiteOrNull(p.ymin()) ?: return null
            val ymax = finiteOrNull(p.ymax()) ?: return null

            return DoubleRectangle.LTRB(xmin, ymin, xmax, ymax)
        }
    }
}
