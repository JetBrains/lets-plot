/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory
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
    nY: Int,
    private val method: Method
) : AbstractDensity2dStat(
    bandWidthX = bandWidthX,
    bandWidthY = bandWidthY,
    bandWidthMethod = bandWidthMethod,
    adjust = adjust,
    kernel = kernel,
    nX = nX,
    nY = nY,
    isContour = false,
    binCount = 0,
    binWidth = 0.0,
    defaultMappings = DEF_MAPPING
) {

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

        val xRange = statCtx.overallXRange() ?: return withEmptyStatValues()
        val yRange = statCtx.overallYRange() ?: return withEmptyStatValues()
        val xy = xRange.length / yRange.length
        val rX = xRange.length / 6.0
        val r2 = rX * rX / xy
        val statData = buildStat(xVector, yVector, groupWeight, r2, xy)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    private fun buildStat(
        xs: List<Double>,
        ys: List<Double>,
        weights: List<Double>,
        r2: Double,
        xy: Double
    ): Map<DataFrame.Variable, List<Double>> {
        return when (method) {
            Method.NEIGHBOURS -> buildNeighboursStat(xs, ys, weights, r2, xy)
            Method.KDE2D -> buildKde2dStat(xs, ys, weights, r2, xy)
        }
    }

    private fun buildNeighboursStat(
        xs: List<Double>,
        ys: List<Double>,
        weights: List<Double>,
        r2: Double,
        xy: Double
    ): Map<DataFrame.Variable, List<Double>> {
        val statCount = countNeighbors(xs, ys, weights, r2, xy)
        val statDensity = statCount.map { it / statCount.size }
        val maxCount = statCount.maxOrNull() ?: 0.0
        val statScaled = statCount.map { it / maxCount }
        return mapOf(
            Stats.X to xs,
            Stats.Y to ys,
            Stats.COUNT to statCount,
            Stats.DENSITY to statDensity,
            Stats.SCALED to statScaled
        )
    }

    private fun buildKde2dStat(
        xs: List<Double>,
        ys: List<Double>,
        weights: List<Double>,
        r2: Double,
        xy: Double
    ): Map<DataFrame.Variable, List<Double>> {
        return mapOf(
            Stats.X to xs,
            Stats.Y to ys,
            Stats.COUNT to List(xs.size) { 0.0 },
            Stats.DENSITY to List(xs.size) { 0.0 },
            Stats.SCALED to List(xs.size) { 0.0 }
        )
    }

    enum class Method {
        NEIGHBOURS, KDE2D;

        companion object {

            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Method>()

            fun safeValueOf(v: String): Method {
                val methodName = when (v.lowercase()) {
                    "neighbors" -> "neighbours" // Support American spelling
                    else -> v
                }
                return ENUM_INFO.safeValueOf(methodName) ?:
                    throw IllegalArgumentException(
                        "Unsupported method: '$v'\n" +
                        "Use one of: neighbours, kde2d."
                    )
            }
        }
    }

    companion object {
        val DEF_METHOD: Method = Method.NEIGHBOURS

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