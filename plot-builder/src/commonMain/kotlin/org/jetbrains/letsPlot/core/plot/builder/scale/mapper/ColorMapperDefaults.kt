/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.mapper

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.color.GradientUtil.gradient

object ColorMapperDefaults {
    val NA_VALUE = Color.GRAY

    object Gradient {
        // https://ggplot2.tidyverse.org/current/scale_gradient.html
        val DEF_LOW = Color.parseHex("#132B43")
        val DEF_HIGH = Color.parseHex("#56B1F7")
    }

    object Gradient2 {
        // https://ggplot2.tidyverse.org/current/scale_gradient.html
        val DEF_LOW = Color.parseHex("#964540") // muted("red")
        val DEF_MID = Color.WHITE
        val DEF_HIGH = Color.parseHex("#3B3D96") // muted("blue")
    }

    object Hue {
        // defaults from ggplot2
        const val DEF_CHROMA = 100.0
        const val DEF_LUMINANCE = 65.0
        const val DEF_START_HUE = 0.0
        val DEF_HUE_RANGE = DoubleSpan(15.0, 375.0)
    }

    object GreyscaleLightness {
        const val DEF_START = 0.2
        const val DEF_END = 0.8
    }

    fun gradientDefault(domain: DoubleSpan): (Double?) -> Color {
        return gradient(
            domain,
            Gradient.DEF_LOW,
            Gradient.DEF_HIGH,
            NA_VALUE,
            alpha = 1.0
        )
    }
}
