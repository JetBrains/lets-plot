/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.HexagonTooltipHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.HexagonsHelper
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.sqrt

class HexGeom : GeomBase() {
    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val tooltipHelper = HexagonTooltipHelper(ctx)
        val helper = HexagonsHelper(aesthetics, pos, coord, ctx, clientHexByDataPoint(ctx))
        val svgHexHelper = helper.createSvgHexHelper()
        svgHexHelper.setResamplingEnabled(!coord.isLinear)
        svgHexHelper.onGeometry { p, hex ->
            if (hex != null) {
                tooltipHelper.addTarget(p, hex)
            }
        }

        val slimGroup = svgHexHelper.createSlimHexagons()
        root.add(wrap(slimGroup))
    }

    companion object {
        const val HANDLES_GROUPS = false

        private fun clientHexByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> List<DoubleVector>? {
            fun factory(p: DataPointAesthetics): List<DoubleVector>? {
                val x = p.finiteOrNull(Aes.X) ?: return null
                val y = p.finiteOrNull(Aes.Y) ?: return null
                val w = p.finiteOrNull(Aes.WIDTH) ?: return null
                val h = p.finiteOrNull(Aes.HEIGHT) ?: return null

                val width = w * 2 * ctx.getResolution(Aes.X) // Without the coefficient 2 * resolution, the hexagon will not be stretched to fill all available area
                val height = h * 2.0 / sqrt(3.0) * ctx.getResolution(Aes.Y) // The same as above

                val origin = DoubleVector(x, y)
                return listOf(
                    DoubleVector(origin.x, origin.y - height / sqrt(3.0)),
                    DoubleVector(origin.x + width / 2, origin.y - height / (2.0 * sqrt(3.0))),
                    DoubleVector(origin.x + width / 2, origin.y + height / (2.0 * sqrt(3.0))),
                    DoubleVector(origin.x, origin.y + height / sqrt(3.0)),
                    DoubleVector(origin.x - width / 2, origin.y + height / (2.0 * sqrt(3.0))),
                    DoubleVector(origin.x - width / 2, origin.y - height / (2.0 * sqrt(3.0))),
                )
            }

            return ::factory
        }
    }
}