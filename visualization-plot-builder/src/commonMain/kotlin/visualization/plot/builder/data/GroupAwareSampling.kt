package jetbrains.datalore.visualization.plot.builder.data

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.Sampling

interface GroupAwareSampling : Sampling {
    fun isApplicable(population: DataFrame, groupMapper: (Int) -> Int): Boolean

    fun apply(population: DataFrame, groupMapper: (Int) -> Int): DataFrame
}