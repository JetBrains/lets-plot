/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class PointDensityStat : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    override fun apply(
        data: DataFrame,
        statCtx: StatContext,
        messageConsumer: (String) -> Unit
    ): DataFrame {
        if (!hasRequiredValues(data, Aes.X) || !hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val xRange = statCtx.overallXRange() ?: return withEmptyStatValues()
        val yRange = statCtx.overallYRange() ?: return withEmptyStatValues()

        val xs = data.getNumeric(TransformVar.X)
        val ys = data.getNumeric(TransformVar.Y)
        val r2 = (xRange.length + yRange.length) / 70.0
        val xy = xRange.length / yRange.length

        val statData = buildStat(xs, ys, r2, xy)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    private fun buildStat(
        xs: List<Double?>,
        ys: List<Double?>,
        r2: Double,
        xy: Double
    ): Map<DataFrame.Variable, List<Double>> {
        val (statX, statY) = SeriesUtil.filterFinite(xs, ys)
        val statCount = countNeighbours(statX, statY, r2, xy).map { it.toDouble() }
        val statDensity = statCount.map { it / statCount.size } // Never divide by zero - no mapping if no points
        val maxCount = statCount.maxOrNull() ?: 0.0 // null only if there are no points (each point counts itself)
        val statScaled = statCount.map { it / maxCount } // Never divide by zero - no mapping if no points
        return mapOf(
            Stats.X to statX,
            Stats.Y to statY,
            Stats.COUNT to statCount,
            Stats.DENSITY to statDensity,
            Stats.SCALED to statScaled
        )
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.COLOR to Stats.DENSITY
        )

        private fun countNeighbours(
            x: List<Double>,
            y: List<Double>,
            r2: Double,
            xy: Double
        ): List<Int> {
            val counts: MutableList<Int> = mutableListOf()
            val n = x.size
            for (i in 0 until n) {
                var count = 0
                for (j in 0 until n) {
                    if (i == j || scaledDistanceSquared(x[i], y[i], x[j], y[j], xy) < r2) {
                        count += 1
                    }
                }
                counts.add(count)
            }
            return counts
        }

        private fun scaledDistanceSquared(x1: Double, y1: Double, x2: Double, y2: Double, xy: Double): Double {
            return (x1 - x2) * (x1 - x2) / xy + (y1 - y2) * (y1 - y2) * xy
        }
    }
}