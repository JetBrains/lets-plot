/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext

class HistogramGeom : BarGeom(), WithWidth {
    var useBinWidth: Boolean = false

    override fun getWidthCalculator(aesthetics: Aesthetics, ctx: GeomContext): (DataPointAesthetics) -> Double? {
        val resolution = ctx.getResolution(Aes.X)

        fun widthCalculator(p: DataPointAesthetics): Double? {
            val width = p.finiteOrNull(Aes.WIDTH) ?: return null
            val scale = if (useBinWidth) {
                p.finiteOrNull(Aes.BINWIDTH) ?: return null
            } else {
                resolution
            }
            return scale * width
        }

        return ::widthCalculator
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return if (useBinWidth) {
            DimensionsUtil.dimensionSpan(p, coordAes, Aes.BINWIDTH, resolution, DimensionUnit.IDENTITY)
        } else {
            DimensionsUtil.dimensionSpan(p, coordAes, Aes.WIDTH, resolution, DimensionUnit.RESOLUTION)
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
