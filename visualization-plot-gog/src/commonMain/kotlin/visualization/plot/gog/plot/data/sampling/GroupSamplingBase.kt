package jetbrains.datalore.visualization.plot.base.data.sampling

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.GroupAwareSampling
import jetbrains.datalore.visualization.plot.gog.plot.data.GroupUtil

internal abstract class GroupSamplingBase(sampleSize: Int) : SamplingBase(sampleSize), GroupAwareSampling {

    override fun isApplicable(population: DataFrame, groupMapper: (Int) -> Int): Boolean {
        return isApplicable(population, groupMapper, SamplingUtil.groupCount(groupMapper, population.rowCount()))
    }

    open fun isApplicable(population: DataFrame, groupMapper: (Int) -> Int, groupCount: Int): Boolean {
        return groupCount > sampleSize
    }

    fun doSelect(population: DataFrame, pickedGroups: Set<Int>, groupMapper: (Int) -> Int): DataFrame {
        val indicesByGroup = GroupUtil.indicesByGroup(population.rowCount(), groupMapper)

        val pickedIndices = ArrayList<Int>()
        for (group in pickedGroups) {
            pickedIndices.addAll(indicesByGroup.get(group)!!)
        }
        return population.selectIndices(pickedIndices)

    }
}
