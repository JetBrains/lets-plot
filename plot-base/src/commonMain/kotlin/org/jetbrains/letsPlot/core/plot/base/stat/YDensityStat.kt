/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class YDensityStat(
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

    init {
        require(n <= DensityStat.MAX_N) {
            "The input n = $n > ${DensityStat.MAX_N} is too large!"
        }
    }

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
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
        val statData = DensityStatUtil.binnedStat(xs, ys, ws, trim, tailsCutoff, bandWidth, bandWidthMethod, adjust, kernel, n, fullScanMax, overallYRange, quantiles)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val statViolinWidth = if (dataAfterStat.rowCount() == 0) {
            emptyList()
        } else {
            when (scale) {
                Scale.AREA -> {
                    val statDensity = dataAfterStat.getNumeric(Stats.DENSITY).map { it!! }
                    val densityMax = statDensity.maxOrNull()!!
                    statDensity.map { it / densityMax }
                }
                Scale.COUNT -> {
                    val statDensity = dataAfterStat.getNumeric(Stats.DENSITY).map { it!! }
                    val densityMax = statDensity.maxOrNull()!!
                    val statCount = dataAfterStat.getNumeric(Stats.COUNT).map { it!! }
                    val widthsSumMax = statDensity.mapIndexed { i, d ->
                        if (d > 0) statCount[i] / d else Double.NaN
                    }.maxOrNull()!!
                    val norm = densityMax * widthsSumMax
                    statCount.map { it / norm }
                }
                Scale.WIDTH -> {
                    dataAfterStat.getNumeric(Stats.SCALED)
                }
            }
        }
        return dataAfterStat.builder()
            .putNumeric(Stats.VIOLIN_WIDTH, statViolinWidth)
            .build()
    }

    enum class Scale {
        AREA,
        COUNT,
        WIDTH
    }

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