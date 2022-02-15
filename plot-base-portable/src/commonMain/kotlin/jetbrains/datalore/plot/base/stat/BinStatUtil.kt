/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.plot.common.util.MutableDouble
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

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

    fun computeHistogramBins(
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

    fun computeDotdensityBins(
        valuesX: List<Double?>,
        binWidth: Double
    ): BinsData {
        val sortedX = valuesX.filter { SeriesUtil.isFinite(it) }
            .map { it!! }
            .sorted()
        val updateBinsData: (BinsData, MutableList<Double>) -> BinsData = { binsData, stack ->
            val v = (stack.last() - stack.first()) / 2.0
            BinsData(
                binsData.x + listOf(stack.first() + v),
                binsData.count + listOf(stack.size.toDouble()),
                binsData.density + listOf(stack.size.toDouble() / sortedX.size),
                binsData.binWidth + listOf(binWidth)
            )
        }

        var binsData = BinsData(emptyList(), emptyList(), emptyList(), emptyList())
        var stack = mutableListOf(sortedX.first())
        for (i in 1 until sortedX.size) {
            if (sortedX[i] - stack.first() < binWidth) {
                stack.add(sortedX[i])
                continue
            }
            binsData = updateBinsData(binsData, stack)
            stack = mutableListOf(sortedX[i])
        }
        binsData = updateBinsData(binsData, stack)

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

    class CountAndWidth(val count: Int, val width: Double)

    class BinsData(
        internal val x: List<Double>,
        internal val count: List<Double>,
        internal val density: List<Double>,
        internal val binWidth: List<Double>
    )
}
