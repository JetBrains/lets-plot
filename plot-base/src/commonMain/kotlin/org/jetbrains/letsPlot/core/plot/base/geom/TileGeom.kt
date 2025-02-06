/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectangleTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.RectanglesHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint.Kind.CURSOR_TOOLTIP

/**
 * geom_tile uses the center of the tile and its size (x, y, width, height).
 */
open class TileGeom : GeomBase(), WithWidth, WithHeight {
    var widthUnit: DimensionUnit = DEF_WIDTH_UNIT
    var heightUnit: DimensionUnit = DEF_HEIGHT_UNIT

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val tooltipHelper = RectangleTooltipHelper(pos, coord, ctx, tooltipKind = CURSOR_TOOLTIP)
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx, clientRectByDataPoint(ctx, widthUnit, heightUnit))
        val svgRectHelper = helper.createSvgRectHelper()
        svgRectHelper.setResamplingEnabled(!coord.isLinear)
        svgRectHelper.onGeometry { p, rect, polygon ->
            if (polygon != null) {
                tooltipHelper.addTarget(p, polygon)
            } else if (rect != null) {
                tooltipHelper.addTarget(p, rect)
            }
        }

        val slimGroup = svgRectHelper.createSlimRectangles()
        root.add(wrap(slimGroup))
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean,
        unit: DimensionUnit
    ): DoubleSpan? {
        val loc = p.finiteOrNull(coordAes) ?: return null
        val size = p.finiteOrNull(Aes.WIDTH) ?: return null
        val expand = when (unit) {
            DimensionUnit.GEOM -> size * resolution / 2.0
            DimensionUnit.AXIS -> size / 2.0
        }
        return DoubleSpan(loc - expand, loc + expand)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean,
        unit: DimensionUnit
    ): DoubleSpan? {
        val loc = p.finiteOrNull(coordAes) ?: return null
        val size = p.finiteOrNull(Aes.HEIGHT) ?: return null
        val expand = when (unit) {
            DimensionUnit.GEOM -> size * resolution / 2.0
            DimensionUnit.AXIS -> size / 2.0
        }
        return DoubleSpan(loc - expand, loc + expand)
    }

    companion object {
        const val HANDLES_GROUPS = false

        val DEF_WIDTH_UNIT: DimensionUnit = DimensionUnit.GEOM
        val DEF_HEIGHT_UNIT: DimensionUnit = DimensionUnit.GEOM

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            widthUnit: DimensionUnit,
            heightUnit: DimensionUnit
        ): (DataPointAesthetics) -> DoubleRectangle? {
            fun factory(p: DataPointAesthetics): DoubleRectangle? {
                val x = p.finiteOrNull(Aes.X) ?: return null
                val y = p.finiteOrNull(Aes.Y) ?: return null
                val w = p.finiteOrNull(Aes.WIDTH) ?: return null
                val h = p.finiteOrNull(Aes.HEIGHT) ?: return null

                val width = when (widthUnit) {
                    DimensionUnit.GEOM -> w * ctx.getResolution(Aes.X)
                    DimensionUnit.AXIS -> w
                }
                val height = when (heightUnit) {
                    DimensionUnit.GEOM -> h * ctx.getResolution(Aes.Y)
                    DimensionUnit.AXIS -> h
                }

                val origin = DoubleVector(x - width / 2, y - height / 2)
                val dimensions = DoubleVector(width, height)
                return DoubleRectangle(origin, dimensions)
            }

            return ::factory
        }
    }
}
