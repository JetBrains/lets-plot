/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil

class ViolinStat : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val ys = data.getNumeric(TransformVar.Y)
        val xs = if (data.has(TransformVar.X)) {
            data.getNumeric(TransformVar.X)
        } else {
            List<Double>(ys.size) { 0.0 }
        }

        val statData = buildStat(xs, ys)

        val statCount = statData.remove(Stats.COUNT)
        if (statCount == null || statCount.all { it == 0.0 }) {
            return withEmptyStatValues()
        }

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.YMIN to Stats.Y_MIN,
            Aes.YMAX to Stats.Y_MAX
        )

        fun buildStat(
            xs: List<Double?>,
            ys: List<Double?>
        ): MutableMap<DataFrame.Variable, List<Double>> {

            val xyPairs = xs.zip(ys).filter { (x, y) ->
                SeriesUtil.allFinite(x, y)
            }
            if (xyPairs.isEmpty()) {
                return mutableMapOf()
            }

            val binnedData: MutableMap<Double, MutableList<Double>> = HashMap()
            for ((x, y) in xyPairs) {
                binnedData.getOrPut(x!!) { ArrayList() }.add(y!!)
            }

            val statX = ArrayList<Double>()
            val statMin = ArrayList<Double>()
            val statMax = ArrayList<Double>()
            val statCount = ArrayList<Double>()

            for ((x, bin) in binnedData) {
                val count = bin.size.toDouble()
                val summary = FiveNumberSummary(bin)

                statX.add(x)
                statMin.add(summary.min)
                statMax.add(summary.max)
                statCount.add(count)
            }

            return mutableMapOf(
                Stats.X to statX,
                Stats.Y_MIN to statMin,
                Stats.Y_MAX to statMax,
                Stats.COUNT to statCount,
            )
        }
    }
}