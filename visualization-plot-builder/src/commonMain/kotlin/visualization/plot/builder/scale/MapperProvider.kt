package jetbrains.datalore.visualization.plot.builder.scale

import jetbrains.datalore.visualization.plot.base.Transform
import jetbrains.datalore.visualization.plot.base.data.DataFrame

interface MapperProvider<T> {
    /**
     * Create mapper with discrete input (domain)
     */
    // ToDo: add limits to this method
    fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<T>

    /**
     * Create mapper with continuous (numeric) input (domain)
     */
    fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<T>
}
