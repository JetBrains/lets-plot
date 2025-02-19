/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.DimensionUnit.IDENTITY
import org.jetbrains.letsPlot.core.plot.base.geom.DimensionUnit.PIXEL
import org.jetbrains.letsPlot.core.plot.base.geom.DimensionUnit.RESOLUTION
import org.jetbrains.letsPlot.core.plot.base.geom.DimensionUnit.SIZE
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
        val transformWidthToUnits: (Double) -> Double = { w -> transformDimensionValue(w, widthUnit, Aes.X, coord, ctx) }
        val transformHeightToUnits: (Double) -> Double = { h -> transformDimensionValue(h, heightUnit, Aes.Y, coord, ctx) }
        val helper = HexagonsHelper(aesthetics, pos, coord, ctx, clientHexByDataPoint(transformWidthToUnits, transformHeightToUnits))
        helper.setResamplingEnabled(!coord.isLinear)
        helper.createHexagons().forEach { hexLinePath ->
            root.add(hexLinePath.rootGroup)
        }
    }

    // Almost the same as in DimensionsUtil.dimensionSpan, but with some differences specific to hex geom
    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val loc = p.finiteOrNull(coordAes) ?: return null
        val width = p.finiteOrNull(Aes.WIDTH) ?: return null
        val expand = when (widthUnit) {
            RESOLUTION -> width * resolution
            IDENTITY -> width / 2.0
            else -> 0.0 // If the units are "absolute" (e.g. pixels), we don't use expand
        }
        return DoubleSpan(
            loc - expand,
            loc + expand
        )
    }

    // Almost the same as in DimensionsUtil.dimensionSpan, but with some differences specific to hex geom
    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val loc = p.finiteOrNull(coordAes) ?: return null
        val height = p.finiteOrNull(Aes.HEIGHT) ?: return null
        val expand = when (heightUnit) {
            RESOLUTION -> height * resolution * HALF_HEX_HEIGHT
            IDENTITY -> height * HALF_HEX_HEIGHT
            else -> 0.0 // If the units are "absolute" (e.g. pixels), we don't use expand
        }
        return DoubleSpan(
            loc - expand,
            loc + expand
        )
    }

    // Almost the same as in GeomHelper::transformDimensionValue, but with some differences specific to hex geom
    private fun transformDimensionValue(
        value: Double,
        unit: DimensionUnit,
        axisAes: Aes<Double>,
        coord: CoordinateSystem,
        ctx: GeomContext
    ): Double {
        val unitSize = when (axisAes) {
            Aes.X -> coord.unitSize(DoubleVector(1.0, 0.0)).x
            Aes.Y -> coord.unitSize(DoubleVector(0.0, 1.0)).y
            else -> error("Unsupported axis aes: $axisAes")
        }
        return when (unit) {
            RESOLUTION -> {
                val resolution = when (axisAes) {
                    Aes.X -> ctx.getResolution(Aes.X)
                    Aes.Y -> HALF_HEX_HEIGHT * ctx.getResolution(Aes.Y)
                    else -> error("Unsupported axis aes: $axisAes")
                }
                2.0 * resolution * value
            }
            IDENTITY -> value
            SIZE -> value * AesScaling.POINT_UNIT_SIZE / unitSize
            PIXEL -> value / unitSize
        }
    }

    companion object {
        const val HANDLES_GROUPS = false

        val DEF_WIDTH_UNIT = RESOLUTION
        val DEF_HEIGHT_UNIT = RESOLUTION

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