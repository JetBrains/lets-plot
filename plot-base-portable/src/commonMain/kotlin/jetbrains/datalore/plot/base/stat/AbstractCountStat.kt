/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.util.MutableDouble

abstract class AbstractCountStat(defaultMappings: Map<Aes<*>, DataFrame.Variable>) : BaseStat(defaultMappings) {

    protected abstract fun getValuesToAggregateBy(data: DataFrame, fromStatVars: Boolean): List<Any?>

    protected abstract fun addToStatVars(values: Set<Any>): Map<DataFrame.Variable, List<Double>>

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        val values = getValuesToAggregateBy(data, fromStatVars = false)
        val weight = BinStatUtil.weightVector(data.rowCount(), data)

        val computedCount = computeCount(values, weight)

        val stat = addToStatVars(computedCount.keys).toMutableMap()
        stat[Stats.COUNT] = computedCount.values.map(MutableDouble::get)

        return DataFrame.Builder().apply {
            stat.forEach { (statVar, values) -> putNumeric(statVar, values) }
        }.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val values = getValuesToAggregateBy(dataAfterStat, fromStatVars = true)
        val counts = dataAfterStat.getNumeric(Stats.COUNT).map { it!! }

        val computedCount = computeCount(values, counts)

        val sumStatCount = ArrayList<Double>()
        val prop = ArrayList<Double>()
        for (i in counts.indices) {
            val sum = computedCount[values[i]]!!.get()
            sumStatCount.add(sum)
            prop.add(counts[i] / sum)
        }
        return dataAfterStat.builder()
            .putNumeric(Stats.SUM, sumStatCount)
            .putNumeric(Stats.PROP, prop)
            .build()
    }

    companion object {
        private fun <T> computeCount(
            data: List<T?>,
            weight: List<Double?>
        ): Map<T, MutableDouble> {

            fun isFinite(v: T?) = when (v) {
                is Double -> SeriesUtil.isFinite(v)
                //is DoubleVector -> SeriesUtil.allFinite(v.x, v.y)
                is Pair<*, *> -> SeriesUtil.allFinite(v.first as Double?, v.second as Double?)
                else -> v != null
            }

            val result = LinkedHashMap<T, MutableDouble>()
            for (i in data.indices) {
                val key = data[i]
                if (isFinite(key)) {
                    if (!result.containsKey(key!!)) {
                        result[key] = MutableDouble(0.0)
                    }
                    result[key]!!.getAndAdd(SeriesUtil.asFinite(weight[i], 0.0))
                }
            }
            return result
        }
    }
}