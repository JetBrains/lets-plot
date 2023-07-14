/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class QQ2LineStat(
    private val lineQuantiles: Pair<Double, Double>
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X) || !hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val statData = buildStat(
            data.getNumeric(TransformVar.X),
            data.getNumeric(TransformVar.Y)
        )

        return DataFrame.Builder()
            .putNumeric(Stats.X, statData.getValue(Stats.X))
            .putNumeric(Stats.Y, statData.getValue(Stats.Y))
            .build()
    }

    private fun buildStat(
        xs: List<Double?>,
        ys: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val sortedX = xs.filter { it?.isFinite() ?: false }.map { it!! }.sorted()
        val sortedY = ys.filter { it?.isFinite() ?: false }.map { it!! }.sorted()
        if (!sortedX.any() || !sortedY.any()) {
            return mutableMapOf(
                Stats.X to emptyList(),
                Stats.Y to emptyList()
            )
        }

        val quantilesX = Pair(
            QQStatUtil.getEstimatedQuantile(sortedX, lineQuantiles.first),
            QQStatUtil.getEstimatedQuantile(sortedX, lineQuantiles.second)
        )
        val quantilesY = Pair(
            QQStatUtil.getEstimatedQuantile(sortedY, lineQuantiles.first),
            QQStatUtil.getEstimatedQuantile(sortedY, lineQuantiles.second)
        )
        if (quantilesX.first == quantilesX.second) {
            return mutableMapOf(
                Stats.X to listOf(quantilesX.first, quantilesX.second),
                Stats.Y to listOf(sortedY.first(), sortedY.last())
            )
        }

        val line = QQStatUtil.lineByPoints(quantilesX, quantilesY)
        return mutableMapOf(
            Stats.X to listOf(sortedX.first(), sortedX.last()),
            Stats.Y to listOf(line(sortedX.first()), line(sortedX.last()))
        )
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}