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
    private val count2d: Boolean
) : BaseStat(defaultMappings) {

    protected abstract fun addToStatVars(values: Set<Any>): Map<DataFrame.Variable, List<Double>>

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        fun getNumerics(variable: DataFrame.Variable) = if (data.has(variable)) {
            data.getNumeric(variable).map {
                if (SeriesUtil.isFinite(it)) it else null
            }
        } else {
            List(data.rowCount()) { 0.0 }
        }

        val aggrBy = if (count2d) {
            val xs = getNumerics(TransformVar.X)
            val ys = getNumerics(TransformVar.Y)
            xs.zip(ys).map { (x, y) ->
                if (x == null || y == null) null
                else x to y
            }
        } else {
            getNumerics(TransformVar.X)
        }

        val weight = BinStatUtil.weightVector(data.rowCount(), data)

        val computedCount = computeCount(aggrBy, weight)

        val stat = addToStatVars(computedCount.keys).toMutableMap()
        stat[Stats.COUNT] = computedCount.values.map(MutableDouble::get)

        return DataFrame.Builder().apply {
            stat.forEach { (statVar, values) -> putNumeric(statVar, values) }
        }.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val aggrBy = if (count2d) {
            val xs = dataAfterStat[Stats.X]
            val ys = dataAfterStat[Stats.Y]
            xs.zip(ys)
        } else {
            dataAfterStat[Stats.X]
        }.map { it!! }

        val counts = dataAfterStat.getNumeric(Stats.COUNT).map { it!! }

        val computedCount = computeCount(aggrBy, counts)

        val sumStatCount = ArrayList<Double>()
        val prop = ArrayList<Double>()
        val propPercent = ArrayList<Double>()
        for (i in counts.indices) {
            val sum = computedCount.getValue(aggrBy[i]).get()
            sumStatCount.add(sum)
            prop.add(counts[i] / sum)
            propPercent.add(counts[i] * 100 / sum)
        }
        return dataAfterStat.builder()
            .putNumeric(Stats.SUM, sumStatCount)
            .putNumeric(Stats.PROP, prop)
            .putNumeric(Stats.PROPPCT, propPercent)
            .build()
    }

    companion object {
        private fun computeCount(
            aggrBy: List<Any?>,
            weight: List<Double?>
        ): Map<Any, MutableDouble> {
            val result = LinkedHashMap<Any, MutableDouble>()
            for (i in aggrBy.indices) {
                val key = aggrBy[i]
                if (key != null) {
                    if (!result.containsKey(key)) {
                        result[key] = MutableDouble(0.0)
                    }
                    result.getValue(key).getAndAdd(SeriesUtil.asFinite(weight[i], 0.0))
                }
            }
            return result
        }
    }
}