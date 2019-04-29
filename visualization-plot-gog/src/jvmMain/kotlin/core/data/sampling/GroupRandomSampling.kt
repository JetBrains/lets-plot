package jetbrains.datalore.visualization.plot.gog.core.data.sampling

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import java.util.stream.Collectors.toSet
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
        val pickedGroups = distinctGroups.stream().limit(sampleSize.toLong()).collect(toSet())
        return doSelect(population, pickedGroups, groupMapper)
    }

    private fun createRandom(): Random {
        return mySeed?.let { Random(it) } ?: Random.Default
    }

    companion object {
        val ALIAS = "group_random"
    }
}
