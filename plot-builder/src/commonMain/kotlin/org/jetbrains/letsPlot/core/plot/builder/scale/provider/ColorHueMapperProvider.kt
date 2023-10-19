/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.HSL
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
) : HSLColorMapperProvider(
    naValue = naValue,
) {
    private val hueRange = DoubleSpan(hueRange.lowerEnd + startHue, hueRange.upperEnd + startHue)

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        val n = discreteTransform.effectiveDomain.size
        val s = chroma / 100.0
        val l = luminance / 100.0

        // if full circle prevent first and last colors to be the same
        val hueRange = hueRange.takeUnless { it.length % 360 < 1.0 }
            ?: DoubleSpan(hueRange.lowerEnd, hueRange.upperEnd - 360.0 / n)

        val step = hueRange.length / (n - 1)

        val from = HSL(hueRange.lowerEnd, s, l)
        val to = HSL(hueRange.lowerEnd + step * (n - 1), s, l)

        return createDiscreteMapper(
            transformedDomain = discreteTransform.effectiveDomainTransformed,
            from = from.takeUnless { reversed } ?: to,
            to = to.takeUnless { reversed } ?: from
        )
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)

        val s = chroma / 100.0
        val l = luminance / 100.0

        val from = HSL(hueRange.lowerEnd, s, l)
        val to = HSL(hueRange.upperEnd, s, l)

        return createContinuousMapper(
            domain = domain,
            from = from.takeUnless { reversed } ?: to,
            to = to.takeUnless { reversed } ?: from
        )
    }

    companion object {
        // hsl palette defaults from seaborn
        const val DEF_CHROMA = 65.0
        const val DEF_LUMINANCE = 60.0
        const val DEF_START_HUE = 0.0
        val DEF_HUE_RANGE = DoubleSpan(15.0, 375.0)
    }
}