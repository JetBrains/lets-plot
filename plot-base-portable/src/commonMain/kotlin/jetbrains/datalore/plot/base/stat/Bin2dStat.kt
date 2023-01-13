/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.data.SeriesUtil.ensureApplicableRange
import jetbrains.datalore.plot.common.data.SeriesUtil.isBeyondPrecision
import jetbrains.datalore.plot.common.util.MutableDouble
import kotlin.math.floor

/**
 * Default stat for geom_bin2d
 *
 * @param binCountX Number of bins (overridden by binWidth).
 * @param binCountY Number of bins (overridden by binWidth).
 * @param binWidthX Used to compute binCount such that bins covers the range of the data.
 * @param binWidthY Used to compute binCount such that bins covers the range of the data.
 * @param drop if TRUE removes all cells with 0 counts.
 *
 * Computed values:
 *
 * count - number of points in bin
 * density - density of points in bin, scaled to integrate to 1
 * ncount - count, scaled to maximum of 1
 * ndensity - density, scaled to maximum of 1
 */
class Bin2dStat(
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
    ): Bins2dData {

        var totalCount = 0.0
        val countByBinIndexKey = HashMap<Pair<Int, Int>, MutableDouble>()
        for (dataIndex in xValues.indices) {
            val x = xValues[dataIndex]
            val y = yValues[dataIndex]
            if (!SeriesUtil.allFinite(x, y)) {
                continue
            }
            val weight = weightAtIndex(dataIndex)
            totalCount += weight
            val binIndexX = floor((x!! - xStart) / binWidth).toInt()
            val binIndexY = floor((y!! - yStart) / binHeight).toInt()
            val binIndexKey = Pair(binIndexX, binIndexY)
            if (!countByBinIndexKey.containsKey(binIndexKey)) {
                countByBinIndexKey[binIndexKey] = MutableDouble(0.0)
            }
            countByBinIndexKey[binIndexKey]!!.getAndAdd(weight)
        }

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
                    count = countByBinIndexKey[binIndexKey]!!.get()
                }

                if (drop && count == 0.0) {
                    continue
                }

                xs.add(x0 + xIndex * binWidth)
                ys.add(y0 + yIndex * binHeight)
                counts.add(count)
                val density = count / totalCount * densityNormalizingFactor
                densities.add(density)
            }
        }

        return Bins2dData(xs, ys, counts, densities)
    }


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
            return if (isBeyondPrecision(r)) {
                // 0 span allways becomes 1
                r.expanded(0.5)
            } else {
                // Expand range by half of bin width (arbitrary choise - can be any positive num) to
                // avoid data-points on the marginal bin margines.
                val exp = binWidth / 2.0
                r.expanded(exp)
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

    class Bins2dData(
        internal val x: List<Double>,
        internal val y: List<Double>,
        internal val count: List<Double>,
        internal val density: List<Double>
    )

    class Bins2dWeightedCounts(
        internal val total: Double,
        internal val countByBinXY: Map<Pair<Double, Double>, Double>
    )
}