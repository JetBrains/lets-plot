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
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.component3

class PointDensityStat(
    bandWidthX: Double?,
    bandWidthY: Double?,
    bandWidthMethod: DensityStat.BandWidthMethod,
    adjust: Double,
    kernel: DensityStat.Kernel,
    nX: Int,
    nY: Int
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(
        data: DataFrame,
        statCtx: StatContext,
        messageConsumer: (String) -> Unit
    ): DataFrame {
        if (!hasRequiredValues(data, Aes.X, Aes.Y)) {
            return withEmptyStatValues()
        }

        val (xVector, yVector, groupWeight) = SeriesUtil.filterFinite(
            data.getNumeric(TransformVar.X),
            data.getNumeric(TransformVar.Y),
            BinStatUtil.weightVector(data.rowCount(), data)
        )

        if (xVector.isEmpty()) {
            return withEmptyStatValues()
        }

        val statData = buildStat(xVector, yVector, groupWeight)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    private fun buildStat(
        xs: List<Double>,
        ys: List<Double>,
        weights: List<Double>
    ): Map<DataFrame.Variable, List<Double>> {
        return mapOf(
            Stats.X to xs,
            Stats.Y to ys,
            Stats.DENSITY to countNeighbors(xs, ys, weights, 1.0, 1.0)
        )
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.COLOR to Stats.DENSITY
        )

        internal fun countNeighbors(xs: List<Double>, ys: List<Double>, weights: List<Double>, r2: Double, xy: Double): List<Double> {
            return xs.indices.map { i ->
                xs.indices.sumOf { j ->
                    if (i != j && scaledDistanceSquared(xs[i], ys[i], xs[j], ys[j], xy) < r2) {
                        weights[i]
                    } else {
                        0.0
                    }
                }
            }
        }

        private fun scaledDistanceSquared(x1: Double, y1: Double, x2: Double, y2: Double, xy: Double): Double {
            return (x1 - x2) * (x1 - x2) / xy + (y1 - y2) * (y1 - y2) * xy
        }
    }
}