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

class DensityRidgesStat(
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
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }

        val xs = data.getNumeric(TransformVar.X)
        val ys = if (data.has(TransformVar.Y)) {
            data.getNumeric(TransformVar.Y)
        } else {
            List(xs.size) { 0.0 }
        }
        val ws = if (data.has(TransformVar.WEIGHT)) {
            data.getNumeric(TransformVar.WEIGHT)
        } else {
            List(ys.size) { 1.0 }
        }

        val overallXRange = statCtx.overallXRange() ?: DoubleSpan(-0.5, 0.5)
        val statData = DensityStatUtil.binnedStat(
            ys, xs, ws,
            trim, tailsCutoff, bandWidth, bandWidthMethod, adjust, kernel, n, fullScanMax, overallXRange, quantiles,
            binVarName = Stats.Y, valueVarName = Stats.X
        )

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val statHeight = if (dataAfterStat.rowCount() == 0) {
            emptyList()
        } else {
            val statDensity = dataAfterStat.getNumeric(Stats.DENSITY).map { it!! }
            val densityMax = statDensity.maxOrNull()!!
            statDensity.map { it / densityMax }
        }
        return dataAfterStat.builder()
            .putNumeric(Stats.HEIGHT, statHeight)
            .build()
    }

    companion object {
        const val DEF_TRIM = false
        val DEF_QUANTILES = listOf(0.25, 0.5, 0.75)

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.HEIGHT to Stats.HEIGHT,
            Aes.QUANTILE to Stats.QUANTILE
        )
    }
}