/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleSegment
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.CoordinateSystem
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.PositionAdjustment
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

class ABLineGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
            .createSvgElementHelper()
        helper.setStrokeAlphaEnabled(true)

        val viewPort = overallAesBounds(ctx)
        val boundaries = viewPort.parts.toList()

        val lines = ArrayList<SvgNode>()
        for (p in aesthetics.dataPoints()) {
            val intercept = p.intercept()
            val slope = p.slope()
            if (SeriesUtil.allFinite(intercept, slope)) {
                val p1 = DoubleVector(viewPort.left, intercept!! + viewPort.left * slope!!)
                val p2 = DoubleVector(viewPort.right, p1.y + viewPort.dimension.x * slope)
                val s = DoubleSegment(p1, p2)

                val lineEnds = HashSet<DoubleVector>(2)
                for (boundary in boundaries) {
                    val intersection = boundary.intersection(s)
                    if (intersection != null) {
                        lineEnds.add(intersection)
                        if (lineEnds.size == 2) {
                            break
                        }
                    }
                }

                if (lineEnds.size == 2) {
                    val it = lineEnds.iterator()
                    val (svg) = helper.createLine(it.next(), it.next(), p) ?: continue
                    lines.add(svg)
                }
            }
        }

        lines.forEach { root.add(it) }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
