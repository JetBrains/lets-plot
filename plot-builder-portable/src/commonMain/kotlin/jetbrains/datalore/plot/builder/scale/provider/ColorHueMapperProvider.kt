/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.HSV
import org.jetbrains.letsPlot.core.plot.base.ContinuousTransform
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import kotlin.math.max
import kotlin.math.min

class ColorHueMapperProvider(
    hueRange: List<Double>?,
    chroma: Double?,
    luminance: Double?,
    startHue: Double?,
    direction: Double?,
    naValue: Color
) : HSVColorMapperProvider(naValue) {

    private val myFromHSV: HSV
    private val myToHSV: HSV
    private val myHSVIntervals: List<Pair<HSV, HSV>>

    init {
        @Suppress("NAME_SHADOWING")
        val hueRange = normalizeHueRange(hueRange)
        val clockwise = direction == null || direction != -1.0

        val fromHue = if (clockwise) hueRange.lowerEnd else hueRange.upperEnd
        val toHue = if (clockwise) hueRange.upperEnd else hueRange.lowerEnd

        @Suppress("NAME_SHADOWING")
        val startHue = startHue ?: DEF_START_HUE

        val hueIntervals = if (hueRange.contains(startHue) &&
            startHue - hueRange.lowerEnd > 1 && hueRange.upperEnd - startHue > 1
        ) {
            listOf(
                startHue to toHue,
                fromHue to startHue
            )
        } else {
            listOf(
                fromHue to toHue
            )
        }

        val saturation = (if (chroma != null) chroma % 100 else DEF_SATURATION) / 100
        val value = (if (luminance != null) luminance % 100 else DEF_VALUE) / 100

        // for continuous data
        myHSVIntervals = hueIntervals.map { HSV(it.first, saturation, value) to HSV(it.second, saturation, value) }

        // for discrete data: 'startHue' is ignored (intervals not used)
        myFromHSV = HSV(fromHue, saturation, value)
        myToHSV = HSV(toHue, saturation, value)
    }

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        return createDiscreteMapper(discreteTransform.effectiveDomainTransformed, myFromHSV, myToHSV)
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
        return createContinuousMapper(domain, myHSVIntervals)
    }

    companion object {
        private const val DEF_SATURATION = 50.0
        private const val DEF_VALUE = 90.0
        private const val DEF_START_HUE = 0.0
        private val DEF_HUE_RANGE = DoubleSpan(15.0, 375.0) // ggplot2 (R): c(0, 360) + 15

        val DEFAULT = ColorHueMapperProvider(
            null,
            null,
            null,
            null,
            null,
            Color.GRAY
        )

        private fun normalizeHueRange(hueRange: List<Double>?): DoubleSpan {
            return if (hueRange == null || hueRange.size != 2) {
                DEF_HUE_RANGE
            } else {
                DoubleSpan(
                    min(hueRange[0], hueRange[1]),
                    max(hueRange[0], hueRange[1])
                )
            }
        }
    }
}
