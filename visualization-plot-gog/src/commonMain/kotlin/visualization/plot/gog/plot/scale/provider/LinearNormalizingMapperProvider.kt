package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.MapperUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.Transform
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.GuideMappers

internal open class LinearNormalizingMapperProvider
/**
 * @param naValue value used when size is not defined
 */
(private val myRange: ClosedRange<Double>, naValue: Double) : MapperProviderBase<Double>(naValue) {

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Double> {
        val values = DataFrameUtil.distinctValues(data, variable)
        return GuideMappers.discreteToContinuous(values, myRange, naValue)
    }

    override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<Double> {
        val dataRange = MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans)
        return GuideMappers.continuousToContinuous(dataRange, myRange, naValue)
    }
}
