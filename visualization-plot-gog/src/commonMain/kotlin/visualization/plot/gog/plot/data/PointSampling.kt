package jetbrains.datalore.visualization.plot.base.data

interface PointSampling : Sampling {
    fun isApplicable(population: DataFrame): Boolean
    fun apply(population: DataFrame): DataFrame
}