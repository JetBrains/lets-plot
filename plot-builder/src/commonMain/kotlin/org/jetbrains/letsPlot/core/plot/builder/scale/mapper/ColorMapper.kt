/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.mapper

import org.jetbrains.letsPlot.commons.colormodel.HCL
import org.jetbrains.letsPlot.commons.colormodel.hclFromRgb
import org.jetbrains.letsPlot.commons.colormodel.rgbFromHcl
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
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
        return gradientHCL(
            domain,
            hclFromRgb(low),
            hclFromRgb(high),
            naColor,
            alpha,
            autoHueDirection = true
        )
    }

    fun gradientHCL(
        domain: DoubleSpan,
        low: HCL,
        high: HCL,
        naColor: Color,
        alpha: Double = 1.0,
        autoHueDirection: Boolean = false
    ): (Double?) -> Color {
        var lowH = low.h
        var highH = high.h

        val lowC = low.c
        val highC = high.c

        // No hue if saturation is near zero
        if (lowC < 0.0001) {
            lowH = highH
        }
        if (highC < 0.0001) {
            highH = lowH
        }

        if (autoHueDirection) {
            val dH = abs(highH - lowH)
            if (dH > 180) {
                if (highH >= lowH) {
                    lowH += 360.0
                } else {
                    highH += 360.0
                }
            }
        }

        val mapperH = Mappers.linear(domain, lowH, highH, null)
        val mapperC = Mappers.linear(domain, lowC, highC, null)
        val mapperL = Mappers.linear(domain, low.l, high.l, null)

        return { input ->
            if (input == null || !domain.contains(input)) {
                naColor
            } else {
                val hue = mapperH(input)!! % 360
                val h = if (hue >= 0) hue else 360 + hue
                val c = mapperC(input)!!
                val l = mapperL(input)!!
                rgbFromHcl(HCL(h, c, l), alpha = alpha)
            }
        }
    }
}
