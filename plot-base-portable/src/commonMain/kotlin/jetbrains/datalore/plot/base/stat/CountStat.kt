/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar

/**
 * Counts the number of cases at each x position
 * (or if the weight aesthetic is supplied, the sum of the weights and the proportion)
 */
internal class CountStat : AbstractCountStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }
        return super.apply(data, statCtx, messageConsumer)
    }

    override fun getValuesToAggregateBy(data: DataFrame, fromStatVars: Boolean): List<Any?> {
        val xVar = if (fromStatVars) Stats.X else TransformVar.X
        return data[xVar]
    }

    override fun addToStatVars(values: Set<Any>): Map<DataFrame.Variable, List<Double>> {
        val statX = values.map { it as Double }
        return mapOf(Stats.X to statX)
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.COUNT
        )
    }
}
