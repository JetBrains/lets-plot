/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.intern.bracketingIndicesOrNull
import org.jetbrains.letsPlot.commons.intern.indicesOf
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.math3.BlockRealMatrix
import kotlin.Comparator
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.math.abs

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
        val finiteIndices = (xs zip ys).indicesOf { (x, y) -> SeriesUtil.allFinite(x, y) }

        val xVector = xs.slice(finiteIndices).requireNoNulls()
        val yVector = ys.slice(finiteIndices).requireNoNulls()
        val groupWeight = BinStatUtil.weightVector(data)
            .slice(finiteIndices)
            .map { SeriesUtil.finiteOrNull(it) ?: 0.0 }

        val xRange = statCtx.overallXRange() ?: return withEmptyStatValues()
        val yRange = statCtx.overallYRange() ?: return withEmptyStatValues()
        val statData = buildStat(xVector, yVector, groupWeight, xRange, yRange)

        val builder = DataFrame.Builder()
            .putNumeric(Stats.X, xVector)
            .putNumeric(Stats.Y, yVector)
            .put(Stats.INDEX, finiteIndices)
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
        val statCount = when {
            xRange.length > 0 && yRange.length > 0 -> {
                val xy = xRange.length / yRange.length
                val rX = RADIUS_FACTOR * xRange.length
                val r2 = adjust * rX * rX / xy
                countNeighbors(xs, ys, weights, r2, xy)
            }
            xRange.length > 0 && yRange.length == 0.0 -> {
                // Only x varies
                val rX = RADIUS_FACTOR * xRange.length
                val r2 = adjust * rX * rX
                countNeighbors(xs, ys, weights, r2, 1.0)
            }
            xRange.length == 0.0 && yRange.length > 0 -> {
                // Only y varies
                val rY = RADIUS_FACTOR * yRange.length
                val r2 = adjust * rY * rY
                countNeighbors(xs, ys, weights, r2, 1.0)
            }
            xRange.length == 0.0 && yRange.length == 0.0 -> {
                // All points are at the same position
                val weightsSum = SeriesUtil.sum(weights)
                weights.map {
                    if (adjust > 0) weightsSum - it else 0.0
                }
            }
            else -> error("Unexpected case: xRange = $xRange, yRange = $yRange") // should never happen
        }
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
        val xComparator = approxCountComparator(APPROX_COUNT_EPSILON * xRange.length)
        val yComparator = approxCountComparator(APPROX_COUNT_EPSILON * yRange.length)
        val statCount = if (xs.any()) {
            val (stepsX, stepsY, densityMatrix) = density2dGrid(xs, ys, weights, xRange, yRange)
            xs.mapIndexed { i, x ->
                approxCount(x, ys[i], stepsX, stepsY, densityMatrix, xComparator, yComparator)
            }
        } else {
            emptyList()
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

        private const val RADIUS_FACTOR = 1.0 / 12.0 // For standard bivariate normal distribution and ~1000 points, the rX will be about 0.5
        private const val APPROX_COUNT_EPSILON = 1e-12

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.COLOR to Stats.DENSITY
        )

        /**
         * Approximate count from density matrix.
         * Find rectangle in which (x, y) is located and return value of the closest corner.
         */
        internal fun approxCount(
            x: Double,
            y: Double,
            stepsX: List<Double>,
            stepsY: List<Double>,
            densityMatrix: BlockRealMatrix,
            xComparator: Comparator<Double>,
            yComparator: Comparator<Double>
        ): Double {
            val (colLow, colHigh) = stepsX.bracketingIndicesOrNull(x, xComparator)!!
            val (rowLow, rowHigh) = stepsY.bracketingIndicesOrNull(y, yComparator)!!
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

        internal fun countNeighbors(xs: List<Double>, ys: List<Double>, weights: List<Double>, r2: Double, xy: Double): List<Double> {
            val n = xs.size
            val xVector = xs.toDoubleArray()
            val yVector = ys.toDoubleArray()
            val weightVector = weights.toDoubleArray()
            val neighboursCounts = DoubleArray(n)
            for (i in 0 until n) {
                val xi = xVector[i]
                val yi = yVector[i]
                val wi = weightVector[i]
                for (j in i + 1 until n) {
                    if (scaledDistanceSquared(xi, yi, xVector[j], yVector[j], xy) < r2) {
                        neighboursCounts[i] = neighboursCounts[i] + weightVector[j]
                        neighboursCounts[j] = neighboursCounts[j] + wi
                    }
                }
            }
            return neighboursCounts.toList()
        }

        private fun scaledDistanceSquared(x1: Double, y1: Double, x2: Double, y2: Double, xy: Double): Double {
            return (x1 - x2) * (x1 - x2) / xy + (y1 - y2) * (y1 - y2) * xy
        }

        private fun approxCountComparator(epsilon: Double): Comparator<Double> =
            Comparator { a, b ->
                when {
                    abs(a - b) < epsilon -> 0
                    a < b -> -1
                    else -> 1
                }
            }
    }
}