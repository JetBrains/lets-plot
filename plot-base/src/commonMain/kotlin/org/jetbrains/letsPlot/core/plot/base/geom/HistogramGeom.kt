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
        this.breaks = breaks.filter(Double::isFinite)
    }

    override fun getBinSpanCalculator(ctx: GeomContext): (DataPointAesthetics) -> DoubleSpan? {
        val resolution = ctx.getResolution(Aes.X)
        return { p -> binSpan(p, breaks, resolution) }
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return binSpan(p, breaks, resolution)
    }

    companion object {
        const val HANDLES_GROUPS = false

        fun binSpan(p: DataPointAesthetics, breaks: List<Double>, resolution: Double): DoubleSpan? {
            val (x, width) = p.finiteOrNull(Aes.X, Aes.WIDTH) ?: return null
            val span = if (breaks.isEmpty()) {
                DoubleSpan(x - resolution / 2.0, x + resolution / 2.0)
            } else {
                val (i, j) = breaks.bracketingIndicesOrNull(x) ?: return null
                DoubleSpan(breaks[i], breaks[j])
            }
            return span.multiplied(width)
        }
    }
}
