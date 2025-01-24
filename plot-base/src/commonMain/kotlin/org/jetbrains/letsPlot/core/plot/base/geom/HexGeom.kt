/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.HexagonTooltipHelper
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
        val tooltipHelper = HexagonTooltipHelper(ctx)
        val helper = HexagonsHelper(aesthetics, pos, coord, ctx, clientHexByDataPoint())
        val svgHexHelper = helper.createSvgHexHelper()
        svgHexHelper.setResamplingEnabled(!coord.isLinear)
        svgHexHelper.onGeometry { p, hex ->
            if (hex != null) {
                tooltipHelper.addTarget(p, hex)
            }
        }
        svgHexHelper.createHexagons().forEach { hexLinePath ->
            root.add(hexLinePath.rootGroup)
        }
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun clientHexByDataPoint(): (DataPointAesthetics) -> List<DoubleVector>? {
            fun factory(p: DataPointAesthetics): List<DoubleVector>? {
                val x = p.finiteOrNull(Aes.X) ?: return null
                val y = p.finiteOrNull(Aes.Y) ?: return null
                val width = p.finiteOrNull(Aes.WIDTH) ?: return null
                val height = p.finiteOrNull(Aes.HEIGHT) ?: return null

                val origin = DoubleVector(x, y)
                return listOf(
                    DoubleVector(origin.x, origin.y - height / sqrt(3.0)),
                    DoubleVector(origin.x + width / 2, origin.y - height / (2.0 * sqrt(3.0))),
                    DoubleVector(origin.x + width / 2, origin.y + height / (2.0 * sqrt(3.0))),
                    DoubleVector(origin.x, origin.y + height / sqrt(3.0)),
                    DoubleVector(origin.x - width / 2, origin.y + height / (2.0 * sqrt(3.0))),
                    DoubleVector(origin.x - width / 2, origin.y - height / (2.0 * sqrt(3.0))),
                    DoubleVector(origin.x, origin.y - height / sqrt(3.0)), // Close the hexagon
                )
            }

            return ::factory
        }
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return sizeSpan(p, coordAes, Aes.WIDTH)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return sizeSpan(p, coordAes, Aes.HEIGHT)
    }

    private fun sizeSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        sizeAes: Aes<Double>,
    ): DoubleSpan? {
        val loc = p[coordAes]
        val size = p[sizeAes]
        return if (SeriesUtil.allFinite(loc, size)) {
            loc!!
            val expand = if (sizeAes == Aes.WIDTH) {
                size!! / 2.0
            } else {
                size!! / sqrt(3.0)
            }
            DoubleSpan(
                loc - expand,
                loc + expand
            )
        } else {
            null
        }
    }
}