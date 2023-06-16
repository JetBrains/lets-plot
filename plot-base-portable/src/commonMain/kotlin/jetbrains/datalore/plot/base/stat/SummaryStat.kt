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
    private val yAgg: (SummaryStatUtil.SummaryCalculator) -> Double,
    private val minAgg: (SummaryStatUtil.SummaryCalculator) -> Double,
    private val maxAgg: (SummaryStatUtil.SummaryCalculator) -> Double,
    private val middleAgg: (SummaryStatUtil.SummaryCalculator) -> Double,
    private val lowerAgg: (SummaryStatUtil.SummaryCalculator) -> Double,
    private val upperAgg: (SummaryStatUtil.SummaryCalculator) -> Double
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
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val xyPairs = SeriesUtil.filterFinite(xs, ys)
            .let { (xs, ys) -> xs zip ys }
        if (xyPairs.isEmpty()) {
            return mutableMapOf()
        }

        val binnedData: MutableMap<Double, MutableList<Double>> = HashMap()
        for ((x, y) in xyPairs) {
            binnedData.getOrPut(x) { ArrayList() }.add(y)
        }

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statMin = ArrayList<Double>()
        val statMax = ArrayList<Double>()
        val statMiddle = ArrayList<Double>()
        val statLower = ArrayList<Double>()
        val statUpper = ArrayList<Double>()

        for ((x, bin) in binnedData) {
            val calc = SummaryStatUtil.SummaryCalculator(bin)
            statX.add(x)
            statY.add(yAgg(calc))
            statMin.add(minAgg(calc))
            statMax.add(maxAgg(calc))
            statMiddle.add(middleAgg(calc))
            statLower.add(lowerAgg(calc))
            statUpper.add(upperAgg(calc))
        }

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY,
            Stats.Y_MIN to statMin,
            Stats.Y_MAX to statMax,
            Stats.MIDDLE to statMiddle,
            Stats.LOWER to statLower,
            Stats.UPPER to statUpper,
        )
    }

    companion object {
        const val DEF_Y_AGG_FUN = "mean"
        const val DEF_MIN_AGG_FUN = "min"
        const val DEF_MAX_AGG_FUN = "max"
        const val DEF_MIDDLE_AGG_FUN = "nan"
        const val DEF_LOWER_AGG_FUN = "nan"
        const val DEF_UPPER_AGG_FUN = "nan"

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.YMIN to Stats.Y_MIN,
            Aes.YMAX to Stats.Y_MAX,
            Aes.MIDDLE to Stats.MIDDLE,
            Aes.LOWER to Stats.LOWER,
            Aes.UPPER to Stats.UPPER
        )
    }
}