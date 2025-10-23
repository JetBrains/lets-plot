/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
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
        val binSpan = getBinSpanCalculator(ctx)
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx, visualRectByDataPoint(binSpan))
        val tooltipHelper = RectangleTooltipHelper(pos, coord, ctx)
        val rectangles = mutableListOf<SvgNode>()
        if (coord.isLinear) {
            helper.createRectangles { _, svgNode, _ -> rectangles.add(svgNode) }

            // Snap tooltips to the proper side (e.g. bottom for negative values, right for coord_flip)
            val hintHelper = RectanglesHelper(aesthetics, pos, coord, ctx, hintRectByDataPoint(binSpan))
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
            val polygons = linesHelper.createRectPolygon(dataPoints, polygonByDataPoint(binSpan))

            BarAnnotation.build(
                root,
                polygons.map { (_, polygonData) -> polygonData },
                { p -> rectByDataPoint(p, binSpan) },
                linesHelper,
                coord,
                ctx
            )
        }
    }

    protected open fun getBinSpanCalculator(ctx: GeomContext): (DataPointAesthetics) -> DoubleSpan? {
        val resolution = ctx.getResolution(Aes.X)

        fun binSpan(p: DataPointAesthetics): DoubleSpan? {
            val (x, width) = p.finiteOrNull(Aes.X, Aes.WIDTH) ?: return null
            return DoubleSpan(x - resolution * width / 2.0, x + resolution * width / 2.0)
        }

        return ::binSpan
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun polygonByDataPoint(binSpan: (DataPointAesthetics) -> DoubleSpan?): (DataPointAesthetics) -> List<DoubleVector>? {
            fun factory(p: DataPointAesthetics): List<DoubleVector>? {
                return rectByDataPoint(p, binSpan)?.points
            }

            return ::factory
        }

        // May return rect with negative height to make the tooltip snap to the bottom side.
        private fun hintRectByDataPoint(binSpan: (DataPointAesthetics) -> DoubleSpan?): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                val y = p.finiteOrNull(Aes.Y) ?: return null

                val span = binSpan(p) ?: return null
                val origin = DoubleVector(span.lowerEnd, y)
                val dimension = DoubleVector(span.length, 0.0)
                return DoubleRectangle(origin, dimension)
            }

            return ::factory
        }

        private fun visualRectByDataPoint(binSpan: (DataPointAesthetics) -> DoubleSpan?): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                return rectByDataPoint(p, binSpan)
            }

            return ::factory
        }

        private fun rectByDataPoint(p: DataPointAesthetics, binSpan: (DataPointAesthetics) -> DoubleSpan?): DoubleRectangle? {
            val y = p.finiteOrNull(Aes.Y) ?: return null

            val span = binSpan(p) ?: return null

            val origin: DoubleVector
            val dimension: DoubleVector

            if (y >= 0) {
                origin = DoubleVector(span.lowerEnd, 0.0)
                dimension = DoubleVector(span.length, y)
            } else {
                origin = DoubleVector(span.lowerEnd, y)
                dimension = DoubleVector(span.length, -y)
            }

            return DoubleRectangle(origin, dimension)
        }
    }
}
