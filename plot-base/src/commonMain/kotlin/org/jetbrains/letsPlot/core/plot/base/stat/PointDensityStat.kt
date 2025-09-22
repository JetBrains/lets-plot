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
    adjust: Double,
    private val method: Method
) : AbstractDensity2dStat(
    defaultMappings = DEF_MAPPING,
    bandWidthX = null, // TODO
    bandWidthY = null, // TODO
    bandWidthMethod = DensityStat.BandWidthMethod.NRD, // TODO
    adjust = adjust,
    kernel = DensityStat.Kernel.GAUSSIAN, // TODO
    nX = DEF_N, // TODO
    nY = DEF_N, // TODO
    isContour = false,
    binCount = DEF_BIN_COUNT,
    binWidth = DEF_BIN_WIDTH
) {

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
        val (xValues, yValues) = SeriesUtil.filterFinite(xs, ys)

        val statData = buildStat(xValues, yValues, xRange, yRange)

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    private fun buildStat(
        xValues: List<Double>,
        yValues: List<Double>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Double>> {
        return when (method) {
            Method.NEIGHBOURS -> buildNeighboursStat(xValues, yValues, xRange, yRange)
            Method.KDE2D -> buildKde2dStat(xValues, yValues, xRange, yRange)
        }
    }

    private fun buildNeighboursStat(
        xValues: List<Double>,
        yValues: List<Double>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Double>> {
        val adjustedXRange = xRange.length * adjust
        val adjustedYRange = yRange.length * adjust
        val r2 = (adjustedXRange + adjustedYRange) / 70.0
        val xy = adjustedXRange / adjustedYRange
        val statCount = countNeighbours(xValues, yValues, r2, xy).map { it.toDouble() }
        val statDensity = statCount.map { it / statCount.size } // Never divide by zero - no mapping if no points
        val maxCount = statCount.maxOrNull() ?: 0.0 // null only if there are no points (each point counts itself)
        val statScaled = statCount.map { it / maxCount } // Never divide by zero - no mapping if no points
        return mapOf(
            Stats.X to xValues,
            Stats.Y to yValues,
            Stats.COUNT to statCount,
            Stats.DENSITY to statDensity,
            Stats.SCALED to statScaled
        )
    }

    private fun buildKde2dStat(
        xValues: List<Double>,
        yValues: List<Double>,
        xRange: DoubleSpan,
        yRange: DoubleSpan
    ): Map<DataFrame.Variable, List<Double>> {
        val weights = List(xValues.size) { 1.0 } // TODO

        val statCount = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statScaled = ArrayList<Double>()

        val (stepsX, stepsY, densityMatrix) = density2dGrid(xValues, yValues, weights, xRange, yRange)

        val indices = findInterval(yValues, stepsY) zip findInterval(xValues, stepsX)
        val weightsSum = SeriesUtil.sum(weights)
        for ((row, col) in indices) {
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
            Stats.SCALED to statScaled
        )
    }

    enum class Method {
        NEIGHBOURS, KDE2D;

        companion object {

            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Method>()

            fun safeValueOf(v: String): Method {
                return ENUM_INFO.safeValueOf(v) ?:
                throw IllegalArgumentException(
                    "Unsupported method: '$v'\n" +
                    "Use one of: neighbours, kde2d."
                )
            }
        }
    }

    companion object {
        const val DEF_ADJUST = 1.0
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

        private fun findInterval(values: List<Double>, breaks: List<Double>): List<Int> {
            return values.map { findInterval(it, breaks) }
        }
    }
}