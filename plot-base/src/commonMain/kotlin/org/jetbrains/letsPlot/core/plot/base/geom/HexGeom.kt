/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.HexagonsHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.sqrt

class HexGeom : GeomBase(), WithWidth, WithHeight {
    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = HexagonsHelper(aesthetics, pos, coord, ctx, clientHexByDataPoint())
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

        private val HALF_HEX_HEIGHT = 1.0 / sqrt(3.0)

        fun clientHexByDataPoint(): (DataPointAesthetics) -> List<DoubleVector>? {
            fun factory(p: DataPointAesthetics): List<DoubleVector>? {
                val x = p.finiteOrNull(Aes.X) ?: return null
                val y = p.finiteOrNull(Aes.Y) ?: return null
                val width = p.finiteOrNull(Aes.WIDTH) ?: return null
                val height = p.finiteOrNull(Aes.HEIGHT) ?: return null

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