package jetbrains.datalore.visualization.plot.gog.core.data.stat

import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.TransformVar
import jetbrains.datalore.visualization.plot.gog.core.util.MutableDouble
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

object StatUtil {
    private val MAX_BIN_COUNT = 500

    fun weightAtIndex(data: DataFrame): (Int) -> Double {
        if (data.has(TransformVar.WEIGHT)) {
            val weights = data.getNumeric(TransformVar.WEIGHT)
            return { index ->
                val weight = weights[index]
                if (SeriesUtil.isFinite(weight))
                    weight
                else
                    0.0
            }
        }
        return { 1.0 }
    }

    fun weightVector(dataLength: Int, data: DataFrame): List<Double> {
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

    /*
  public static List<Double> pickAtIndices(List<Double> list, List<Integer> indices) {
    List<Double> result = new ArrayList<>();
    for (Integer index : indices) {
      result.add(list.get(index));
    }
    return result;
  }
  */

    fun computeBins(
            valuesX: List<Double>, startX: Double, binCount: Int, binWidth: Double, weightAtIndex: (Int) -> Double, densityNormalizingFactor: Double): BinsData {
        var totalCount = 0.0
        val countByBinIndex = HashMap<Int, MutableDouble>()
        val dataIndicesByBinIndex = HashMap<Int, MutableList<Int>>()
        for (dataIndex in valuesX.indices) {
            val x = valuesX[dataIndex]
            if (!SeriesUtil.isFinite(x)) {
                continue
            }
            val weight = weightAtIndex(dataIndex)
            totalCount += weight
            val binIndex = floor((x - startX) / binWidth).toInt()
            if (!countByBinIndex.containsKey(binIndex)) {
                countByBinIndex[binIndex] = MutableDouble(0.0)
            }
            countByBinIndex[binIndex]!!.getAndAdd(weight)

            if (!dataIndicesByBinIndex.containsKey(binIndex)) {
                dataIndicesByBinIndex[binIndex] = ArrayList()
            }

            dataIndicesByBinIndex[binIndex]!!.add(dataIndex)
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

        return BinsData(x, counts, densities, dataIndicesByBinIndex)
    }

    class BinOptions(binCount: Int, val binWidth: Double?  // optional
    ) {
        val binCount: Int = min(MAX_BIN_COUNT, max(1, binCount))

        fun hasBinWidth(): Boolean {
            return binWidth != null && binWidth > 0
        }
    }

    class CountAndWidth(val count: Int, val width: Double)

    class BinsData(internal val x: List<Double>, internal val count: List<Double>, internal val density: List<Double>, private val dataIndicesByBinIndex: Map<Int, List<Int>>)

}
