/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgLineElement

class ABLineGeom : GeomBase() {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = HLineGeom.LEGEND_KEY_ELEMENT_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coordinateSystem: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coordinateSystem, ctx)
            .createSvgElementHelper()

        val viewPort = aesViewPort(aesthetics)
        val boundaries = Iterables.toList(viewPort.parts)

        val lines = ArrayList<SvgLineElement>()
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
                    val line = helper.createLine(it.next(), it.next(), p)
                    lines.add(line)
                }
            }
        }

        lines.forEach { root.add(it) }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
