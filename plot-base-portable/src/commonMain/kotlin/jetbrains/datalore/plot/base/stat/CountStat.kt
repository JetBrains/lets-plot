/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.util.MutableDouble

/**
 * Counts the number of cases at each x position.
 * (or if the weight aesthetic is supplied, the sum of the weights)
 */
internal class CountStat : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }

        val valuesX = data.getNumeric(TransformVar.X)
        val weight = BinStatUtil.weightVector(valuesX.size, data)

        val statX = ArrayList<Double>()
        val statCount = ArrayList<Double>()

        val countByX = countByX(valuesX, weight)
        for (x in countByX.keys) {
            statX.add(x)
            statCount.add(countByX[x]!!.get())
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.COUNT, statCount)
            .build()
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.COUNT
        )

        private fun countByX(valuesX: List<Double?>, weight: List<Double?>): Map<Double, MutableDouble> {
            val result = LinkedHashMap<Double, MutableDouble>()
            for (i in valuesX.indices) {
                val x = valuesX[i]
                if (SeriesUtil.isFinite(x)) {
                    if (!result.containsKey(x!!)) {
                        result[x] = MutableDouble(0.0)
                    }
                    result[x]!!.getAndAdd(SeriesUtil.asFinite(weight[i], 0.0))
                }
            }
            return result
        }
    }
}