package jetbrains.datalore.visualization.plot.builder.data.sampling

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.builder.data.PointSampling
import kotlin.math.round

/**
 * Take points at indices selected at regular intervals starting from index 0
 */
internal class SystematicSampling(sampleSize: Int) : SamplingBase(sampleSize), PointSampling {

    override val expressionText: String
        get() = "sampling_" + ALIAS + "(" +
                "n=" + sampleSize +
                ")"

    override fun isApplicable(population: DataFrame): Boolean {
        return super.isApplicable(population) && computeStep(population.rowCount()) >= 2
    }

    override fun apply(population: DataFrame): DataFrame {
        checkArgument(isApplicable(population))
        val popSize = population.rowCount()

        val step = computeStep(popSize)
        val pickedIndices = ArrayList<Int>()
        var i = 0
        while (i < popSize) {
            pickedIndices.add(i)
            i += step
        }

        return population.selectIndices(pickedIndices)
    }

    private fun computeStep(popSize: Int): Int {
        return round(popSize.toDouble() / (sampleSize - 1)).toInt()
    }

    companion object {
        const val ALIAS = "systematic"

        fun computeStep(popSize: Int, sampleSize: Int): Int {
            return round((popSize - 1).toDouble() / (sampleSize - 1)).toInt()
        }
    }
}
