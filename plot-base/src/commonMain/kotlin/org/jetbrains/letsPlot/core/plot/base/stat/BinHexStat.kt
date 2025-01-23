/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.ensureApplicableRange
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.isBeyondPrecision
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class BinHexStat(
    binCountX: Int = DEF_BINS,
    binCountY: Int = DEF_BINS,
    binWidthX: Double? = DEF_BINWIDTH,
    binWidthY: Double? = DEF_BINWIDTH,
    private val drop: Boolean = DEF_DROP
) : BaseStat(DEF_MAPPING) {
    private val binOptionsX = BinStatUtil.BinOptions(binCountX, binWidthX)
    private val binOptionsY = BinStatUtil.BinOptions(binCountY, binWidthY)

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X, Aes.Y)) {
            return withEmptyStatValues()
        }

        val xRange = statCtx.overallXRange()
        val yRange = statCtx.overallYRange()
        if (xRange == null || yRange == null) {
            return withEmptyStatValues()
        }

        // initial bin width and count

        val xRangeInit = adjustRangeInitial(xRange)
        val yRangeInit = adjustRangeInitial(yRange)

        val xCountAndWidthInit = BinStatUtil.binCountAndWidth(xRangeInit.length, binOptionsX)
        val yCountAndWidthInit = BinStatUtil.binCountAndWidth(yRangeInit.length, binOptionsY)

        // final bin width and count

        val xRangeFinal = adjustRangeFinal(xRange, xCountAndWidthInit.width)
        val yRangeFinal = adjustRangeFinal(yRange, yCountAndWidthInit.width)

        val xCountAndWidthFinal = BinStatUtil.binCountAndWidth(xRangeFinal.length, binOptionsX)
        val yCountAndWidthFinal = BinStatUtil.binCountAndWidth(yRangeFinal.length, binOptionsY)

        val countTotal = xCountAndWidthFinal.count * yCountAndWidthFinal.count
        val densityNormalizingFactor =
            densityNormalizingFactor(xRangeFinal.length, yRangeFinal.length, countTotal)

        val binsData = computeBins(
            data.getNumeric(TransformVar.X),
            data.getNumeric(TransformVar.Y),
            xRangeFinal.lowerEnd,
            yRangeFinal.lowerEnd,
            xCountAndWidthFinal.count,
            yCountAndWidthFinal.count,
            xCountAndWidthFinal.width,
            yCountAndWidthFinal.width,
            BinStatUtil.weightAtIndex(data),
            densityNormalizingFactor
        )

        return DataFrame.Builder()
            .putNumeric(Stats.X, binsData.x)
            .putNumeric(Stats.Y, binsData.y)
            .putNumeric(Stats.COUNT, binsData.count)
            .putNumeric(Stats.DENSITY, binsData.density)
            .build()
    }

    private fun computeBins(
        xValues: List<Double?>,
        yValues: List<Double?>,
        xStart: Double,
        yStart: Double,
        binCountX: Int,
        binCountY: Int,
        binWidth: Double,
        binHeight: Double,
        weightAtIndex: (Int) -> Double,
        densityNormalizingFactor: Double
    ): BinsHexData {
        val countByBinIndexKey = computeCounts(xValues, yValues, xStart, yStart, binWidth, binHeight, weightAtIndex)
        val totalCount = countByBinIndexKey.values.sum()

        val xs = ArrayList<Double>()
        val ys = ArrayList<Double>()
        val counts = ArrayList<Double>()
        val densities = ArrayList<Double>()

        val x0 = xStart + binWidth / 2.0
        val y0 = yStart + binHeight / 2.0
        for (xIndex in 0 until binCountX) {
            for (yIndex in 0 until binCountY) {
                val binIndexKey = Pair(xIndex, yIndex)
                var count = 0.0
                if (countByBinIndexKey.containsKey(binIndexKey)) {
                    count = countByBinIndexKey[binIndexKey]!!
                }

                if (drop && count == 0.0) {
                    continue
                }

                if (yIndex % 2 == 0) {
                    xs.add(x0 + xIndex * binWidth)
                } else {
                    xs.add(x0 + xIndex * binWidth + binWidth / 2.0)
                }
                ys.add(y0 + yIndex * binHeight)
                counts.add(count)
                val density = count / totalCount * densityNormalizingFactor
                densities.add(density)
            }
        }

        return BinsHexData(xs, ys, counts, densities)
    }

    private fun computeCounts(
        xValues: List<Double?>,
        yValues: List<Double?>,
        xStart: Double,
        yStart: Double,
        binWidth: Double,
        binHeight: Double,
        weightAtIndex: (Int) -> Double,
    ): Map<Pair<Int, Int>, Double> {
        fun getRawGridIndex(
            p: DoubleVector
        ): Pair<Int, Int> {
            val j = floor((p.y - yStart) / binHeight).toInt()
            val hexXStart = xStart + (j % 2) * binWidth / 2.0
            val i = floor((p.x - hexXStart) / binWidth).toInt()
            return Pair(i, j)
        }

        fun hexWithNeighbours(
            hexagonIndex: Pair<Int, Int>
        ): Set<Pair<Int, Int>> {
            val hexIds = if (hexagonIndex.second % 2 == 0)
                listOf(
                    hexagonIndex,
                    Pair(hexagonIndex.first, hexagonIndex.second + 1),
                    Pair(hexagonIndex.first + 1, hexagonIndex.second),
                    Pair(hexagonIndex.first, hexagonIndex.second - 1),
                    Pair(hexagonIndex.first - 1, hexagonIndex.second - 1),
                    Pair(hexagonIndex.first - 1, hexagonIndex.second),
                    Pair(hexagonIndex.first - 1, hexagonIndex.second + 1),
                )
            else
                listOf(
                    hexagonIndex,
                    Pair(hexagonIndex.first + 1, hexagonIndex.second + 1),
                    Pair(hexagonIndex.first + 1, hexagonIndex.second),
                    Pair(hexagonIndex.first + 1, hexagonIndex.second - 1),
                    Pair(hexagonIndex.first, hexagonIndex.second - 1),
                    Pair(hexagonIndex.first - 1, hexagonIndex.second),
                    Pair(hexagonIndex.first, hexagonIndex.second + 1),
                )
            return hexIds.filter { p -> p.first >= 0 && p.second >= 0 }.toSet()
        }

        fun isPointInHexagon(
            p: DoubleVector,
            hexagonIndex: Pair<Int, Int>
        ): Boolean {
            val halfHexHeight = 2.0 * binHeight / 3.0
            val center = DoubleVector(
                xStart + binWidth / 2.0 + if (hexagonIndex.second % 2 == 0)
                    hexagonIndex.first * binWidth
                else
                    hexagonIndex.first * binWidth + binWidth / 2.0,
                yStart + binHeight / 2.0 + hexagonIndex.second * binHeight
            )
            val q = p.subtract(center)
            val v1 = DoubleVector(0.0, halfHexHeight)
            val v2 = DoubleVector(binWidth / 2.0, halfHexHeight / 2.0)
            val v3 = DoubleVector(binWidth / 2.0, -halfHexHeight / 2.0)
            val v4 = DoubleVector(0.0, -halfHexHeight)
            val v5 = DoubleVector(-binWidth / 2.0, -halfHexHeight / 2.0)
            val v6 = DoubleVector(-binWidth / 2.0, halfHexHeight / 2.0)
            // line(v1, v2):
            val slope12 = (v2.y - v1.y) / (v2.x - v1.x)
            val intercept12 = v1.y - slope12 * v1.x
            val in12 = q.y < slope12 * q.x + intercept12
            // line(v2, v3):
            val in23 = q.x < binWidth / 2.0
            // line(v3, v4):
            val slope34 = (v4.y - v3.y) / (v4.x - v3.x)
            val intercept34 = v3.y - slope34 * v3.x
            val in34 = q.y >= slope34 * q.x + intercept34
            // line(v4, v5):
            val slope45 = (v5.y - v4.y) / (v5.x - v4.x)
            val intercept45 = v4.y - slope45 * v4.x
            val in45 = q.y >= slope45 * q.x + intercept45
            // line(v5, v6):
            val in56 = q.x >= -binWidth / 2.0
            // line(v6, v1):
            val slope61 = (v1.y - v6.y) / (v1.x - v6.x)
            val intercept61 = v6.y - slope61 * v6.x
            val in61 = q.y < slope61 * q.x + intercept61
            return in12 && in23 && in34 && in45 && in56 && in61
        }

        fun distanceToHexCenter(
            p: DoubleVector,
            hexagonIndex: Pair<Int, Int>
        ): Double {
            val x = xStart + binWidth / 2.0 + if (hexagonIndex.second % 2 == 0)
                hexagonIndex.first * binWidth
            else
                hexagonIndex.first * binWidth + binWidth / 2.0
            val y = yStart + binHeight / 2.0 + hexagonIndex.second * binHeight
            return sqrt((x - p.x).pow(2) + (y - p.y).pow(2))
        }

        val countByBinIndexKey = HashMap<Pair<Int, Int>, Double>()
        for (dataIndex in xValues.indices) {
            val x = xValues[dataIndex]
            val y = yValues[dataIndex]
            if (!SeriesUtil.allFinite(x, y)) {
                continue
            }
            val suspectedHexagons = hexWithNeighbours(getRawGridIndex(DoubleVector(x!!, y!!)))
            val hexIndexKey = suspectedHexagons.sortedWith(compareBy({
                    distanceToHexCenter(DoubleVector(x, y), it) },
                    { -it.second }, // To prefer hexagons with bigger y
                    { -it.first } // To prefer hexagons with bigger x
                )).first()
            if (!countByBinIndexKey.containsKey(hexIndexKey)) {
                countByBinIndexKey[hexIndexKey] = 0.0
            }
            countByBinIndexKey[hexIndexKey] = countByBinIndexKey.getValue(hexIndexKey) + weightAtIndex(dataIndex)
        }
        return countByBinIndexKey
    }

    class BinsHexData(
        internal val x: List<Double>,
        internal val y: List<Double>,
        internal val count: List<Double>,
        internal val density: List<Double>
    )

    companion object {
        const val DEF_BINS = 30
        val DEF_BINWIDTH: Double? = null
        const val DEF_DROP = true

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.FILL to Stats.COUNT
        )

        private fun adjustRangeInitial(r: DoubleSpan): DoubleSpan {
            // span can't be 0
            return ensureApplicableRange(r)
        }

        private fun adjustRangeFinal(r: DoubleSpan, binWidth: Double): DoubleSpan {
            return r.expanded(binWidth / 2.0).let {
                if (isBeyondPrecision(it)) {
                    // 0 span always becomes 1
                    it.expanded(0.5)
                } else {
                    it
                }
            }
        }

        private fun densityNormalizingFactor(
            xSpan: Double,
            ySpan: Double,
            count: Int
        ): Double {
            // density should integrate to 1.0
            val area = xSpan * ySpan
            val binArea = area / count
            return 1.0 / binArea
        }
    }
}