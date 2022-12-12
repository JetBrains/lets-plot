/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext

/**
 * Counts the number of cases at each x position
 * (or if the weight aesthetic is supplied, the sum of the weights and the proportion)
 */
internal class CountStat : AbstractCountStat(DEF_MAPPING, count2d = false) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }
        return super.apply(data, statCtx, messageConsumer)
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
