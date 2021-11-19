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
            Aes.WEIGHT to Stats.DENSITY,
        )

        fun buildStat(
            xs: List<Double?>,
            ys: List<Double?>,
            ws: List<Double?>
        ): MutableMap<DataFrame.Variable, List<Double>> {
            val binnedData: MutableMap<Double, Pair<MutableList<Double>, MutableList<Double>>> = HashMap()
            for ((x, p) in xs zip (ys zip ws)) {
                binnedData.getOrPut(x!!) { Pair(ArrayList(), ArrayList()) }
                binnedData[x]?.first?.add(p.first!!)
                binnedData[x]?.second?.add(p.second!!)
            }

            val statX = ArrayList<Double>()
            val statY = ArrayList<Double>()
            val statDensity = ArrayList<Double>()

            for ((x, bin) in binnedData) {
                val (ysFiltered, wsFiltered) = SeriesUtil.filterFinite(bin.first, bin.second)
                val (ysSorted, wsSorted) = (ysFiltered zip wsFiltered)
                    .sortedBy { it.first }
                    .unzip()
                val binY = ysSorted.toMutableList()
                val binW = wsSorted.toMutableList()

                val ySummary = FiveNumberSummary(binY)
                val rangeY = ClosedRange(ySummary.min, ySummary.max)
                val localStatY = DensityStatUtil.createStepValues(rangeY, DensityStat.DEF_N)

                val bandWidth = DensityStatUtil.bandWidth(DensityStat.DEF_BW, binY)
                val kernelFun: (Double) -> Double = DensityStatUtil.kernel(DensityStat.DEF_KERNEL)
                val densityFunction: (Double) -> Double = when (binY.size <= DensityStat.DEF_FULL_SCAN_MAX) {
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

                val nTotal = binW.sum()
                for (y in localStatY) {
                    statDensity.add(densityFunction(y) / nTotal)
                }

                statX += MutableList(localStatY.size) { x }
                statY += localStatY
            }

            return mutableMapOf(
                Stats.X to statX,
                Stats.Y to statY,
                Stats.DENSITY to statDensity
            )
        }
    }
}