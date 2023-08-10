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

        val locations = if (count2d) {
            val xs = getPositional(data, TransformVar.X)
            val ys = getPositional(data, TransformVar.Y)
            xs.zip(ys).map { it.takeIf { (x, y) -> x != null && y != null } }
        } else {
            getPositional(data, TransformVar.X)
        }

        val weights = BinStatUtil.weightVector(data.rowCount(), data)
        val localSums = sumCounts(locations, weights)

        val statDf = DataFrame.Builder()
        statDf.putNumeric(Stats.COUNT, localSums.values.toList())

        if (count2d) {
            @Suppress("UNCHECKED_CAST")
            val xys = localSums.keys as Collection<Pair<*, *>>
            statDf.putNumeric(Stats.X, xys.map { (x, _) -> x as Double })
            statDf.putNumeric(Stats.Y, xys.map { (_, y) -> y as Double })
        } else {
            val xs = localSums.keys
            statDf.putNumeric(Stats.X, xs.map { it as Double })
        }

        if (!local) {
            val prop = localSums.values.map { it / data.rowCount() }
            val propPercent = prop.map { it * 100 }

            statDf.putNumeric(Stats.PROP, prop)
            statDf.putNumeric(Stats.PROPPCT, propPercent)
        }

        return statDf.build()
    }

    override fun normalize(dataAfterStat: DataFrame): DataFrame {
        val locations = if (count2d) {
            val xs = dataAfterStat[Stats.X]
            val ys = dataAfterStat[Stats.Y]
            xs.zip(ys)
        } else {
            dataAfterStat[Stats.X].map { it!! }
        }

        val counts = dataAfterStat.getNumeric(Stats.COUNT).map { it!! }
        val localSums = sumCounts(locations, counts)
        val sums = locations.map { localSums[it]!! }

        val statDf = dataAfterStat.builder()
        if (local) {
            val prop = counts.zip(sums).map { (count, sum) -> count / sum }
            val propPercent = prop.map { it * 100 }

            statDf.putNumeric(Stats.SUM, sums)
            statDf.putNumeric(Stats.PROP, prop)
            statDf.putNumeric(Stats.PROPPCT, propPercent)
        } else {
            statDf.putNumeric(Stats.N, sums)
        }

        return statDf.build()
    }

    companion object {
        private fun sumCounts(locations: List<Any?>, counts: List<Double?>): Map<Any, Double> {
            return locations.zip(counts)
                .groupBy { (loc, _) -> loc }
                .filterNotNullKeys()
                .mapValues { (_, localCounts) -> localCounts.sumOf { (_, count) -> SeriesUtil.asFinite(count, 0.0) } }
        }
    }
}