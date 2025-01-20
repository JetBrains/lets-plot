/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

// TODO: Add parameters as in the Bin2dStat
class BinHexStat : BaseStat(DEF_MAPPING) {
    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X, Aes.Y)) {
            return withEmptyStatValues()
        }

        val binsData = computeBins(
            data.getNumeric(TransformVar.X),
            data.getNumeric(TransformVar.Y),
            -1.5, // TODO
            -1.5, // TODO
            3, // TODO
            3, // TODO
            1.0, // TODO
            1.0, // TODO
            BinStatUtil.weightAtIndex(data),
            1.0 // TODO
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

        val x0 = xStart + binWidth / 2
        val y0 = yStart + binHeight / 2
        for (xIndex in 0 until binCountX) {
            for (yIndex in 0 until binCountY) {
                val binIndexKey = Pair(xIndex, yIndex)
                var count = 0.0
                if (countByBinIndexKey.containsKey(binIndexKey)) {
                    count = countByBinIndexKey[binIndexKey]!!
                }

                if (yIndex % 2 == 0) {
                    xs.add(x0 + xIndex * binWidth)
                } else {
                    xs.add(x0 + xIndex * binWidth + binWidth / 2)
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
            val hexXStart = xStart + (j % 2) * binWidth / 2
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
                { it.second },
                { it.first }
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
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.FILL to Stats.COUNT
        )
    }
}