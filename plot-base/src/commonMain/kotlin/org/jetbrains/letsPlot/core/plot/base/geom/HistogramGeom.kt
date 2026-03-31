/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.intern.bracketingIndicesOrNull
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext

class HistogramGeom : BarGeom(), WithWidth {
    private var breaks: List<Double> = emptyList()

    fun setBreaks(breaks: List<Double>) {
        this.breaks = breaks.filter(Double::isFinite).distinct().sorted()
    }

    override fun getBinSpanCalculator(ctx: GeomContext): (DataPointAesthetics) -> DoubleSpan? {
        val resolution = ctx.getResolution(Aes.X)
        return { p -> binSpan(p, Aes.X, breaks, resolution) }
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return binSpan(p, coordAes, breaks, resolution)
    }

    companion object {
        const val HANDLES_GROUPS = false

        fun binSpan(p: DataPointAesthetics, coordAes: Aes<Double>, breaks: List<Double>, resolution: Double): DoubleSpan? {
            val (loc, width) = p.finiteOrNull(coordAes, Aes.WIDTH) ?: return null
            val (leftSpan, rightSpan) = if (breaks.isEmpty()) {
                Pair(resolution / 2.0, resolution / 2.0)
            } else {
                val (i, j) = breaks.bracketingIndicesOrNull(loc) ?: return null
                Pair(loc - breaks[i], breaks[j] - loc)
            }
            return DoubleSpan(loc - width * leftSpan, loc + width * rightSpan)
        }
    }
}
