/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.mutables.MutableDouble
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

abstract class AbstractCountStat(
    defaultMappings: Map<Aes<*>, DataFrame.Variable>,
    private val count2d: Boolean,
    private val local: Boolean
) : BaseStat(defaultMappings) {

    protected abstract fun toStatPositionVars(values: Set<Any>): Map<DataFrame.Variable, List<Double>>

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
        val countData = computeCount(aggrBy, weight)
        val positionVars = toStatPositionVars(countData.keys)

        val statDf = DataFrame.Builder()
        if (count2d) {
            statDf.putNumeric(Stats.X, positionVars[Stats.X]!!)
            statDf.putNumeric(Stats.Y, positionVars[Stats.Y]!!)
        } else {
            statDf.putNumeric(Stats.X, positionVars[Stats.X]!!)
        }

        statDf.putNumeric(Stats.COUNT, countData.values.map(MutableDouble::get))

        if (!local) {
            val prop = countData.values.map { it.get() / data.rowCount() }
            val propPercent = prop.map { it * 100 }

            statDf.putNumeric(Stats.PROP, prop)
            statDf.putNumeric(Stats.PROPPCT, propPercent)
        }

        return statDf.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val statDf = dataAfterStat.builder()

        if (local) {
            val aggrBy = if (count2d) {
                val xs = dataAfterStat[Stats.X]
                val ys = dataAfterStat[Stats.Y]
                xs.zip(ys)
            } else {
                dataAfterStat[Stats.X]
            }.map { it!! }

            val counts = dataAfterStat.getNumeric(Stats.COUNT).map { it!! }
            val computedCount = computeCount(aggrBy, counts)
            val sums = aggrBy.map { computedCount[it]!!.get() }

            val prop = counts.zip(sums).map { (count, sum) -> count / sum }
            val propPercent = prop.map { it * 100 }

            statDf.putNumeric(Stats.SUM, sums)
            statDf.putNumeric(Stats.PROP, prop)
            statDf.putNumeric(Stats.PROPPCT, propPercent)
        }

        return statDf.build()
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