/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.*

class QQLineStat(
    private val distribution: QQStat.Distribution,
    private val distributionParameters: List<Double>,
    private val lineQuantiles: Pair<Double, Double>
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val ys = data.getNumeric(TransformVar.Y)
        val statData = if (data.has(TransformVar.X)) {
            val xs = data.getNumeric(TransformVar.X)
            buildSampleSampleStat(xs, ys)
        } else {
            buildSampleTheoreticalStat(ys)
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statData[Stats.X]!!)
            .putNumeric(Stats.Y, statData[Stats.Y]!!)
            .build()
    }

    private fun buildSampleSampleStat(
        xs: List<Double?>,
        ys: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val (finiteX, finiteY) = (xs zip ys).filter { (x, y) ->
            SeriesUtil.allFinite(x, y)
        }.unzip()
        val sortedX = finiteX.map { it!! }.sorted()
        val sortedY = finiteY.map { it!! }.sorted()

        return buildStat(
            getQuantiles(sortedX),
            getQuantiles(sortedY),
            Pair(sortedX.first(), sortedX.last())
        )
    }

    private fun buildSampleTheoreticalStat(
        ys: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val sortedY = ys
            .filter { it?.isFinite() == true }
            .map { it!! }
            .sorted()
        val quantilesY = getQuantiles(sortedY)
        val dist = QQStatUtil.getDistribution(distribution, distributionParameters)
        // Use min/max to avoid an infinity
        val quantilesX = Pair(
            dist.inverseCumulativeProbability(max(0.5 / sortedY.size, lineQuantiles.first)),
            dist.inverseCumulativeProbability(min(1.0 - 0.5 / sortedY.size, lineQuantiles.second))
        )
        val endpointsX = Pair(
            dist.inverseCumulativeProbability(0.5 / sortedY.size),
            dist.inverseCumulativeProbability(1.0 - 0.5 / sortedY.size)
        )

        return buildStat(quantilesX, quantilesY, endpointsX)
    }

    private fun buildStat(
        quantilesX: Pair<Double, Double>,
        quantilesY: Pair<Double, Double>,
        endpointsX: Pair<Double, Double>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val slope = (quantilesY.second - quantilesY.first) / (quantilesX.second - quantilesX.first)
        val intercept = quantilesY.first - slope * quantilesX.first
        val statX = listOf(endpointsX.first, endpointsX.second)
        val statY = statX.map { x -> slope * x + intercept }

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY
        )
    }

    private fun getQuantiles(
        sortedSeries: List<Double>
    ): Pair<Double, Double> {
        val i = (lineQuantiles.first * (sortedSeries.size - 1)).roundToInt()
        val j = (lineQuantiles.second * (sortedSeries.size - 1)).roundToInt()

        return Pair(sortedSeries[i], sortedSeries[j])
    }

    companion object {
        val DEF_LINE_QUANTILES = Pair(0.25, 0.75)

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}