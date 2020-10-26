/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.mapper

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.base.values.HSV
import jetbrains.datalore.plot.base.scale.Mappers
import kotlin.math.abs

object ColorMapper {
    val NA_VALUE = Color.GRAY

    // https://ggplot2.tidyverse.org/current/scale_gradient.html
    val DEF_GRADIENT_LOW = Color.parseHex("#132B43")
    val DEF_GRADIENT_HIGH = Color.parseHex("#56B1F7")

    fun gradientDefault(domain: ClosedRange<Double>): (Double?) -> Color {
        return gradient(
            domain,
            DEF_GRADIENT_LOW,
            DEF_GRADIENT_HIGH,
            NA_VALUE
        )
    }

    fun gradient(domain: ClosedRange<Double>, low: Color, high: Color, naColor: Color): (Double?) -> Color {
        return gradientHSV(
            domain,
            Colors.hsvFromRgb(low),
            Colors.hsvFromRgb(high),
            true,
            naColor
        )
    }

    /**
     * @deprecated
     */
    fun gradientHSV(
        domain: ClosedRange<Double>,
        lowHSV: DoubleArray,
        highHSV: DoubleArray,
        autoHueDirection: Boolean,
        naColor: Color
    ): (Double?) -> Color {
        return gradientHSV(
            domain,
            HSV(lowHSV[0], lowHSV[1], lowHSV[2]),
            HSV(highHSV[0], highHSV[1], highHSV[2]),
            autoHueDirection,
            naColor
        )
    }

    fun gradientHSV(
        domain: ClosedRange<Double>,
        lowHSV: HSV,
        highHSV: HSV,
        autoHueDirection: Boolean,
        naColor: Color
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

        val mapperH = Mappers.linear(domain, lowHue, highHue, Double.NaN)
        val mapperS = Mappers.linear(domain, lowS, highS, Double.NaN)
        val mapperV = Mappers.linear(domain, lowHSV.v, highHSV.v, Double.NaN)

        return { input ->
            if (input == null || !domain.contains(input)) {
                naColor
            } else {
                val hue = mapperH(input) % 360
                val H = if (hue >= 0) hue else 360 + hue
                val S = mapperS(input)
                val V = mapperV(input)
                Colors.rgbFromHsv(H, S, V)
            }
        }
    }
}
