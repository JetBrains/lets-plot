/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.colorspace.HCL
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.MapperUtil
import org.jetbrains.letsPlot.core.plot.builder.scale.GuideMapper

class ColorHueMapperProvider(
    hueRange: DoubleSpan,
    private val chroma: Double,
    private val luminance: Double,
    startHue: Double,
    private val reversed: Boolean,
    naValue: Color
) : HclColorMapperProvider(
    naValue = naValue,
) {
    private val hueRange = DoubleSpan(hueRange.lowerEnd + startHue, hueRange.upperEnd + startHue)

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        val n = discreteTransform.effectiveDomain.size

        // if full circle prevent first and last colors to be the same
        val hueRange = hueRange.takeUnless { it.length % 360 < 1.0 }
            ?: DoubleSpan(hueRange.lowerEnd, hueRange.upperEnd - 360.0 / n)

        val step = hueRange.length / (n - 1)

        val from = HCL(hueRange.lowerEnd, chroma, luminance)
        val to = HCL(hueRange.lowerEnd + step * (n - 1), chroma, luminance)

        return createDiscreteMapper(
            transformedDomain = discreteTransform.effectiveDomainTransformed,
            from = from.takeUnless { reversed } ?: to,
            to = to.takeUnless { reversed } ?: from
        )
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)

        val from = HCL(hueRange.lowerEnd, chroma, luminance)
        val to = HCL(hueRange.upperEnd, chroma, luminance)

        return createContinuousMapper(
            domain = domain,
            from = from.takeUnless { reversed } ?: to,
            to = to.takeUnless { reversed } ?: from
        )
    }

    companion object {
        // defaults from ggplot2
        const val DEF_CHROMA = 100.0
        const val DEF_LUMINANCE = 65.0
        const val DEF_START_HUE = 0.0
        val DEF_HUE_RANGE = DoubleSpan(15.0, 375.0)
    }
}