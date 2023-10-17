/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.mapper

import demoAndTestShared.assertEquals
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.commons.values.HSL
import kotlin.test.Test

class ColorMapperTest {
    @Test
    fun gradientHSV_Hue() {
        val lowHue = 0.0
        val highHue = 360.0
        val saturation = 0.5
        val value = 0.9

        val f = ColorMapper.gradientHSL(
            DoubleSpan(0.0, 1.0),
            HSL(lowHue, saturation, value),
            HSL(highHue, saturation, value),
            Color.GRAY
        )

//        val hue0 = Colors.hsvFromRgb(f(0.0))[0]
//        val hue1 = Colors.hsvFromRgb(f(0.5))[0]
//        val hue2 = Colors.hsvFromRgb(f(1.0))[0]
        val hue0 = Colors.hslFromRgb(f(0.0)).h
        val hue1 = Colors.hslFromRgb(f(0.5)).h
        val hue2 = Colors.hslFromRgb(f(1.0)).h

        val accuracy = .001
        assertEquals(0.0, hue0, accuracy)
        assertEquals(180.0, hue1, accuracy)
        assertEquals(0.0, hue2, accuracy)
    }
}