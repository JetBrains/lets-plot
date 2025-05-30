/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.DataFrame

class YDensityStat(
    private val scale: Scale,
    trim: Boolean,
    tailsCutoff: Double?,
    bandWidth: Double?,
    bandWidthMethod: DensityStat.BandWidthMethod,
    adjust: Double,
    kernel: DensityStat.Kernel,
    n: Int,
    fullScanMax: Int,
    quantiles: List<Double>
) : BaseYDensityStat(
    trim,
    tailsCutoff,
    bandWidth,
    bandWidthMethod,
    adjust,
    kernel,
    n,
    fullScanMax,
    quantiles
) {
    override fun applyPostProcessing(statData: DataFrame, xs: List<Double?>, ys: List<Double?>, ws: List<Double?>): DataFrame {
        return statData
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val statViolinWidth = if (dataAfterStat.rowCount() == 0) {
            emptyList()
        } else {
            when (scale) {
                Scale.AREA -> areaViolinWidth(dataAfterStat)
                Scale.COUNT -> countViolinWidth(dataAfterStat)
                Scale.WIDTH -> dataAfterStat.getNumeric(Stats.SCALED)
            }
        }
        return dataAfterStat.builder()
            .putNumeric(Stats.VIOLIN_WIDTH, statViolinWidth)
            .build()
    }
}