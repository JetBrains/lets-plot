/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.math3.UniformDistribution

class ECDFStat : BaseStat(DEF_MAPPING) {

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
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val statX = xs.filter { it?.isFinite() ?: false }.map { it!! }.sorted()
        val t = (1..statX.size).map { (it - 0.5) / statX.size }
        val dist = UniformDistribution(0.0, 1.0)
        val statY = t.map { dist.inverseCumulativeProbability(it) }

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY
        )
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}