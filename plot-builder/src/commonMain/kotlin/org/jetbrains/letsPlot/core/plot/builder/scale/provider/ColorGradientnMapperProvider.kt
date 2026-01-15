/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.interval.DoubleSpan.Companion.encloseAllQ
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.color.GradientUtil.createGradient
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.ensureApplicableRange
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.MapperUtil
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.PaletteGenerator
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.GuideMappers

class ColorGradientnMapperProvider(
    private val colors: List<Color>,
    naValue: Color
) : MapperProviderBase<Color>(naValue),
    PaletteGenerator {

    init {
        require(colors.size > 1) { "gradient requires colors list with two or more elements" }
    }

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        val transformedDomain = discreteTransform.effectiveDomainTransformed
        val mapperDomain = ensureApplicableRange(encloseAllQ(transformedDomain))
        val gradient = createGradient(mapperDomain, colors, naValue)
        return GuideMappers.asNotContinuous(ScaleMapper.wrap(gradient))
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
        val gradient = createGradient(domain, colors, naValue)
        return GuideMappers.asContinuous(ScaleMapper.wrap(gradient))
    }

    override fun createPaletteGeneratorScaleMapper(colorCount: Int): ScaleMapper<Color> {
        val domain = DoubleSpan(0.0, (colorCount - 1).toDouble())
        return createContinuousMapper(domain, Transforms.IDENTITY)
    }
}
