/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.common.data.SeriesUtil


class ColorGradientMapperProvider(low: Color?, high: Color?, naValue: Color) : MapperProviderBase<Color>(naValue) {

    private val low: Color = low ?: ColorMapper.DEF_GRADIENT_LOW
    private val high: Color = high ?: ColorMapper.DEF_GRADIENT_HIGH

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): GuideMapper<Color> {
        val transformedDomain = discreteTransform.effectiveDomainTransformed
        val mapperDomain = SeriesUtil.range(transformedDomain)!!
        val gradient = ColorMapper.gradient(mapperDomain, low, high, naValue)
        return GuideMappers.asNotContinuous(gradient)
    }

    override fun createContinuousMapper2(domain: ClosedRange<Double>, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform2(domain, trans)
        val gradient = ColorMapper.gradient(domain, low, high, naValue)
        return GuideMappers.asContinuous(gradient)
    }

    companion object {
        val DEFAULT = ColorGradientMapperProvider(
            null,
            null,
            ColorMapper.NA_VALUE
        )
    }
}
