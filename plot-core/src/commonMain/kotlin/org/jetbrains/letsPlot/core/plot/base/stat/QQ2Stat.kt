/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class QQ2Stat : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        return listOf(org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, org.jetbrains.letsPlot.core.plot.base.Aes.X) || !hasRequiredValues(data, org.jetbrains.letsPlot.core.plot.base.Aes.Y)) {
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

        return if (!sortedX.any() || !sortedY.any()) {
            mutableMapOf(
                Stats.X to emptyList(),
                Stats.Y to emptyList()
            )
        } else {
            val t = (1..sortedX.size).map { (it - 0.5) / sortedX.size }
            mutableMapOf(
                Stats.X to t.map { QQStatUtil.getEstimatedQuantile(sortedX, it) },
                Stats.Y to t.map { QQStatUtil.getEstimatedQuantile(sortedY, it) }
            )
        }
    }

    companion object {
        private val DEF_MAPPING: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DataFrame.Variable> = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X to Stats.X,
            org.jetbrains.letsPlot.core.plot.base.Aes.Y to Stats.Y
        )
    }
}