/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics

class HistogramGeom : BarGeom(), WithWidth {
    private var breaks: List<Double> = emptyList()

    fun setBreaks(breaks: List<Double>) {
        this.breaks = breaks.filter { SeriesUtil.isFinite(it) }.sorted().takeIf { it.size >= 2 } ?: emptyList()
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return if (this.breaks.any()) {
            DoubleSpan(breaks.first(), breaks.last())
        } else {
            DimensionsUtil.dimensionSpan(p, coordAes, Aes.WIDTH, resolution, DimensionUnit.RESOLUTION)
        }
    }

    companion object {
        const val HANDLES_GROUPS = false
    }
}
