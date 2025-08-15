/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.BarAnnotation
import org.jetbrains.letsPlot.core.plot.base.geom.util.LinesHelper
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
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx, visualRectByDataPoint(ctx))
        val tooltipHelper = RectangleTooltipHelper(pos, coord, ctx)
        val rectangles = mutableListOf<SvgNode>()
        if (coord.isLinear) {
            helper.createRectangles { _, svgNode, _ -> rectangles.add(svgNode) }

            // Snap tooltips to the proper side (e.g. bottom for negative values, right for coord_flip)
            val hintHelper = RectanglesHelper(aesthetics, pos, coord, ctx, hintRectByDataPoint(ctx))
            hintHelper.createRectangles { aes, _, rect -> tooltipHelper.addTarget(aes, rect) }
        } else {
            helper.createNonLinearRectangles { aes, svgNode, polygon ->
                rectangles.add(svgNode)
                tooltipHelper.addTarget(aes, polygon)
            }
        }
        rectangles.reverse() // TODO: why reverse?
        rectangles.forEach(root::add)

        ctx.annotation?.let {
            val dataPoints = aesthetics.dataPoints()
            val linesHelper = LinesHelper(pos, coord, ctx)
            linesHelper.setResamplingEnabled(!coord.isLinear)
            val polygons = linesHelper.createRectPolygon(dataPoints, polygonByDataPoint(ctx))

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

        // May return rect with negative height to make the tooltip snap to the bottom side.
        private fun hintRectByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                val (x, y, width ) = p.finiteOrNull(Aes.X, Aes.Y, Aes.WIDTH) ?: return null

                val w = width * ctx.getResolution(Aes.X)
                val origin = DoubleVector(x - w / 2, y)
                val dimension = DoubleVector(w, 0.0)
                return DoubleRectangle(origin, dimension)
            }

            return ::factory
        }

        private fun visualRectByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                return rectByDataPoint(p, ctx)
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
