/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ColorGradientPaletteGeneratorTest {

    @Test
    fun `gradient from low to high`() {
        val provider = ColorGradientMapperProvider(
            low = Color.BLUE,
            high = Color.RED,
            naValue = Color.GRAY
        )

        val palette = provider.generatePalette(3)
        assertEquals(Color.BLUE.toHexColor(), palette.first())
        assertEquals(Color.RED.toHexColor(), palette.last())

        // The middle color should be interpolated (purple-ish blend of blue and red)
        val middleColor = Color.parseHex(palette[1])
        assertTrue(middleColor.blue > 0 && middleColor.red > 0,
            "Middle color should be a blend of blue and red, got: ${palette[1]}")
    }
}
