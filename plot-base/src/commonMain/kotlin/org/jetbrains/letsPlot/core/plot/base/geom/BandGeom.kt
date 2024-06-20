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
            fun stripRectByDataPoint(p: DataPointAesthetics): DoubleRectangle? {
                val xmin = p.xmin() ?: return null
                val xmax = p.xmax() ?: return null
                val ymin = viewPort.bottom
                val ymax = viewPort.top
                return DoubleRectangle.LTRB(xmin, ymin, xmax, ymax)
            }
            return ::stripRectByDataPoint
        }
    }
}