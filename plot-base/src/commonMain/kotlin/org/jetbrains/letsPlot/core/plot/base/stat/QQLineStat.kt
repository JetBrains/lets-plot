/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.math.min
import kotlin.math.max

class QQLineStat(
    private val distribution: QQStat.Distribution,
    private val distributionParameters: List<Double>,
    private val lineQuantiles: Pair<Double, Double>
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.SAMPLE)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.SAMPLE)) {
            return withEmptyStatValues()
        }

        val statData = buildStat(data.getNumeric(TransformVar.SAMPLE))

        return DataFrame.Builder()
            .putNumeric(Stats.THEORETICAL, statData.getValue(Stats.THEORETICAL))
            .putNumeric(Stats.SAMPLE, statData.getValue(Stats.SAMPLE))
            .build()
    }

    private fun buildStat(
        sampleSeries: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val sortedSample = sampleSeries.filter { it?.isFinite() ?: false }.map { it!! }.sorted()
        if (!sortedSample.any()) {
            return mutableMapOf(
                Stats.THEORETICAL to emptyList(),
                Stats.SAMPLE to emptyList()
            )
        }

        val quantilesSample = Pair(
            QQStatUtil.getEstimatedQuantile(sortedSample, lineQuantiles.first),
            QQStatUtil.getEstimatedQuantile(sortedSample, lineQuantiles.second)
        )
        val dist = QQStatUtil.getDistribution(distribution, distributionParameters)
        // Use min/max to avoid an infinity
        val quantilesTheoretical = Pair(
            dist.inverseCumulativeProbability(max(0.5 / sortedSample.size, lineQuantiles.first)),
            dist.inverseCumulativeProbability(min(1.0 - 0.5 / sortedSample.size, lineQuantiles.second))
        )
        val endpointsTheoretical = listOf(
            dist.inverseCumulativeProbability(0.5 / sortedSample.size),
            dist.inverseCumulativeProbability(1.0 - 0.5 / sortedSample.size)
        )
        if (quantilesTheoretical.first == quantilesTheoretical.second) {
            return mutableMapOf(
                Stats.THEORETICAL to listOf(quantilesTheoretical.first, quantilesTheoretical.second),
                Stats.SAMPLE to listOf(sortedSample.first(), sortedSample.last())
            )
        }

        val line = QQStatUtil.lineByPoints(quantilesTheoretical, quantilesSample)
        return mutableMapOf(
            Stats.THEORETICAL to endpointsTheoretical,
            Stats.SAMPLE to endpointsTheoretical.map { line(it) }
        )
    }

    companion object {
        val DEF_LINE_QUANTILES = Pair(0.25, 0.75)

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.THEORETICAL,
            Aes.Y to Stats.SAMPLE
        )
    }
}