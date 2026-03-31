/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.MapperUtil
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper
import org.jetbrains.letsPlot.core.plot.builder.scale.PaletteGenerator
import org.jetbrains.letsPlot.core.plot.builder.scale.mapper.GuideMappers


class ColorManualMapperProvider(
    private val colors: List<Color>,
    naValue: Color
) : MapperProviderBase<Color>(naValue),
    PaletteGenerator {

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        return GuideMappers.discreteToDiscrete(discreteTransform, colors, naValue)
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
        return GuideMappers.continuousToQuantizedContinuous(domain, colors, naValue)
    }

    override fun createPaletteGeneratorScaleMapper(colorCount: Int): ScaleMapper<Color> {
        val domain = DoubleSpan(0.0, (colorCount - 1).toDouble())
        return createContinuousMapper(domain, Transforms.IDENTITY)
    }
}
