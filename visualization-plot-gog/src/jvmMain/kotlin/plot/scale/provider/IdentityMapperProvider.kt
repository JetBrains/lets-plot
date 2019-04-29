package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Variable
import jetbrains.datalore.visualization.plot.gog.core.scale.Transform
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.MapperProvider
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.GuideMappers

internal class IdentityMapperProvider<T>(private val myDiscreteMapperProvider: IdentityDiscreteMapperProvider<T>, private val myContinuousMapper: (Double) -> T) : MapperProvider<T> {

    override fun createDiscreteMapper(data: DataFrame, variable: Variable): GuideMapper<T> {
        return myDiscreteMapperProvider.createDiscreteMapper(data, variable)
    }

    override fun createContinuousMapper(data: DataFrame, variable: Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform): GuideMapper<T> {
        return GuideMappers.adaptContinuous(myContinuousMapper)
    }
}
