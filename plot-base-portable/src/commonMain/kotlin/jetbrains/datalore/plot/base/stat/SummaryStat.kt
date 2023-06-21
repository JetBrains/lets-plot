/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil

class SummaryStat(
    private val aggFunctionsMap: Map<Aes<*>, (SummaryCalculator) -> Double>
) : BaseStat(DEF_MAPPING) {

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
            List(ys.size) { 0.0 }
        }

        val statData = buildStat(xs, ys)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    private fun buildStat(
        xs: List<Double?>,
        ys: List<Double?>
    ): Map<DataFrame.Variable, List<Double>> {
        val binnedData = SeriesUtil.filterFinite(xs, ys)
            .let { (xs, ys) -> xs zip ys }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
        if (binnedData.isEmpty()) {
            return emptyMap()
        }

        val statValues: Map<Aes<*>, MutableList<Double>> = DEF_MAPPING.keys.associateWith { mutableListOf() }
        val defaultAggFun: (SummaryCalculator) -> Double = { calc -> calc.nan }
        for ((x, bin) in binnedData) {
            val calc = SummaryCalculator(bin)
            for (aes in statValues.keys) {
                if (aes == Aes.X) {
                    statValues[aes]!!.add(x)
                } else {
                    statValues[aes]!!.add(aggFunctionsMap.getOrElse(aes) { defaultAggFun }(calc))
                }
            }
        }

        return statValues.map { (aes, values) -> Pair(DEF_MAPPING[aes]!!, values) }.toMap()
    }

    companion object {
        const val DEF_Y_AGG_FUN = "mean"
        const val DEF_MIN_AGG_FUN = "min"
        const val DEF_MAX_AGG_FUN = "max"
        const val DEF_AGG_FUN = "nan"

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.YMIN to Stats.Y_MIN,
            Aes.YMAX to Stats.Y_MAX,
            Aes.MIDDLE to Stats.MIDDLE,
            Aes.LOWER to Stats.LOWER,
            Aes.UPPER to Stats.UPPER,
        )
    }
}