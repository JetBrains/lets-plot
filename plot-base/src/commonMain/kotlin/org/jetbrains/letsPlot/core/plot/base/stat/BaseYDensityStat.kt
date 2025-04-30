/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

abstract class BaseYDensityStat(
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

    final override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    abstract fun applyPostProcessing(statData: DataFrame, xs: List<Double?>, ys: List<Double?>, ws: List<Double?>): DataFrame

    final override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
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
        val statData = DensityStatUtil.binnedStat(
            xs,
            ys,
            ws,
            trim,
            tailsCutoff,
            bandWidth,
            bandWidthMethod,
            adjust,
            kernel,
            n,
            fullScanMax,
            overallYRange,
            quantiles
        )

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return applyPostProcessing(builder.build(), xs, ys, ws)
    }

    protected fun areaViolinWidth(statData: DataFrame, normalizationData: DataFrame? = null): List<Double> {
        val statDensity = statData.getNumeric(Stats.DENSITY).map { it!! }
        val normalizationDensity = normalizationData?.getNumeric(Stats.DENSITY)?.map { it!! } ?: statDensity
        val densityMax = normalizationDensity.max()
        return statDensity.map { it / densityMax }
    }

    protected fun countViolinWidth(statData: DataFrame, normalizationData: DataFrame? = null): List<Double> {
        val statCount = statData.getNumeric(Stats.COUNT).map { it!! }
        val normalizationDensity = (normalizationData ?: statData).getNumeric(Stats.DENSITY).map { it!! }
        val normalizationCount = normalizationData?.getNumeric(Stats.COUNT)?.map { it!! } ?: statCount
        val densityMax = normalizationDensity.max()
        val weightsSumMax = (normalizationCount.asSequence() zip normalizationDensity.asSequence())
            .filter { (_, density) -> density > 0 }
            .maxOf { (count, density) -> count / density }
        val norm = densityMax * weightsSumMax
        return statCount.map { it / norm }
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