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
import org.jetbrains.letsPlot.core.plot.base.stat.math3.BlockRealMatrix
import kotlin.collections.component1
import kotlin.collections.component2

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

        val xs = data.getNumeric(TransformVar.X)
        val ys = data.getNumeric(TransformVar.Y)
        val weights = BinStatUtil.weightVector(data.rowCount(), data)
        val indices = indicesOfFinite(xs, ys, weights)
        if (indices.isEmpty()) {
            return withEmptyStatValues()
        }
        val xVector = indices.map { xs[it]!! }
        val yVector = indices.map { ys[it]!! }
        val groupWeight = indices.map { weights[it]!! }

        val xRange = statCtx.overallXRange() ?: return withEmptyStatValues()
        val yRange = statCtx.overallYRange() ?: return withEmptyStatValues()
        val statData = buildStat(xVector, yVector, groupWeight, xRange, yRange)

        val builder = DataFrame.Builder()
            .putNumeric(Stats.X, xVector)
            .putNumeric(Stats.Y, yVector)
            .put(Stats.INDEX, indices)
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    private fun buildStat(
        xs: List<Double>,
        ys: List<Double>,
        weights: List<Double>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Double>> {
        return when (method) {
            Method.NEIGHBOURS -> buildNeighboursStat(xs, ys, weights, xRange, yRange)
            Method.KDE2D -> buildKde2dStat(xs, ys, weights, xRange, yRange)
        }
    }

    private fun buildNeighboursStat(
        xs: List<Double>,
        ys: List<Double>,
        weights: List<Double>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Double>> {
        val xy = xRange.length / yRange.length
        val rX = xRange.length / 12.0 // For standard bivariate normal distribution and ~1000 points, the rX is about 0.5
        val r2 = adjust * rX * rX / xy
        val statCount = countNeighbors(xs, ys, weights, r2, xy)
        val statDensity = statCount.map { it / statCount.size }
        val maxCount = statCount.maxOrNull() ?: 0.0
        val statScaled = statCount.map { it / maxCount }
        return mapOf(
            Stats.COUNT to statCount,
            Stats.DENSITY to statDensity,
            Stats.SCALED to statScaled
        )
    }

    private fun buildKde2dStat(
        xs: List<Double>,
        ys: List<Double>,
        weights: List<Double>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Double>> {
        val (stepsX, stepsY, densityMatrix) = density2dGrid(xs, ys, weights, xRange, yRange)
        val statCount = ArrayList<Double>()
        xs.forEachIndexed { i, x ->
            statCount.add(approxCount(x, ys[i], stepsX, stepsY, densityMatrix))
        }
        val totalWeights = SeriesUtil.sum(weights)
        val maxCount = statCount.maxOrNull() ?: 0.0
        return mapOf(
            Stats.COUNT to statCount,
            Stats.DENSITY to statCount.map { it / totalWeights },
            Stats.SCALED to statCount.map { it / maxCount }
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

        /**
         * Approximate count from density matrix.
         * Find rectangle in which (x, y) is located and return value of the closest corner.
         */
        internal fun approxCount(
            x: Double,
            y: Double,
            stepsX: List<Double>,
            stepsY: List<Double>,
            densityMatrix: BlockRealMatrix
        ): Double {
            val (colLow, colHigh) = binarySearchOfLimits(x, stepsX)
            val (rowLow, rowHigh) = binarySearchOfLimits(y, stepsY)
            if (colLow == colHigh && rowLow == rowHigh) {
                return densityMatrix.getEntry(rowLow, colLow)
            }
            val alphaRow = if (rowLow == rowHigh) 0.0 else (y - stepsY[rowLow]) / (stepsY[rowHigh] - stepsY[rowLow])
            val alphaCol = if (colLow == colHigh) 0.0 else (x - stepsX[colLow]) / (stepsX[colHigh] - stepsX[colLow])
            return when {
                alphaRow < 0.5 && alphaCol < 0.5 -> densityMatrix.getEntry(rowLow, colLow)
                alphaRow < 0.5 && alphaCol >= 0.5 -> densityMatrix.getEntry(rowLow, colHigh)
                alphaRow >= 0.5 && alphaCol < 0.5 -> densityMatrix.getEntry(rowHigh, colLow)
                else -> densityMatrix.getEntry(rowHigh, colHigh)
            }
        }

        private fun indicesOfFinite(l0: List<Double?>, l1: List<Double?>, l2: List<Double?>): List<Int> {
            return l0.mapIndexedNotNull { i, v0 ->
                if (SeriesUtil.allFinite(v0, l1[i], l2[i])) i else null
            }
        }

        private fun scaledDistanceSquared(x1: Double, y1: Double, x2: Double, y2: Double, xy: Double): Double {
            return (x1 - x2) * (x1 - x2) / xy + (y1 - y2) * (y1 - y2) * xy
        }

        private fun binarySearchOfLimits(v: Double, steps: List<Double>): Pair<Int, Int> {
            var low = 0
            var high = steps.size - 1
            if (v < steps[low]) {
                return Pair(low, low)
            }
            if (v > steps[high]) {
                return Pair(high, high)
            }
            while (low <= high) {
                val mid = (low + high) / 2
                val midVal = steps[mid]
                when {
                    midVal < v -> low = mid + 1
                    midVal > v -> high = mid - 1
                    else -> return Pair(mid, mid)
                }
            }
            return Pair(high, low)
        }
    }
}