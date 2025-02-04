/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.polygonContainsCoordinate
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.ensureApplicableRange
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.isBeyondPrecision
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.math.abs
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
    private val binOptionsY = BinStatUtil.BinOptions(binCountY, binWidthY?.let { it * HEIGHT_TO_BINHEIGHT })

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

        // Initial bin width and count

        val xRangeInit = adjustRangeInitial(xRange)
        val yRangeInit = adjustRangeInitial(yRange)

        /*
        Use the `extraExpand` parameter to cover not only the entire data range with bins, but also a reserve - so that
        half a hexagon protrudes from each side of the range, as required by the hexagonal coverage of a rectangle area.
        */
        val xCountAndWidthInit = BinStatUtil.binCountAndWidth(xRangeInit.length, binOptionsX, extraExpand = true)
        val yCountAndWidthInit = BinStatUtil.binCountAndWidth(yRangeInit.length, binOptionsY, extraExpand = true)

        // Final bin width and count

        val (xWidthInit, xExpandCenter) = if (!binOptionsX.hasBinWidth() && xCountAndWidthInit.count == 1 && yCountAndWidthInit.count > 1) {
            /*
            Double the width and shift the centre to the left if there is only one bin in the x direction,
            since in this case the initial binwidth is the same as the x range,
            but due to the specific stacking of hexagons all data must fit into half of one hexagon
            */
            Pair(xCountAndWidthInit.width * 2.0, -0.75)
        } else {
            Pair(xCountAndWidthInit.width, 0.0)
        }
        val xRangeFinal = adjustRangeFinal(xRange, xWidthInit, expandCenter = xExpandCenter)
        val yRangeFinal = adjustRangeFinal(yRange, yCountAndWidthInit.width)

        val xCountAndWidthFinal = BinStatUtil.binCountAndWidth(xRangeFinal.length, binOptionsX)
        val yCountAndWidthFinal = BinStatUtil.binCountAndWidth(yRangeFinal.length, binOptionsY)

        val countTotal = xCountAndWidthFinal.count * yCountAndWidthFinal.count
        val densityNormalizingFactor =
            densityNormalizingFactor(xRangeFinal.length, yRangeFinal.length, countTotal)

        val height = yCountAndWidthFinal.width * BINHEIGHT_TO_HEIGHT

        // If the hexagons are too flattened, floating-point arithmetic errors can occur, so computeBins() assumes the hexagons are regular
        val ratio = xCountAndWidthFinal.width / height
        val binsData = computeBins(
            data.getNumeric(TransformVar.X),
            data.getNumeric(TransformVar.Y).map { y -> y?.let { it * ratio} },
            xRangeFinal.lowerEnd,
            yRangeFinal.lowerEnd * ratio,
            xCountAndWidthFinal.count,
            yCountAndWidthFinal.count,
            xCountAndWidthFinal.width,
            yCountAndWidthFinal.width * ratio,
            BinStatUtil.weightAtIndex(data),
            densityNormalizingFactor,
        )

        return DataFrame.Builder()
            .putNumeric(Stats.X, binsData.x)
            .putNumeric(Stats.Y, binsData.y.map { y -> y / ratio }) // In the result, the y-coordinates need to be flattened back
            .putNumeric(Stats.COUNT, binsData.count)
            .putNumeric(Stats.DENSITY, binsData.density)
            .putNumeric(Stats.WIDTH, List(binsData.x.size) { xCountAndWidthFinal.width })
            .putNumeric(Stats.HEIGHT, List(binsData.x.size) { height })
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
        require(abs(binWidth / binHeight - BINHEIGHT_TO_HEIGHT) < EPSILON) { "Hexagons should be regular" }
        val countByBinIndexKey = computeCounts(xValues, yValues, xStart, yStart, binWidth, binHeight, weightAtIndex)
        val totalCount = countByBinIndexKey.values.sum()

        val xs = ArrayList<Double>()
        val ys = ArrayList<Double>()
        val counts = ArrayList<Double>()
        val densities = ArrayList<Double>()

        val x0 = xStart + binWidth / 2.0
        val y0 = yStart + binHeight / 2.0
        for (yIndex in 0 until binCountY) {
            for (xIndex in 0 until binCountX) {
                val binIndexKey = Pair(xIndex, yIndex)
                val count = countByBinIndexKey[binIndexKey] ?: 0.0

                if (count == 0.0 && drop) {
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
        // Index as if the tiling were done with rectangles - a first approximation to the true index
        fun getCoarseGridIndex(
            p: DoubleVector
        ): Pair<Int, Int> {
            val j = floor((p.y - yStart) / binHeight).toInt()
            val hexXStart = xStart + (j % 2) * binWidth / 2.0
            val i = floor((p.x - hexXStart) / binWidth).toInt()
            return Pair(i, j)
        }

        // The true index is either the coarse index or one of its six nearest neighbours
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

        fun getHexagonCenter(hexagonIndex: Pair<Int, Int>): DoubleVector {
            return DoubleVector(
                xStart + binWidth / 2.0 + if (hexagonIndex.second % 2 == 0)
                    hexagonIndex.first * binWidth
                else
                    hexagonIndex.first * binWidth + binWidth / 2.0,
                yStart + binHeight / 2.0 + hexagonIndex.second * binHeight
            )
        }

        fun isPointInHexagon(
            p: DoubleVector,
            hexagonIndex: Pair<Int, Int>
        ): Boolean {
            val halfHexHeight = 2.0 * binHeight / 3.0
            val q = p.subtract(getHexagonCenter(hexagonIndex))
            val v1 = DoubleVector(0.0, halfHexHeight)
            val v2 = DoubleVector(binWidth / 2.0, halfHexHeight / 2.0)
            val v3 = DoubleVector(binWidth / 2.0, -halfHexHeight / 2.0)
            val v4 = DoubleVector(0.0, -halfHexHeight)
            val v5 = DoubleVector(-binWidth / 2.0, -halfHexHeight / 2.0)
            val v6 = DoubleVector(-binWidth / 2.0, halfHexHeight / 2.0)
            // Check that q is in Hexagon(v1, v2, v3, v4, v5, v6):
            return polygonContainsCoordinate(listOf(v1, v2, v3, v4, v5, v6, v1), q)
        }

        fun distanceToHexagonCenter(
            p: DoubleVector,
            hexagonIndex: Pair<Int, Int>
        ): Double {
            val center = getHexagonCenter(hexagonIndex)
            return sqrt((p.x - center.x).pow(2) + (p.y - center.y).pow(2))
        }

        fun getAllTouchingHexagons(
            p: DoubleVector,
            allHexagons: Set<Pair<Int, Int>>
        ): Set<Pair<Int, Int>> {
            /*
            Among all available hexagons, the neighbors of a point are those that have the minimum distance from their centers to it.
            If we compare these distances with each other, they may not be exactly equal (due to floating-point arithmetic errors),
            but the target values differ from the others by an order of magnitude comparable to the size of the hexagon.
            So if all distances to the centers are divided by the minimum of these distances, the target numbers will be close to 1,
            and the rest will be noticeably larger.
            */
            val sortedHexagonsWithDistances = allHexagons
                .map { hexagonIndex -> Pair(hexagonIndex, distanceToHexagonCenter(p, hexagonIndex)) }
                .sortedBy { it.second }
            val minimalDistance = sortedHexagonsWithDistances.first().second
            return sortedHexagonsWithDistances
                .takeWhile { abs(it.second / minimalDistance - 1.0) < EPSILON }
                .map { it.first }
                .toSet()
        }

        val countByBinIndexKey = HashMap<Pair<Int, Int>, Double>()
        for (dataIndex in xValues.indices) {
            val x = xValues[dataIndex]
            val y = yValues[dataIndex]
            if (!SeriesUtil.allFinite(x, y)) {
                continue
            }
            val p = DoubleVector(x!!, y!!)
            val suspectedHexagons = hexWithNeighbours(getCoarseGridIndex(p))
            val hexIndexKey = suspectedHexagons.firstOrNull { isPointInHexagon(p, it) } ?:
                getAllTouchingHexagons(p, suspectedHexagons) // If the point is not in any of the suspected hexagons it means that point is on the border between hexagons
                    .sortedWith(compareBy(
                        { it.second }, // To prefer the hexagons with smaller y-index
                        { -it.first } // To prefer the hexagons with larger x-index
                    )).firstOrNull() // Point on the border belongs to the few hexagons, so we take the bottom-right one
            require(hexIndexKey != null) { "Unexpected state: no hexagon found for point ($x, $y)" }
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

        private const val EPSILON = 1e-4

        /*
        Let `binHeight` be the vertical distance between the centres of adjacent hexagons in coordinates.
        Then the hexagon height in coordinates is `hexHeight = 4 * binHeight / 3`.
        This height should be equal to `2 / sqrt(3)` if the user specifies `height = 1` (because `2 / sqrt(3)` is
        the diameter of the circumcircle of a regular hexagon when the diameter of the inscribed circle is 1).
        So, `height = hexHeight * sqrt(3) / 2 = binHeight * (2 * sqrt(3)) / 3`.
        These constants are used throughout the calculations.
        */
        private val HEIGHT_TO_BINHEIGHT = 3.0 / (2.0 * sqrt(3.0))
        private val BINHEIGHT_TO_HEIGHT = 2.0 * sqrt(3.0) / 3.0

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.FILL to Stats.COUNT,
            Aes.WIDTH to Stats.WIDTH,
            Aes.HEIGHT to Stats.HEIGHT
        )

        private fun adjustRangeInitial(r: DoubleSpan): DoubleSpan {
            // Span can't be 0
            return ensureApplicableRange(r)
        }

        private fun adjustRangeFinal(r: DoubleSpan, binWidth: Double, expandCenter: Double = 0.0): DoubleSpan {
            return r.expanded(binWidth / 2.0).let {
                DoubleSpan(it.lowerEnd + expandCenter * binWidth / 2.0, it.upperEnd + expandCenter * binWidth / 2.0)
            }.let {
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
            // Density should integrate to 1.0
            val area = xSpan * ySpan
            val binArea = area / count
            return 1.0 / binArea
        }
    }
}