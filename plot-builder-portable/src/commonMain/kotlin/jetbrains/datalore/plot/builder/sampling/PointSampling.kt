package jetbrains.datalore.plot.builder.sampling

import jetbrains.datalore.plot.base.DataFrame

interface PointSampling : Sampling {
    fun isApplicable(population: DataFrame): Boolean
    fun apply(population: DataFrame): DataFrame
}