/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.intern.indicesOf
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class SinaStat(
    private val scale: Scale,
    private val trim: Boolean,
    private val tailsCutoff: Double?,
    private val bandWidth: Double?,
    private val bandWidthMethod: DensityStat.BandWidthMethod,
    private val adjust: Double,
    private val kernel: DensityStat.Kernel,
    private val n: Int,
    private val fullScanMax: Int,
    private val quantiles: List<Double>
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    // Almost the same as in YDensityStat::apply()
    override fun apply(
        data: DataFrame,
        statCtx: StatContext,
        messageConsumer: (String) -> Unit
    ): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val ys = data.getNumeric(TransformVar.Y)
        val xs = if (data.has(TransformVar.X)) {
            data.getNumeric(TransformVar.X)
        } else {
            List(ys.size) { 0.0 }
        }
        val ws = if (data.has(TransformVar.WEIGHT)) {
            data.getNumeric(TransformVar.WEIGHT)
        } else {
            List(ys.size) { 1.0 }
        }

        val overallYRange = statCtx.overallYRange() ?: DoubleSpan(-0.5, 0.5)
        val yDensityStatData = DensityStatUtil.binnedStat(xs, ys, ws, trim, tailsCutoff, bandWidth, bandWidthMethod, adjust, kernel, n, fullScanMax, overallYRange, quantiles)
        val yDensityStatBuilder = DataFrame.Builder()
        for ((variable, series) in yDensityStatData) {
            yDensityStatBuilder.putNumeric(variable, series)
        }
        val yDensityDf = yDensityStatBuilder.build()
        return appendSinaStatData(yDensityDf, xs, ys, ws)
    }

    // Similar to YDensityStat::normalize()
    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val sinaIndices = dataAfterStat.getNumeric(Stats.N).indicesOf { it == 1.0 }
        val sinaDf = dataAfterStat.slice(sinaIndices)
        val statViolinWidth = when {
            sinaDf.rowCount() == 0 -> emptyList()

            sinaDf.rowCount() == dataAfterStat.rowCount() -> List(sinaDf.rowCount()) { 0.0 }

            else -> {
                val yDensityIndices = dataAfterStat.getNumeric(Stats.N).indicesOf { it == 0.0 }
                val yDensityDf = dataAfterStat.slice(yDensityIndices)
                when (scale) {
                    Scale.AREA -> {
                        val yStatDensity = yDensityDf.getNumeric(Stats.DENSITY).map { it!! }
                        val sinaStatDensity = sinaDf.getNumeric(Stats.DENSITY).map { it!! }
                        val yDensityMax = yStatDensity.maxOrNull()!!
                        sinaStatDensity.map { it / yDensityMax }
                    }

                    Scale.COUNT -> {
                        val yStatDensity = yDensityDf.getNumeric(Stats.DENSITY).map { it!! }
                        val yDensityMax = yStatDensity.maxOrNull()!!
                        val yStatCount = yDensityDf.getNumeric(Stats.COUNT).map { it!! }
                        val yWidthsSumMax = yStatDensity.mapIndexed { i, d ->
                            if (d > 0) yStatCount[i] / d else Double.NaN
                        }.maxOrNull()!!
                        val norm = yDensityMax * yWidthsSumMax
                        val sinaStatCount = sinaDf.getNumeric(Stats.COUNT).map { it!! }
                        sinaStatCount.map { it / norm }
                    }

                    Scale.WIDTH -> {
                        sinaDf.getNumeric(Stats.SCALED)
                    }
                }
            }
        }
        return sinaDf.builder()
            .remove(Stats.N)
            .putNumeric(Stats.VIOLIN_WIDTH, statViolinWidth)
            .build()
    }

    private fun appendSinaStatData(
        yDensityDf: DataFrame,
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
            val indices = yDensityDf.getNumeric(Stats.X).indicesOf { it == x }
            val yDensityDfSlice = yDensityDf.slice(indices)
            val yValues = yDensityDfSlice.getNumeric(Stats.Y).map { it!! }
            statX += List(yDensityDfSlice.rowCount() + binValue.size) { x }
            statY += yValues + binValue
            statDensity += yDensityDfSlice.getNumeric(Stats.DENSITY).map { it!! }.let { densities ->
                densities + binValue.map(DensityStatUtil.pwLinInterp(yValues, densities))
            }
            statCount += yDensityDfSlice.getNumeric(Stats.COUNT).map { it!! }.let { counts ->
                counts + binValue.map(DensityStatUtil.pwLinInterp(yValues, counts))
            }
            statScaled += yDensityDfSlice.getNumeric(Stats.SCALED).map { it!! }.let { scaled ->
                scaled + binValue.map(DensityStatUtil.pwLinInterp(yValues, scaled))
            }
            statQuantile += yDensityDfSlice.getNumeric(Stats.QUANTILE).map { it!! }.let { quantiles ->
                quantiles + binValue.map(pwCeilInterp(yValues, quantiles))
            }
            statN += List(yDensityDfSlice.rowCount()) { 0.0 } + List(binValue.size) { 1.0 }
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

    // Analogue of the DensityStatUtil.pwLinInterp() function, but returns appropriate vertices of the piecewise linear interpolation function
    private fun pwCeilInterp(x: List<Double>, y: List<Double>): (Double) -> Double {
        return fun(t: Double): Double {
            val i = x.indexOfFirst { it >= t }
            if (i == 0) return y.first()
            if (i == -1) return y.last()
            return y[i]
        }
    }

    // The same as in YDensityStat::Scale
    enum class Scale {
        AREA,
        COUNT,
        WIDTH
    }

    // The same as in YDensityStat
    companion object {
        val DEF_SCALE = Scale.AREA
        const val DEF_TRIM = true
        const val DEF_TAILS_CUTOFF = 3.0
        val DEF_QUANTILES = listOf(0.25, 0.5, 0.75)

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.VIOLINWIDTH to Stats.VIOLIN_WIDTH,
            Aes.QUANTILE to Stats.QUANTILE
        )
    }
}