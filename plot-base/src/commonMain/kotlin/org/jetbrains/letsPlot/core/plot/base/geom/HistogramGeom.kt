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
    override fun getWidthCalculator(aesthetics: Aesthetics, ctx: GeomContext): (DataPointAesthetics) -> Double? {
        fun widthCalculator(p: DataPointAesthetics): Double? {
            val width = p.finiteOrNull(Aes.WIDTH) ?: return null
            val binWidth = p.finiteOrNull(Aes.BINWIDTH) ?: return null
            return binWidth * width
        }

        return ::widthCalculator
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val width = p.finiteOrNull(Aes.WIDTH) ?: return null
        return DimensionsUtil.dimensionSpan(p, coordAes, Aes.BINWIDTH, width, DimensionUnit.RESOLUTION)
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
