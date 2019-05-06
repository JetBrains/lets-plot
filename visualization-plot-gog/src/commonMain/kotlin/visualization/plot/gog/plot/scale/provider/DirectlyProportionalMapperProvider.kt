package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.scale.MapperUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.Transform
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.GuideMappers

internal open class DirectlyProportionalMapperProvider
/**
 * @param naValue value used when size is not defined
 */
(private val myMax: Double, naValue: Double) : MapperProviderBase<Double>(naValue) {

    override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): GuideMapper<Double> {
        val dataMax = MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans).upperEndpoint()
        return GuideMappers.continuousToContinuous(ClosedRange.closed(0.0, dataMax), ClosedRange.closed(0.0, myMax), naValue)
    }
}
