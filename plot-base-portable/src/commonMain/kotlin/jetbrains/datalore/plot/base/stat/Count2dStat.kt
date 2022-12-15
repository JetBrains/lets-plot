/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame

/**
 * Counts the number of cases at each (x, y) position
 * (or if the weight aesthetic is supplied, the sum of the weights and the proportion)
 */
internal class Count2dStat : AbstractCountStat(DEF_MAPPING, count2d = true) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
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
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.SLICE to Stats.COUNT
        )
    }
}