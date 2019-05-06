package jetbrains.datalore.visualization.plot.gog.plot.scale

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.scale.Transform

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
