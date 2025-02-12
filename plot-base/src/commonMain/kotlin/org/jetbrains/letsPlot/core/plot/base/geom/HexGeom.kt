/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HexagonsHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.sqrt

class HexGeom : GeomBase(), WithWidth, WithHeight {
    var widthUnit: DimensionUnit = DEF_WIDTH_UNIT
    var heightUnit: DimensionUnit = DEF_HEIGHT_UNIT

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        val transformWidthToUnits: (Double) -> Double = { w -> geomHelper.transformDimensionValue(w, widthUnit, Aes.X) }
        val transformHeightToUnits: (Double) -> Double = { h -> geomHelper.transformDimensionValue(h, heightUnit, Aes.Y) }
        val helper = HexagonsHelper(aesthetics, pos, coord, ctx, clientHexByDataPoint(transformWidthToUnits, transformHeightToUnits))
        helper.setResamplingEnabled(!coord.isLinear)
        helper.createHexagons().forEach { hexLinePath ->
            root.add(hexLinePath.rootGroup)
        }
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val loc = p.finiteOrNull(coordAes) ?: return null
        val size = p.finiteOrNull(Aes.WIDTH) ?: return null
        val expand = size / 2.0
        return DoubleSpan(loc - expand, loc + expand)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val loc = p.finiteOrNull(coordAes) ?: return null
        val size = p.finiteOrNull(Aes.HEIGHT) ?: return null
        val expand = size * HALF_HEX_HEIGHT
        return DoubleSpan(loc - expand, loc + expand)
    }

    companion object {
        const val HANDLES_GROUPS = false

        val DEF_WIDTH_UNIT = DimensionUnit.IDENTITY
        val DEF_HEIGHT_UNIT = DimensionUnit.IDENTITY

        private val HALF_HEX_HEIGHT = 1.0 / sqrt(3.0)

        fun clientHexByDataPoint(
            transformWidthToUnits: (Double) -> Double,
            transformHeightToUnits: (Double) -> Double,
        ): (DataPointAesthetics) -> List<DoubleVector>? {
            fun factory(p: DataPointAesthetics): List<DoubleVector>? {
                val x = p.finiteOrNull(Aes.X) ?: return null
                val y = p.finiteOrNull(Aes.Y) ?: return null
                val w = p.finiteOrNull(Aes.WIDTH) ?: return null
                val h = p.finiteOrNull(Aes.HEIGHT) ?: return null

                val width = transformWidthToUnits(w)
                val height = transformHeightToUnits(h)

                val origin = DoubleVector(x, y)
                return listOf(
                    DoubleVector(origin.x, origin.y - height * HALF_HEX_HEIGHT),
                    DoubleVector(origin.x + width / 2, origin.y - height * HALF_HEX_HEIGHT / 2.0),
                    DoubleVector(origin.x + width / 2, origin.y + height * HALF_HEX_HEIGHT / 2.0),
                    DoubleVector(origin.x, origin.y + height / sqrt(3.0)),
                    DoubleVector(origin.x - width / 2, origin.y + height * HALF_HEX_HEIGHT / 2.0),
                    DoubleVector(origin.x - width / 2, origin.y - height * HALF_HEX_HEIGHT / 2.0),
                    DoubleVector(origin.x, origin.y - height * HALF_HEX_HEIGHT), // Close the hexagon
                )
            }

            return ::factory
        }
    }
}