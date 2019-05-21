package jetbrains.datalore.visualization.plot.builder.data.sampling

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import kotlin.random.Random

internal class GroupRandomSampling(sampleSize: Int, private val mySeed: Long?) : GroupSamplingBase(sampleSize) {

    override val expressionText: String
        get() = "sampling_" + ALIAS + "(" +
                "n=" + sampleSize +
                (if (mySeed != null) ", seed=$mySeed" else "") +
                ")"

    override fun apply(population: DataFrame, groupMapper: (Int) -> Int): DataFrame {
        checkArgument(isApplicable(population, groupMapper))
        val distinctGroups = SamplingUtil.distinctGroups(groupMapper, population.rowCount())

        distinctGroups.shuffle(createRandom())
        val pickedGroups = distinctGroups.take(sampleSize).toSet()
        return doSelect(population, pickedGroups, groupMapper)
    }

    private fun createRandom(): Random {
        return mySeed?.let { Random(it) } ?: Random.Default
    }

    companion object {
        const val ALIAS = "group_random"
    }
}
