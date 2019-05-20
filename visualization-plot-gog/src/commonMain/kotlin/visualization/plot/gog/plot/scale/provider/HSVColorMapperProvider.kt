package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.scale.MapperUtil
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.plot.scale.GuideMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.ColorMapper
import jetbrains.datalore.visualization.plot.gog.plot.scale.mapper.GuideMappers

internal abstract class HSVColorMapperProvider(naValue: Color) : MapperProviderBase<Color>(naValue) {

    protected fun createDiscreteMapper(domainValues: Collection<*>, lowHSV: DoubleArray, highHSV: DoubleArray): GuideMapper<Color> {
        val domainValuesAsNumbers = MapperUtil.mapDiscreteDomainValuesToNumbers(domainValues)
        val mapperDomain = SeriesUtil.range(domainValuesAsNumbers.values)
        val gradient = ColorMapper.gradientHSV(mapperDomain!!, lowHSV, highHSV, false, naValue)
        return GuideMappers.adapt(gradient)
    }

    protected fun createContinuousMapper(domain: ClosedRange<Double>, lowHSV: DoubleArray, highHSV: DoubleArray): GuideMapper<Color> {
        val gradient = ColorMapper.gradientHSV(domain, lowHSV, highHSV, false, naValue)
        return GuideMappers.adaptContinuous(gradient)
    }
}
