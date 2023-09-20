/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil

class SummaryStat(
    private val yAggFunction: (List<Double>) -> Double,
    private val yMinAggFunction: (List<Double>) -> Double,
    private val yMaxAggFunction: (List<Double>) -> Double
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
        if (statData.isEmpty()) {
            return withEmptyStatValues()
        }

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

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statYMin = ArrayList<Double>()
        val statYMax = ArrayList<Double>()
        for ((x, bin) in binnedData) {
            val sortedBin = bin.sorted()
            statX.add(x)
            statY.add(yAggFunction(sortedBin))
            statYMin.add(yMinAggFunction(sortedBin))
            statYMax.add(yMaxAggFunction(sortedBin))
        }

        return mapOf(
            Stats.X to statX,
            Stats.Y to statY,
            Stats.Y_MIN to statYMin,
            Stats.Y_MAX to statYMax,
        )
    }

    companion object {
        val DEF_QUANTILES = Triple(0.25, 0.5, 0.75)

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.YMIN to Stats.Y_MIN,
            Aes.YMAX to Stats.Y_MAX
        )
    }
}