/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.math.abs
import kotlin.random.Random

class SinaStat(
    private val scale: Scale,
    private val bandWidth: Double?,
    private val bandWidthMethod: DensityStat.BandWidthMethod,
    private val adjust: Double,
    private val kernel: DensityStat.Kernel,
    private val n: Int,
    private val fullScanMax: Int,
    private val quantiles: List<Double>,
    private val seed: Long?,
    private val jitterY: Boolean
) : BaseStat(DEF_MAPPING) {

    init {
        require(n <= DensityStat.MAX_N) {
            "The input n = $n > ${DensityStat.MAX_N} is too large!"
        }
    }

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
        val statData = DensityStatUtil.binnedStat(xs, ys, ws, true, null, bandWidth, bandWidthMethod, adjust, kernel, n, fullScanMax, overallYRange, quantiles, resetValueRange = false) // Differences is: trim = true, tailsCutoff = null, resetValueRange = false

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val statViolinWidth: List<Double> = if (dataAfterStat.rowCount() == 0) {
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
                    dataAfterStat.getNumeric(Stats.SCALED).map { it!! }
                }
            }
        }
        val statX = dataAfterStat.getNumeric(Stats.X).map { it!! }
        val statY = dataAfterStat.getNumeric(Stats.Y).map { it!! }
        val rand = seed?.let { Random(seed) } ?: Random.Default
        val yRes = SeriesUtil.resolution(statY, 0.0)
        val jitterY: Boolean = if (!integerish(statY, yRes)) {
            false
        } else {
            jitterY
        }
        val statJitteredX = (statX zip statViolinWidth).map { (x, violinwidth) ->
            val sign = if (rand.nextBoolean()) 1 else -1
            val randomWidthShift = rand.nextDouble()
            val widthLimit = violinwidth / 2.0
            x + sign * randomWidthShift * widthLimit
        }
        val statJitteredY = if (jitterY) {
            val sign = if (rand.nextBoolean()) 1 else -1
            val randomHeightShift = rand.nextDouble()
            val heightLimit = DY * yRes / 2.0
            statY.map { it + sign * randomHeightShift * heightLimit }
        } else {
            statY
        }
        return dataAfterStat.builder()
            .remove(Stats.X)
            .remove(Stats.Y)
            .putNumeric(Stats.X, statJitteredX)
            .putNumeric(Stats.Y, statJitteredY)
            .build()
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
        val DEF_QUANTILES = listOf(0.25, 0.5, 0.75)
        const val DEF_JITTER_Y = true

        private const val INTEGERISH_EPSILON = 1e-9
        private const val DY = .5 // y-jitter height factor, 1 means full height

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.VIOLINWIDTH to Stats.VIOLIN_WIDTH,
            Aes.QUANTILE to Stats.QUANTILE
        )

        private fun integerish(values: List<Double>, resolution: Double): Boolean {
            return values.all { abs(it - it.toLong()) < resolution * INTEGERISH_EPSILON }
        }
    }
}