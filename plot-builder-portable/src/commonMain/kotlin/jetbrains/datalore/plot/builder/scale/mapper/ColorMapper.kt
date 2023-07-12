/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.mapper

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.commons.values.HSV
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import kotlin.math.abs

object ColorMapper {
    val NA_VALUE = Color.GRAY

    // https://ggplot2.tidyverse.org/current/scale_gradient.html
    val DEF_GRADIENT_LOW = Color.parseHex("#132B43")
    val DEF_GRADIENT_HIGH = Color.parseHex("#56B1F7")

    fun gradientDefault(domain: DoubleSpan): (Double?) -> Color {
        return gradient(
            domain,
            DEF_GRADIENT_LOW,
            DEF_GRADIENT_HIGH,
            NA_VALUE,
            alpha = 1.0
        )
    }

    /**
     * Alpha channel [0..1] (0 - transparent and 1 - opaque).
     */
    fun gradient(
        domain: DoubleSpan,
        low: Color,
        high: Color,
        naColor: Color,
        alpha: Double = 1.0
    ): (Double?) -> Color {
        return gradientHSV(
            domain,
            Colors.hsvFromRgb(low),
            Colors.hsvFromRgb(high),
            true,
            naColor, alpha
        )
    }

    fun gradientHSV(
        domain: DoubleSpan,
        lowHSV: HSV,
        highHSV: HSV,
        autoHueDirection: Boolean,
        naColor: Color,
        alpha: Double = 1.0
    ): (Double?) -> Color {

        var lowHue = lowHSV.h
        var highHue = highHSV.h

        val lowS = lowHSV.s
        val highS = highHSV.s

        // No hue if saturation is near zero
        if (lowS < 0.0001) {
            lowHue = highHue
        }
        if (highS < 0.0001) {
            highHue = lowHue
        }

        if (autoHueDirection) {
            val dH = abs(highHue - lowHue)
            if (dH > 180) {
                if (highHue >= lowHue) {
                    lowHue += 360.0
                } else {
                    highHue += 360.0
                }
            }
        }

        val mapperH = Mappers.linear(domain, lowHue, highHue, null)
        val mapperS = Mappers.linear(domain, lowS, highS, null)
        val mapperV = Mappers.linear(domain, lowHSV.v, highHSV.v, null)

        return { input ->
            if (input == null || !domain.contains(input)) {
                naColor
            } else {
                val hue = mapperH(input)!! % 360
                val H = if (hue >= 0) hue else 360 + hue
                val S = mapperS(input)!!
                val V = mapperV(input)!!
                Colors.rgbFromHsv(H, S, V, alpha = alpha)
            }
        }
    }
}
