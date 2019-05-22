package jetbrains.datalore.visualization.plot.builder.data

import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.data.Sampling

interface PointSampling : Sampling {
    fun isApplicable(population: DataFrame): Boolean
    fun apply(population: DataFrame): DataFrame
}