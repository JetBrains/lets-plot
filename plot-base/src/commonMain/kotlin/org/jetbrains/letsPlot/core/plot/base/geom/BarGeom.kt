/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.finiteOrNull
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.annotation.BarAnnotation
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

        ctx.annotation?.let { BarAnnotation.build(root, helper, coord, ctx) }
    }

    companion object {
        const val HANDLES_GROUPS = false
        private fun xyw(p: DataPointAesthetics, ctx: GeomContext): Triple<Double, Double, Double>? {
            val x = finiteOrNull(p.x()) ?: return null
            val y = finiteOrNull(p.y()) ?: return null
            val w = finiteOrNull(p.width()) ?: return null

            return Triple(x, y, w * ctx.getResolution(Aes.X))
        }

        private fun hintRectByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                val (x, y, w) = xyw(p, ctx) ?: return null
                val origin = DoubleVector(x - w / 2, y)
                val dimension = DoubleVector(w, 0.0)
                return DoubleRectangle(origin, dimension)
            }

            return ::factory
        }

        private fun visualRectByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                val (x, y, w) = xyw(p, ctx) ?: return null

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

            return ::factory
        }
    }
}
