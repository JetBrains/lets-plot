/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.util.MutableDouble
import kotlin.math.*

object BinStatUtil {
    private const val MAX_BIN_COUNT = 500

    fun weightAtIndex(data: DataFrame): (Int) -> Double {
        if (data.has(TransformVar.WEIGHT)) {
            val weights = data.getNumeric(TransformVar.WEIGHT)
            return { index ->
                val weight = weights[index]
                SeriesUtil.asFinite(weight, 0.0)
            }
        }
        return { 1.0 }
    }

    // ToDo: need to deal fith n/a values (see DensityStat)
    fun weightVector(dataLength: Int, data: DataFrame): List<Double?> {
        return if (data.has(TransformVar.WEIGHT)) {
            data.getNumeric(TransformVar.WEIGHT)
        } else List(dataLength) { 1.0 }
    }

    fun binCountAndWidth(dataRange: Double, binOptions: BinOptions): CountAndWidth {
        var binCount = binOptions.binCount
        val binWidth: Double
        if (binOptions.hasBinWidth()) {
            binWidth = binOptions.binWidth!!
            var count = dataRange / binWidth
            count = min(MAX_BIN_COUNT.toDouble(), count)
            binCount = ceil(count).toInt()
        } else {
            binWidth = dataRange / binCount
        }
        return CountAndWidth(binCount, binWidth)
    }

    fun computeSummaryStatSeries(
        xs: List<Double?>,
        ys: List<Double?>,
        aggFunctions: Map<DataFrame.Variable, (List<Double>) -> Double>,
        rangeX: DoubleSpan,
        xPosKind: BinStat.XPosKind,
        xPos: Double,
        binOptions: BinOptions
    ): Map<DataFrame.Variable, List<Double>> {
        val (xValues, yValues) = SeriesUtil.filterFinite(xs, ys)
        val (binCount, binWidth, startX) = getBinningParameters(rangeX, xPosKind, xPos, binOptions)

        val statData = computeSummaryBins(xValues, yValues, aggFunctions, startX, binCount, binWidth)
        check(statData[Stats.X]?.size == binCount) {
            "Internal: stat data size=${statData[Stats.X]?.size} expected bin count=$binCount"
        }
        return statData
    }

    fun computeHistogramStatSeries(
        data: DataFrame,
        rangeX: DoubleSpan,
        valuesX: List<Double?>,
        xPosKind: BinStat.XPosKind,
        xPos: Double,
        binOptions: BinOptions
    ): BinsData {
        val (binCount, binWidth, startX) = getBinningParameters(rangeX, xPosKind, xPos, binOptions)

        // density plot area should be == 1
        val normalBinWidth = rangeX.length / binCount
        val densityNormalizingFactor = if (normalBinWidth > 0)
            1.0 / normalBinWidth
        else
            1.0

        // compute bins
        val binsData = computeHistogramBins(
            valuesX,
            startX,
            binCount,
            binWidth,
            weightAtIndex(data),
            densityNormalizingFactor
        )
        check(binsData.x.size == binCount)
        { "Internal: stat data size=" + binsData.x.size + " expected bin count=" + binCount }

        return binsData
    }

    fun computeDotdensityStatSeries(
        rangeX: DoubleSpan,
        valuesX: List<Double?>,
        binOptions: BinOptions
    ): BinsData {
        val spanX = rangeX.length
        val binWidth = if (spanX > 0.0) {
            binCountAndWidth(spanX, binOptions).width
        } else {
            // Only one stack of dots overall data
            1.0
        }

        return computeDotdensityBins(valuesX, binWidth)
    }

    internal fun getBinningParameters(
        rangeX: DoubleSpan,
        xPosKind: BinStat.XPosKind,
        xPos: Double,
        binOptions: BinOptions
    ): Triple<Int, Double, Double> {
        var startX = rangeX.lowerEnd
        var spanX = rangeX.upperEnd - startX

        // initial bin count/width
        val b: CountAndWidth = binCountAndWidth(spanX, binOptions)

        // adjusted bin count/width
        // extend the data range by 0.7 of binWidth on each ends (to allow limited horizontal adjustments)
        startX -= b.width * 0.7
        spanX += b.width * 1.4
        val (binCount, binWidth) = binCountAndWidth(spanX, binOptions)

        if (xPosKind == BinStat.XPosKind.NONE) {
            return Triple(binCount, binWidth, startX)
        }

        // optional horizontal adjustment (+/-0.5 bin width max)
        var minDelta = when (xPosKind) {
            BinStat.XPosKind.CENTER -> Double.MAX_VALUE
            else -> xPos - startX
        }

        for (i in 0 until binCount) {
            val binLeft = startX + i * binWidth
            val delta = when (xPosKind) {
                BinStat.XPosKind.CENTER -> xPos - (binLeft + binWidth / 2)
                else -> xPos - (binLeft + binWidth)
            }
            if (abs(delta) < abs(minDelta)) {
                minDelta = delta
            }
        }

        // max offset: +/-0.5 bin width
        val offset = minDelta % (binWidth / 2)
        startX += offset

        return Triple(binCount, binWidth, startX)
    }

