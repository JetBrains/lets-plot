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

        val ys: List<Double>
        val ws: List<Double>
        // TODO: Move filtering and sorting into the buildStat()
        if (data.has(TransformVar.WEIGHT)) {
            val (ysFiltered, wsFiltered) = SeriesUtil.filterFinite(
                data.getNumeric(TransformVar.Y),
                data.getNumeric(TransformVar.WEIGHT)
            )
            val (ysSorted, wsSorted) = (ysFiltered zip wsFiltered)
                .sortedBy { it.first }
                .unzip()
            ys = ysSorted
            ws = wsSorted
        } else {
            ys = data.getNumeric(TransformVar.Y)
                .filterNotNull().filter { it.isFinite() }
                .sorted()
            ws = List(ys.size) { 1.0 }
        }
        if (ys.isEmpty()) return withEmptyStatValues()
        val xs = if (data.has(TransformVar.X)) {
            data.getNumeric(TransformVar.X)
        } else {
            List(ys.size) { 0.0 }
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
                val y = bin.first
                val weights = bin.second
                val ySummary = FiveNumberSummary(y)
                val rangeY = ClosedRange(ySummary.min, ySummary.max)
                val n = 512 // TODO: Should be a parameter
                val localStatY = DensityStatUtil.createStepValues(rangeY, n)
                statX += MutableList(localStatY.size) { x }
                statY += localStatY
                val localStatDensity = ArrayList<Double>()

                val fullScalMax = 5000 // TODO: Should be a parameter
                val kernel = DensityStat.Kernel.GAUSSIAN // TODO: Should be a parameter
                val bandWidth = DensityStatUtil.bandWidth(
                    DensityStat.DEF_BW,
                    y
                )
                val adjust = 1.0 // TODO: Should be a parameter
                val kernelFun: (Double) -> Double = DensityStatUtil.kernel(kernel)
                val densityFunction: (Double) -> Double = when (y.size <= fullScalMax) {
                    true -> DensityStatUtil.densityFunctionFullScan(
                        y,
                        weights,
                        kernelFun,
                        bandWidth,
                        adjust
                    )
                    false -> DensityStatUtil.densityFunctionFast(
                        y,
                        weights,
                        kernelFun,
                        bandWidth,
                        adjust
                    )
                }

                val nTotal = weights.sum()
                for (u in localStatY) {
                    val d = densityFunction(u)
                    localStatDensity.add(d / nTotal)
                }
                statDensity += localStatDensity
            }

            return mutableMapOf(
                Stats.X to statX,
                Stats.Y to statY,
                Stats.DENSITY to statDensity,
            )
        }
    }
}