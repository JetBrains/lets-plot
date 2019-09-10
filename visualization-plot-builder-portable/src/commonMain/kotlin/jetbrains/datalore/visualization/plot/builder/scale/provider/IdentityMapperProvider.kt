package jetbrains.datalore.visualization.plot.builder.scale.provider

import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.DataFrame.Variable
import jetbrains.datalore.visualization.plot.base.Transform
import jetbrains.datalore.visualization.plot.builder.scale.GuideMapper
import jetbrains.datalore.visualization.plot.builder.scale.MapperProvider
import jetbrains.datalore.visualization.plot.builder.scale.mapper.GuideMappers

class IdentityMapperProvider<T>(
        private val myDiscreteMapperProvider: IdentityDiscreteMapperProvider<T>,
        private val myContinuousMapper: (Double?) -> T?) : MapperProvider<T> {

    override fun createDiscreteMapper(data: DataFrame, variable: Variable): GuideMapper<T> {
        return myDiscreteMapperProvider.createDiscreteMapper(data, variable)
    }

    override fun createContinuousMapper(data: DataFrame, variable: Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<T> {
        return GuideMappers.adaptContinuous(myContinuousMapper)
    }
}
