/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.common.data.SeriesUtil

class ColorGradientnMapperProvider(
    private val colors: List<Color>,
    naValue: Color
) : MapperProviderBase<Color>(naValue) {

    init {
        require(colors.size > 1) { "gradient requires colors list with two or more elements" }
    }

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        val transformedDomain = discreteTransform.effectiveDomainTransformed
        val mapperDomain = SeriesUtil.range(transformedDomain)!!
        val gradient = createGradient(mapperDomain)
        return GuideMappers.asNotContinuous(ScaleMapper.wrap(gradient))
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform2(domain, trans)
        val gradient = createGradient(domain)
        return GuideMappers.asContinuous(ScaleMapper.wrap(gradient))
    }

    private fun createGradient(domain: DoubleSpan): (Double?) -> Color {
        val subdomainsCount = colors.size - 1
        val subdomainLength = domain.length / subdomainsCount

        val mappers = (0..subdomainsCount)
            .map { domain.lowerEnd + subdomainLength * it }
            .zip(colors)
            .windowed(2)
            .map { (low, high) ->
                val (lowValue, lowColor) = low
                val (highValue, highColor) = high
                val subdomain = DoubleSpan(lowValue, highValue)
                subdomain to ColorMapper.gradient(subdomain, lowColor, highColor, naValue)
            }

        return { value ->
            value?.let {
                mappers
                    .firstOrNull { (subdomain, _) -> value in subdomain }
                    ?.let { (_, gradient) -> gradient(value) }
                    ?: naValue
            } ?: naValue
        }
    }
}
