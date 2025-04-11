/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.interval.DoubleSpan.Companion.encloseAllQ
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.ensureApplicableRange
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.MapperUtil
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.ColorMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.GuideMappers


class ColorGradientMapperProvider(low: Color?, high: Color?, naValue: Color) : MapperProviderBase<Color>(naValue) {

    private val low: Color = low ?: ColorMapper.DEF_GRADIENT_LOW
    private val high: Color = high ?: ColorMapper.DEF_GRADIENT_HIGH

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        val transformedDomain = discreteTransform.effectiveDomainTransformed
        val mapperDomain = ensureApplicableRange(encloseAllQ(transformedDomain))
        val gradient = ColorMapper.gradient(mapperDomain, low, high, naValue)
        return GuideMappers.asNotContinuous(ScaleMapper.wrap(gradient))
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
        val gradient = ColorMapper.gradient(domain, low, high, naValue)
        return GuideMappers.asContinuous(ScaleMapper.wrap(gradient))
    }

    companion object {
        val DEFAULT = ColorGradientMapperProvider(
            null,
            null,
            ColorMapper.NA_VALUE
        )
    }
}
