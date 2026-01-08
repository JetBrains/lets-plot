/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.mapper

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.color.GradientUtil.gradient

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
}
