/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.BarAnnotation
import org.jetbrains.letsPlot.core.plot.base.geom.util.*
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
        val dataPoints = aesthetics.dataPoints()
        val linesHelper = LinesHelper(pos, coord, ctx)
        linesHelper.setResamplingEnabled(!coord.isLinear)

        val targetCollectorHelper = TargetCollectorHelper(GeomKind.BAR, ctx)
        val polygons = linesHelper.createRectPolygon(dataPoints, polygonByDataPoint(ctx))

        val rectangles = mutableListOf<SvgNode>()
        polygons.forEach { (svg, polygonData) ->
            targetCollectorHelper.addPolygons(polygonData)

            rectangles.add(svg)
        }

        rectangles.reverse() // TODO: why reverse?
        rectangles.forEach(root::add)

        ctx.annotation?.let {
            BarAnnotation.build(
                root,
                polygons.map { (_, polygonData) -> polygonData },
                ::rectByDataPoint,
                linesHelper,
                coord,
                ctx
            )
        }
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun polygonByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> List<DoubleVector>? {
            fun factory(p: DataPointAesthetics): List<DoubleVector>? {
                return rectByDataPoint(p, ctx)?.points
            }

            return ::factory
        }

        private fun rectByDataPoint(p: DataPointAesthetics, ctx: GeomContext): DoubleRectangle? {
            val (x, y, width) = p.finiteOrNull(Aes.X, Aes.Y, Aes.WIDTH) ?: return null

            val w = width * ctx.getResolution(Aes.X)

            val origin: DoubleVector
            val dimension: DoubleVector

            if (y >= 0) {
                origin = DoubleVector(x - w / 2, 0.0)
                dimension = DoubleVector(w, y)
            } else {
                origin = DoubleVector(x - w / 2, y)
                dimension = DoubleVector(w, -y)
            }

            return DoubleRectangle(origin, dimension)
        }
    }
}
