/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame

/**
 * Counts the number of cases at each (x, y) position
 * (or if the weight aesthetic is supplied, the sum of the weights and the proportion)
 */
internal class Count2dStat : AbstractCountStat(DEF_MAPPING, count2d = true) {

    override fun consumes(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        return listOf(org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y, org.jetbrains.letsPlot.core.plot.base.Aes.WEIGHT)
    }

    override fun addToStatVars(values: Set<Any>): Map<DataFrame.Variable, List<Double>> {
        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        values.forEach {
            it as Pair<*, *>
            statX += it.first as Double
            statY += it.second as Double
        }
        return mapOf(
            Stats.X to statX,
            Stats.Y to statY
        )
    }

    companion object {
        private val DEF_MAPPING: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DataFrame.Variable> = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X to Stats.X,
            org.jetbrains.letsPlot.core.plot.base.Aes.Y to Stats.Y,
            org.jetbrains.letsPlot.core.plot.base.Aes.SLICE to Stats.COUNT
        )
    }
}