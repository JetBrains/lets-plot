package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.MapperUtil
import jetbrains.datalore.visualization.plot.gog.core.scale.Transform
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.ColorMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.GuideMappers

internal class ColorGradientMapperProvider(low: Color?, high: Color?, naValue: Color) : MapperProviderBase<Color>(naValue) {

    private val myLow: Color
    private val myHigh: Color

    init {
        myLow = low ?: ColorMapper.DEF_GRADIENT_LOW
        myHigh = high ?: ColorMapper.DEF_GRADIENT_HIGH
    }

    override fun createDiscreteMapper(data: DataFrame, variable: DataFrame.Variable): GuideMapper<Color> {
        val domainValues = DataFrameUtil.distinctValues(data, variable)
        val domainValuesAsNumbers = MapperUtil.mapDiscreteDomainValuesToNumbers(domainValues)
        val mapperDomain = SeriesUtil.range(domainValuesAsNumbers.values)!!
        val gradient = ColorMapper.gradient(mapperDomain, myLow, myHigh, naValue)
        return GuideMappers.adapt(gradient)
    }

    override fun createContinuousMapper(data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform): GuideMapper<Color> {
        val domain = MapperUtil.rangeWithLimitsAfterTransform(data, variable, lowerLimit, upperLimit, trans)
        val gradient = ColorMapper.gradient(domain, myLow, myHigh, naValue)
        return GuideMappers.adaptContinuous(gradient)
    }

    companion object {
        val DEFAULT = ColorGradientMapperProvider(null, null, ColorMapper.NA_VALUE)
    }
}
