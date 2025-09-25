/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

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
    defaultMappings = DEF_MAPPING,
    bandWidthX = bandWidthX,
    bandWidthY = bandWidthY,
    bandWidthMethod = bandWidthMethod,
    adjust = adjust,
    kernel = kernel,
    nX = nX,
    nY = nY,
    isContour = false,
    binCount = 0,
    binWidth = 0.0
) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
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
        val weights = BinStatUtil.weightVector(data.rowCount(), data)
        val indices = indicesOfFinite(xs, ys, weights)
        val xVector = indices.map { xs[it]!! }
        val yVector = indices.map { ys[it]!! }
        val weightVector = indices.map { weights[it]!! }

        val statData = buildStat(xVector, yVector, weightVector, indices, xRange, yRange)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            @Suppress("UNCHECKED_CAST")
            when (variable) {
                Stats.INDEX -> builder.put(variable, series)
                else -> builder.putNumeric(variable, series as List<Double>)
            }
        }
        return builder.build()
    }

    private fun buildStat(
        xVector: List<Double>,
        yVector: List<Double>,
        weightVector: List<Double>,
        indices: List<Int>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Number>> {
        return when (method) {
            Method.NEIGHBOURS -> buildNeighboursStat(xVector, yVector, weightVector, indices, xRange, yRange)
            Method.KDE2D -> buildKde2dStat(xVector, yVector, weightVector, indices, xRange, yRange)
        }
    }

    private fun buildNeighboursStat(
        xVector: List<Double>,
        yVector: List<Double>,
        weightVector: List<Double>,
        indices: List<Int>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Number>> {
        val adjustedXRange = xRange.length * adjust
        val adjustedYRange = yRange.length * adjust
        val r2 = (adjustedXRange + adjustedYRange) / 70.0
        val xy = adjustedXRange / adjustedYRange
        val statCount = countNeighbours(xVector, yVector, weightVector, r2, xy)
        val statDensity = statCount.map { it / statCount.size }
        val maxCount = statCount.maxOrNull() ?: 0.0
        val statScaled = statCount.map { it / maxCount }
        return mapOf(
            Stats.X to xVector,
            Stats.Y to yVector,
            Stats.COUNT to statCount,
            Stats.DENSITY to statDensity,
            Stats.SCALED to statScaled,
            Stats.INDEX to indices
        )
    }

    private fun buildKde2dStat(
        xValues: List<Double>,
        yValues: List<Double>,
        weightVector: List<Double>,
        indices: List<Int>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Number>> {
        val statCount = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statScaled = ArrayList<Double>()

        val (stepsX, stepsY, densityMatrix) = density2dGrid(xValues, yValues, weightVector, xRange, yRange)

        val multiIndex = findInterval(yValues, stepsY) zip findInterval(xValues, stepsX)
        val weightsSum = SeriesUtil.sum(weightVector)
        for ((row, col) in multiIndex) {
            val count = densityMatrix.getEntry(row, col)
            statCount.add(count)
            statDensity.add(count / weightsSum)
        }

        statCount.maxOrNull()?.let { maxCount ->
            for (d in statCount) {
                statScaled.add(d / maxCount)
            }
        }

        return mapOf(
            Stats.X to xValues,
            Stats.Y to yValues,
            Stats.COUNT to statCount,
            Stats.DENSITY to statDensity,
            Stats.SCALED to statScaled,
            Stats.INDEX to indices
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
        val DEF_BW = DensityStat.BandWidthMethod.NRD
        const val DEF_ADJUST = 1.0
        val DEF_KERNEL = DensityStat.Kernel.GAUSSIAN
        const val DEF_N = 100
        val DEF_METHOD = Method.NEIGHBOURS

        // It's assumed that breaks are sorted and contain at least one value
        internal fun findInterval(value: Double, breaks: List<Double>): Int {
            if (value < breaks.first()) return 0
            if (value >= breaks.last()) return breaks.size - 1

            var lower = 0
            var upper = breaks.size - 1
            while (lower < upper) {
                val mid = (lower + upper) / 2
                if (value >= breaks[mid]) {
                    lower = mid + 1
                } else {
                    upper = mid
                }
            }

            return lower
        }

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.COLOR to Stats.DENSITY
        )

        private fun countNeighbours(
            x: List<Double>,
            y: List<Double>,
            w: List<Double>,
            r2: Double,
            xy: Double
        ): List<Double> {
            val counts: MutableList<Double> = mutableListOf()
            val n = x.size
            for (i in 0 until n) {
                var count = 0.0
                for (j in 0 until n) {
                    if (i == j || scaledDistanceSquared(x[i], y[i], x[j], y[j], xy) < r2) {
                        count += w[i]
                    }
                }
                counts.add(count)
            }
            return counts
        }

        private fun scaledDistanceSquared(x1: Double, y1: Double, x2: Double, y2: Double, xy: Double): Double {
            return (x1 - x2) * (x1 - x2) / xy + (y1 - y2) * (y1 - y2) * xy
        }

        private fun findInterval(values: List<Double>, breaks: List<Double>): List<Int> {
            return values.map { findInterval(it, breaks) }
        }

        private fun indicesOfFinite(l0: List<Double?>, l1: List<Double?>, l2: List<Double?>): List<Int> {
            return l0.mapIndexedNotNull { i, v0 ->
                if (SeriesUtil.allFinite(v0, l1[i], l2[i])) i else null
            }
        }
    }
}