/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot

class BandGeom : GeomBase() {
    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = LinesHelper(pos, coord, ctx)
        val viewPort = overallAesBounds(ctx)
        val paths = helper.createStrips(aesthetics.dataPoints(), toStrip(viewPort))
        root.appendNodes(paths)
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun toStrip(viewPort: DoubleRectangle): (DataPointAesthetics) -> DoubleRectangle? {
            val xRange = viewPort.xRange()
            val yRange = viewPort.yRange()
            fun stripRectByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
                val xmin = p.finiteOrNull(Aes.XMIN)
                val xmax = p.finiteOrNull(Aes.XMAX)
                val ymin = p.finiteOrNull(Aes.YMIN)
                val ymax = p.finiteOrNull(Aes.YMAX)
                return when {
                    xmin != null && xmax != null && xmin <= xmax && (xmin in xRange || xmax in xRange) ->
                        DoubleRectangle.LTRB(xmin, viewPort.top, xmax, viewPort.bottom)
                    ymin != null && ymax != null && ymin <= ymax && (ymin in yRange || ymax in yRange) ->
                        DoubleRectangle.LTRB(viewPort.left, ymax, viewPort.right, ymin)
                    else -> null
                }
            }
            return ::stripRectByDataPoint
        }
    }
}