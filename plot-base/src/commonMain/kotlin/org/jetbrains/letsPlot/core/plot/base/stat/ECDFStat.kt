/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class ECDFStat(
    private val n: Int?
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }

        val statData = buildStat(data.getNumeric(TransformVar.X))

        return DataFrame.Builder()
            .putNumeric(Stats.X, statData.getValue(Stats.X))
            .putNumeric(Stats.Y, statData.getValue(Stats.Y))
            .build()
    }

    private fun buildStat(
        xs: List<Double?>
    ): Map<DataFrame.Variable, List<Double>> {
        val xValues = xs.filter { it?.isFinite() ?: false }.map { it!! }
        if (xValues.isEmpty()) {
            return mapOf(
                Stats.X to emptyList(),
                Stats.Y to emptyList(),
            )
        }

        val ecdf: (Double) -> Double = { t -> xValues.count { x -> x <= t }.toDouble() / xValues.size }
        val statX = if (n == null) {
            xValues.distinct()
        } else {
            linspace(xValues.min(), xValues.max(), n)
        }
        val statY = statX.map { ecdf(it) }

        return mapOf(
            Stats.X to statX,
            Stats.Y to statY,
        )
    }

    private fun linspace(start: Double, stop: Double, num: Int): List<Double> {
        if (num <= 0) return emptyList()
        if (num == 1) return listOf(start)
        val step = (stop - start) / (num - 1)
        return List(num) { start + it * step }
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}