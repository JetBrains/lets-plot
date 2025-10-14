/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext

class HistogramGeom : BarGeom(), WithWidth {
    private var breaks: List<Double> = emptyList()

    fun setBreaks(breaks: List<Double>) {
        this.breaks = breaks.filter { SeriesUtil.isFinite(it) }.sorted().takeIf { it.size >= 2 } ?: emptyList()
    }

    override fun getWidthCalculator(aesthetics: Aesthetics, ctx: GeomContext): (DataPointAesthetics) -> Double? {
        val useBinWidth = aesthetics.dataPoints()
            .map(DataPointAesthetics::binwidth)
            .firstOrNull { SeriesUtil.isFinite(it) && it!! > 0.0 && it != 1.0 } != null // There is finite nontrivial binwidth, otherwise we can use resolution
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
        return if (breaks.isNotEmpty()) {
            DoubleSpan(breaks.first(), breaks.last())
        } else {
            DimensionsUtil.dimensionSpan(p, coordAes, Aes.WIDTH, resolution, DimensionUnit.RESOLUTION)
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
