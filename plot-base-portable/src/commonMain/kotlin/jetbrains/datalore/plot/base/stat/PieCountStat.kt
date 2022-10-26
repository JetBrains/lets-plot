/*
 * Copyright (c) 2022. JetBrains s.r.o.
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
 * Counts the number of cases at each x, y, fill position.
 * (or if the weight aesthetic is supplied, the sum of the weights)
 */
internal class PieCountStat: BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.FILL)) {
            return withEmptyStatValues()
        }

        val values = data.getNumeric(TransformVar.FILL)
        val xs = if (data.has(TransformVar.X)) {
            data.getNumeric(TransformVar.X)
        } else {
            List(values.size) { 0.0 }
        }
        val ys = if (data.has(TransformVar.Y)) {
            data.getNumeric(TransformVar.Y)
        } else {
            List(values.size) { 0.0 }
        }
        val weight = BinStatUtil.weightVector(values.size, data)

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statCount = ArrayList<Double>()

        val countByValues = countByValues(xs, ys, values, weight)
        for (key in countByValues.keys) {
            statX.add(key.first)
            statY.add(key.second)
            statCount.add(countByValues[key]!!.get())
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.Y, statY)
            .putNumeric(Stats.COUNT, statCount)
            .build()
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.SLICE to Stats.COUNT
        )

        private fun countByValues(
            valuesX: List<Double?>,
            valuesY: List<Double?>,
            valuesFill: List<Double?>,
            weight: List<Double?>
        ): Map<Triple<Double, Double, Double>, MutableDouble> {
            val result = LinkedHashMap<Triple<Double, Double, Double>, MutableDouble>()
            for (i in valuesX.indices) {
                val x = valuesX[i]
                val y = valuesY[i]
                val value = valuesFill[i]
                if (SeriesUtil.allFinite(x,y,value)) {
                    val key = Triple(x!!, y!!, value!!)
                    if (!result.containsKey(key)) {
                        result[key] = MutableDouble(0.0)
                    }
                    result[key]!!.getAndAdd(SeriesUtil.asFinite(weight[i], 0.0))
                }
            }
            return result
        }
    }
}