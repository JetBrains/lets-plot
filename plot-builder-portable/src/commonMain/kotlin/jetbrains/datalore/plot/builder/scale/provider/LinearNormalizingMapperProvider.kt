package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.Transform
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.base.scale.MapperUtil

open class LinearNormalizingMapperProvider(
        private val outputRange: ClosedRange<Double>,
        naValue: Double) :
        MapperProviderBase<Double>(naValue) {

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Double> {
        val values = DataFrameUtil.distinctValues(data, variable)
        return GuideMappers.discreteToContinuous(values, outputRange, naValue)
    }

    override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<Double> {
        val dataRange = MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans)
        return GuideMappers.continuousToContinuous(dataRange, outputRange, naValue)
    }
}
