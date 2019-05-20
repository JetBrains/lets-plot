package jetbrains.datalore.visualization.plot.gog.plot.scale

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.scale.Transform

open class MapperProviderAdapter<T> : MapperProvider<T> {
    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<T> {
        throw IllegalStateException("Can't create mapper for discrete domain ($variable)")
    }

    override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<T> {
        throw IllegalStateException("Can't create mapper for continuous domain ($variable)")
    }
}
