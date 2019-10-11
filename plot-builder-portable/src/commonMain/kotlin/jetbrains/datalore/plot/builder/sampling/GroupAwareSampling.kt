package jetbrains.datalore.plot.builder.sampling

import jetbrains.datalore.plot.base.DataFrame

interface GroupAwareSampling : Sampling {
    fun isApplicable(population: DataFrame, groupMapper: (Int) -> Int): Boolean

    fun apply(population: DataFrame, groupMapper: (Int) -> Int): DataFrame
}