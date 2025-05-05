/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.intern.indicesOf
import org.jetbrains.letsPlot.core.plot.base.DataFrame

class SinaStat(
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
    override fun applyPostProcessing(
        statData: DataFrame,
        xs: List<Double?>,
        ys: List<Double?>,
        ws: List<Double?>
    ): DataFrame {
        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statScaled = ArrayList<Double>()
        val statQuantile = ArrayList<Double>()
        val statN = ArrayList<Double>()

        DensityStatUtil.handleBinnedData(xs, ys, ws) { x, binValue, _ ->
            val indices = statData.getNumeric(Stats.X).indicesOf { it == x }
            val statDataSlice = statData.slice(indices)
            val yValues = statDataSlice.getNumeric(Stats.Y).map { it!! }
            statX += List(statDataSlice.rowCount() + binValue.size) { x }
            statY += yValues + binValue
            statDensity += statDataSlice.getNumeric(Stats.DENSITY).map { it!! }.let { densities ->
                densities + binValue.map(DensityStatUtil.pwLinInterp(yValues, densities))
            }
            statCount += statDataSlice.getNumeric(Stats.COUNT).map { it!! }.let { counts ->
                counts + binValue.map(DensityStatUtil.pwLinInterp(yValues, counts))
            }
            statScaled += statDataSlice.getNumeric(Stats.SCALED).map { it!! }.let { scaled ->
                scaled + binValue.map(DensityStatUtil.pwLinInterp(yValues, scaled))
            }
            statQuantile += statDataSlice.getNumeric(Stats.QUANTILE).map { it!! }.let { quantiles ->
                quantiles + binValue.map(pwCeilInterp(yValues, quantiles))
            }
            statN += List(statDataSlice.rowCount()) { 0.0 } + List(binValue.size) { 1.0 }
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.Y, statY)
            .putNumeric(Stats.DENSITY, statDensity)
            .putNumeric(Stats.COUNT, statCount)
            .putNumeric(Stats.SCALED, statScaled)
            .putNumeric(Stats.QUANTILE, statQuantile)
            .putNumeric(Stats.N, statN)
            .build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val sinaIndices = dataAfterStat.getNumeric(Stats.N).indicesOf { it == 1.0 }
        val sinaData = dataAfterStat.slice(sinaIndices)
        val statViolinWidth = when {
            sinaData.rowCount() == 0 -> emptyList()
            sinaData.rowCount() == dataAfterStat.rowCount() -> List(sinaData.rowCount()) { 0.0 }
            else -> {
                val yDensityIndices = dataAfterStat.getNumeric(Stats.N).indicesOf { it == 0.0 }
                val yDensityData = dataAfterStat.slice(yDensityIndices)
                when (scale) {
                    Scale.AREA -> areaViolinWidth(sinaData, yDensityData)
                    Scale.COUNT -> countViolinWidth(sinaData, yDensityData)
                    Scale.WIDTH -> sinaData.getNumeric(Stats.SCALED)
                }
            }
        }
        return sinaData.builder()
            .remove(Stats.N)
            .putNumeric(Stats.VIOLIN_WIDTH, statViolinWidth)
            .build()
    }

    // Analogue of the DensityStatUtil.pwLinInterp() function, but returns appropriate vertices of the piecewise linear interpolation function
    private fun pwCeilInterp(x: List<Double>, y: List<Double>): (Double) -> Double {
        return fun(t: Double): Double {
            val i = x.indexOfFirst { it >= t }
            if (i == 0) return y.first()
            if (i == -1) return y.last()
            return y[i]
        }
    }
}