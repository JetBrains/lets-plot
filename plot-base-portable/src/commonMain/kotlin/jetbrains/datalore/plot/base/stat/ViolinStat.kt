/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil

class ViolinStat : BaseStat(DEF_MAPPING) {

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

        val statData = buildStat(xs, ys, ws)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.WEIGHT to Stats.DENSITY
        )

        private fun buildStat(
            xs: List<Double?>,
            ys: List<Double?>,
            ws: List<Double?>
        ): MutableMap<DataFrame.Variable, List<Double>> {
            val binnedData = (xs zip (ys zip ws))
                .groupBy({ it.first!! }, { it.second })
                .mapValues { it.value.unzip() }

            val statX = ArrayList<Double>()
            val statY = ArrayList<Double>()
            val statDensity = ArrayList<Double>()
            val statCount = ArrayList<Double>()
            val statScaled = ArrayList<Double>()

            for ((x, bin) in binnedData) {
                val (filteredY, filteredW) = SeriesUtil.filterFinite(bin.first, bin.second)
                val (binY, binW) = (filteredY zip filteredW)
                    .sortedBy { it.first }
                    .unzip()
                val ySummary = FiveNumberSummary(binY)
                val rangeY = ClosedRange(ySummary.min, ySummary.max)
                val binStatY = DensityStatUtil.createStepValues(rangeY, DensityStat.DEF_N)
                val densityFunction = getDensityFunction(binY, binW)
                val binStatCount = binStatY.map { densityFunction(it) }
                val weightsSum = binW.sum()
                val maxBinCount = binStatCount.maxOrNull()!!

                statX += MutableList(binStatY.size) { x }
                statY += binStatY
                statDensity += binStatCount.map { it / weightsSum }
                statCount += binStatCount
                statScaled += binStatCount.map { it / maxBinCount }
            }

            return mutableMapOf(
                Stats.X to statX,
                Stats.Y to statY,
                Stats.DENSITY to statDensity,
                Stats.COUNT to statCount,
                Stats.SCALED to statScaled
            )
        }

        private fun getDensityFunction(
            binY: List<Double>,
            binW: List<Double>
        ): (Double) -> Double {
            val bandWidth = DensityStatUtil.bandWidth(DensityStat.DEF_BW, binY)
            val kernelFun: (Double) -> Double = DensityStatUtil.kernel(DensityStat.DEF_KERNEL)
            return when (binY.size <= DensityStat.DEF_FULL_SCAN_MAX) {
                true -> DensityStatUtil.densityFunctionFullScan(
                    binY,
                    binW,
                    kernelFun,
                    bandWidth,
                    DensityStat.DEF_ADJUST
                )
                false -> DensityStatUtil.densityFunctionFast(
                    binY,
                    binW,
                    kernelFun,
                    bandWidth,
                    DensityStat.DEF_ADJUST
                )
            }
        }
    }
}