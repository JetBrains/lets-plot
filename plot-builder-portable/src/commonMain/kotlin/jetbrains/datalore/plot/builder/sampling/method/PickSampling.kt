package jetbrains.datalore.plot.builder.sampling.method

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.builder.sampling.PointSampling
import jetbrains.datalore.plot.builder.sampling.method.SamplingUtil.xVar
import jetbrains.datalore.plot.common.data.SeriesUtil

/**
 * Picks first N data points with unique X-values. In addition scoops all data-points with X-values
 * which are already being accepted to the sample (to account for grouping)
 */
internal class PickSampling(sampleSize: Int) : SamplingBase(sampleSize),
    PointSampling {

    override val expressionText: String
        get() = "sampling_" + ALIAS + "(" +
                "n=" + sampleSize + ")"

    override fun apply(population: DataFrame): DataFrame {
        checkArgument(isApplicable(population))
        val pickedXValues = HashSet<Any>()
        val pickedIndices = ArrayList<Int>()

        val xValues = population[xVar(population)]
        for (i in xValues.indices) {
            val v = xValues[i]
            if (v is String || SeriesUtil.isFinite(v as Double)) {
                if (!pickedXValues.contains(v)) {
                    if (pickedXValues.size >= sampleSize) {
                        // do not add new X-values
                        continue
                    }
                    // pick this data-point and all subsequent data-points with equal X
                    pickedXValues.add(v)
                }

                pickedIndices.add(i)
            }
        }

        return population.selectIndices(pickedIndices)
    }

    companion object {
        const val ALIAS = "pick"
    }
}
