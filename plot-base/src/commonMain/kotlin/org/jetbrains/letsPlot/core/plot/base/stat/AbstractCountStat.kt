/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.intern.filterNotNullKeys
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

abstract class AbstractCountStat(
    defaultMappings: Map<Aes<*>, DataFrame.Variable>,
    private val count2d: Boolean,
    private val local: Boolean
) : BaseStat(defaultMappings) {

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        fun getPositional(data:DataFrame, variable: DataFrame.Variable) = when (data.has(variable)) {
            true -> data.getNumeric(variable).map { it.takeIf(SeriesUtil::isFinite) }
            false -> List(data.rowCount()) { 0.0 }
        }

        val weights = BinStatUtil.weightVector(data.rowCount(), data)
        val locations = if (count2d) {
            val xs = getPositional(data, TransformVar.X)
            val ys = getPositional(data, TransformVar.Y)
            xs.zip(ys).map { it.takeIf { (x, y) -> x != null && y != null } }
        } else {
            getPositional(data, TransformVar.X)
        }

        val summary = groupAndSum(locations, weights)

        val statDf = DataFrame.Builder()

        if (count2d) {
            @Suppress("UNCHECKED_CAST")
            val xys = summary.keys as Collection<Pair<*, *>>
            statDf.putNumeric(Stats.X, xys.map { (x, _) -> x as Double })
            statDf.putNumeric(Stats.Y, xys.map { (_, y) -> y as Double })
        } else {
            val xs = summary.keys
            statDf.putNumeric(Stats.X, xs.map { it as Double })
        }

        if (local) {
            statDf.putNumeric(Stats.COUNT, summary.values.toList())
        } else {
            val totalWeight = summary.values.sum()
            val prop = summary.values.map { it / totalWeight }
            val propPercent = prop.map { it * 100 }

            statDf.putNumeric(Stats.PROP, prop)
            statDf.putNumeric(Stats.PROPPCT, propPercent)
            statDf.putNumeric(Stats.N, summary.values.toList())
        }

        return statDf.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        if (!local) {
            return dataAfterStat
        }

        val locations = if (count2d) {
            val xs = dataAfterStat[Stats.X]
            val ys = dataAfterStat[Stats.Y]
            xs.zip(ys)
        } else {
            dataAfterStat[Stats.X].map { it!! }
        }

        // weights are group-wide (were computed within independent groups)
        val weights = dataAfterStat.getNumeric(Stats.COUNT).map { it!! }
        // compute total location weights on the whole data
        val summary = groupAndSum(locations, weights)
        val totalWeights = locations.map { summary[it]!! }
        val totalWeightsSum = summary.values.sum()

        val prop = weights.zip(totalWeights).map { (groupWeight, totalWeight) -> groupWeight / totalWeight }
        val propPercent = prop.map { it * 100 }
        val sumProp = totalWeights.map { it / totalWeightsSum }
        val sumPropPercent = sumProp.map { it * 100 }

        val statDf = dataAfterStat.builder()
        statDf.putNumeric(Stats.SUM, totalWeights)
        statDf.putNumeric(Stats.PROP, prop)
        statDf.putNumeric(Stats.PROPPCT, propPercent)
        statDf.putNumeric(Stats.SUMPROP, sumProp)
        statDf.putNumeric(Stats.SUMPCT, sumPropPercent)
        return statDf.build()
    }

    companion object {
        private fun groupAndSum(groups: List<Any?>, values: List<Double?>): Map<Any, Double> {
            return groups.zip(values)
                .groupBy { (g, _) -> g }
                .filterNotNullKeys()
                .mapValues { (_, groupValues) -> groupValues.sumOf { (_, v) -> SeriesUtil.finiteOrNull(v) ?: 0.0 } }
        }
    }
}