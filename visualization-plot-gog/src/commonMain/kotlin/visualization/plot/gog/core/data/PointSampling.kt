package jetbrains.datalore.visualization.plot.gog.core.data

interface PointSampling : Sampling {
    fun isApplicable(population: DataFrame): Boolean
    fun apply(population: DataFrame): DataFrame
}
