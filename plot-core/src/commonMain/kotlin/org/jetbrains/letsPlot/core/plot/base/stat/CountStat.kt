/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame

/**
 * Counts the number of cases at each x position
 * (or if the weight aesthetic is supplied, the sum of the weights and the proportion)
 */
internal class CountStat : AbstractCountStat(DEF_MAPPING, count2d = false) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.WEIGHT)
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