    private fun computeSummaryBins(
        xValues: List<Double>,
        yValues: List<Double>,
        aggFunctions: Map<DataFrame.Variable, (List<Double>) -> Double>,
        startX: Double,
        binCount: Int,
        binWidth: Double
    ): Map<DataFrame.Variable, List<Double>> {
        val yValuesByBinIndex = HashMap<Int, MutableList<Double>>()
        for ((x, y) in xValues zip yValues) {
            val binIndex = floor((x - startX) / binWidth).toInt()
            yValuesByBinIndex.getOrPut(binIndex, { mutableListOf() }).add(y)
        }

        val statX = ArrayList<Double>()
        val summaryBins: Map<DataFrame.Variable, MutableList<Double>> = aggFunctions.keys.associateWith { mutableListOf() }
        val lowerX = startX + binWidth / 2
        for (i in 0 until binCount) {
            statX.add(lowerX + i * binWidth)
            val sortedBin = (yValuesByBinIndex[i] ?: emptyList()).sorted()
            for ((statVar, aggValues) in summaryBins) {
                val aggFunction = aggFunctions[statVar] ?: { Double.NaN }
                aggValues.add(aggFunction(sortedBin))
            }
        }

        return mapOf(Stats.X to statX) + summaryBins
    }

    private fun computeHistogramBins(
        valuesX: List<Double?>,
        startX: Double,
        binCount: Int,
        binWidth: Double,
        weightAtIndex: (Int) -> Double,
        densityNormalizingFactor: Double
    ): BinsData {

        var totalCount = 0.0
        val countByBinIndex = HashMap<Int, MutableDouble>()
//        val dataIndicesByBinIndex = HashMap<Int, MutableList<Int>>()
        for (dataIndex in valuesX.indices) {
            val x = valuesX[dataIndex]
            if (!SeriesUtil.isFinite(x)) {
                continue
            }
            val weight = weightAtIndex(dataIndex)
            totalCount += weight
            val binIndex = floor((x!! - startX) / binWidth).toInt()
            if (!countByBinIndex.containsKey(binIndex)) {
                countByBinIndex[binIndex] = MutableDouble(0.0)
            }
            countByBinIndex[binIndex]!!.getAndAdd(weight)

//            if (!dataIndicesByBinIndex.containsKey(binIndex)) {
//                dataIndicesByBinIndex[binIndex] = ArrayList()
//            }

//            dataIndicesByBinIndex[binIndex]!!.add(dataIndex)
        }

        val x = ArrayList<Double>()
        val counts = ArrayList<Double>()
        val densities = ArrayList<Double>()

        val x0 = startX + binWidth / 2
        for (i in 0 until binCount) {
            x.add(x0 + i * binWidth)

            var count = 0.0
            // some bins are left empty (not excluded from map)
            if (countByBinIndex.containsKey(i)) {
                count = countByBinIndex[i]!!.get()
            }

            counts.add(count)
            val density = count / totalCount * densityNormalizingFactor
            densities.add(density)
        }

//        return BinsData(x, counts, densities, dataIndicesByBinIndex)
        return BinsData(x, counts, densities, List(x.size) { binWidth })
    }

    private fun computeDotdensityBins(
        valuesX: List<Double?>,
        binWidth: Double
    ): BinsData {
        fun updateBinsData(
            binsData: BinsData,
            stack: MutableList<Double>,
            dataSize: Int
        ): BinsData {
            val v = (stack.last() - stack.first()) / 2.0

            return BinsData(
                binsData.x + listOf(stack.first() + v),
                binsData.count + listOf(stack.size.toDouble()),
                binsData.density + listOf(stack.size.toDouble() / dataSize),
                binsData.binWidth + listOf(binWidth)
            )
        }

        val sortedX = valuesX.filter { SeriesUtil.isFinite(it) }
            .map { it!! }
            .sorted()
        var binsData = BinsData(emptyList(), emptyList(), emptyList(), emptyList())
        if (sortedX.isEmpty()) {
            return binsData
        }
        var stack = mutableListOf(sortedX.first())
        for (i in 1 until sortedX.size) {
            if (sortedX[i] - stack.first() < binWidth) {
                stack.add(sortedX[i])
                continue
            }
            binsData = updateBinsData(binsData, stack, sortedX.size)
            stack = mutableListOf(sortedX[i])
        }
        binsData = updateBinsData(binsData, stack, sortedX.size)

        return binsData
    }

    class BinOptions(
        binCount: Int, val binWidth: Double?  // optional
    ) {
        val binCount: Int = min(MAX_BIN_COUNT, max(1, binCount))

        fun hasBinWidth(): Boolean {
            return binWidth != null && binWidth > 0
        }
    }

    data class CountAndWidth(val count: Int, val width: Double)

    class BinsData(
        internal val x: List<Double>,
        internal val count: List<Double>,
        internal val density: List<Double>,
        internal val binWidth: List<Double>
    )
}
