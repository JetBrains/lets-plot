package jetbrains.datalore.visualization.plot.base.data

interface GroupAwareSampling : Sampling {
    fun isApplicable(population: DataFrame, groupMapper: (Int) -> Int): Boolean

    fun apply(population: DataFrame, groupMapper: (Int) -> Int): DataFrame
}