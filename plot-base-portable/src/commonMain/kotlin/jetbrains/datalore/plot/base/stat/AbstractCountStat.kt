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


abstract class AbstractCountStat(
    defaultMappings: Map<Aes<*>, DataFrame.Variable>,
    private val baseAes: Aes<*>
) : BaseStat(defaultMappings) {

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, baseAes)) {
            return withEmptyStatValues()
        }

        val baseValues = data.getNumeric(TransformVar.forAes(baseAes))
        val weight = BinStatUtil.weightVector(baseValues.size, data)
        val aesList = consumes() - Aes.WEIGHT
        val values = aesList.map { aes ->
            val variable = TransformVar.forAes(aes)
            if (data.has(variable)) {
                data.getNumeric(variable)
            } else {
                List(baseValues.size) { 0.0 }
            }
        }

        val statCount = ArrayList<Double>()
        val stats = List(aesList.size) { ArrayList<Double>() }

        val countBy = countByValues(baseValues, values, weight)
        for (key in countBy.keys) {
            key.second.forEachIndexed { index, value -> stats[index].add(value) }
            statCount.add(countBy[key]!!.get())
        }

        val statVariables = aesList.map { aes -> getDefaultMapping(aes) }

        return DataFrame.Builder().apply {
            statVariables.forEachIndexed { index, statVar ->
                putNumeric(statVar, stats[index])
            }
            putNumeric(Stats.COUNT, statCount)
        }.build()
    }

    companion object {
        private fun countByValues(
            baseValues: List<Double?>,
            values: List<List<Double?>>,
            weight: List<Double?>
        ): Map<Pair<Double, List<Double>>, MutableDouble> {
            val result = LinkedHashMap<Pair<Double, List<Double>>, MutableDouble>()
            for (i in baseValues.indices) {
                val v = baseValues[i]
                val others = values.map { it[i] }

                if (SeriesUtil.isFinite(v) && others.all { SeriesUtil.isFinite(it) }) {
                    val key = v!! to others.map { it!! }
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