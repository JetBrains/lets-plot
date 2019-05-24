package jetbrains.datalore.visualization.plot.builder.sampling

import jetbrains.datalore.visualization.plot.base.DataFrame

interface PointSampling : Sampling {
    fun isApplicable(population: DataFrame): Boolean
    fun apply(population: DataFrame): DataFrame
}