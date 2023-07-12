/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.ColorMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import kotlin.math.abs
import kotlin.math.min

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
        val gradient = createGradient(mapperDomain, colors, naValue)
        return GuideMappers.asNotContinuous(ScaleMapper.wrap(gradient))
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
        val gradient = createGradient(domain, colors, naValue)
        return GuideMappers.asContinuous(ScaleMapper.wrap(gradient))
    }


    companion object {
        internal fun createGradient(
            domain: DoubleSpan,
            colors: List<Color>,
            naColor: Color,
            alpha: Double = 1.0
        ): (Double?) -> Color {
            val subdomainsCount = colors.size - 1
            val subdomainLength = domain.length / subdomainsCount

            val subdomainEnds = (0 until subdomainsCount)
                .map { domain.lowerEnd + subdomainLength * it } +
                    listOf(domain.upperEnd) // The last "end" should be exact.
            val mappers = subdomainEnds.zip(colors)
                .windowed(2)
                .map { (low, high) ->
                    val (lowValue, lowColor) = low
                    val (highValue, highColor) = high
                    val subdomain = DoubleSpan(lowValue, highValue)
                    ColorMapper.gradient(subdomain, lowColor, highColor, naColor, alpha)
                }

            return { value ->
                when {
                    value == null || !value.isFinite() -> naColor
                    value < subdomainEnds.first() || value > subdomainEnds.last() -> naColor
                    else -> {
                        val i = subdomainEnds.binarySearch(value)
                        val subdomainIndex = when {
                            i < 0 -> abs(i + 1) - 1
                            else -> min(i, mappers.lastIndex)
                        }
                        mappers[subdomainIndex](value)
                    }
                }
            }
        }
    }
}
