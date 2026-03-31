/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorGradient2PaletteGeneratorTest {

    @Test
    fun `midpoint affects palette`() {

        // When generating a palette, the midpoint is in the range -1..1

        // Default midpoint (0.0 - middle of the range)
        run {
            val provider = ColorGradient2MapperProvider(
                low = Color.BLUE,
                mid = Color.WHITE,
                high = Color.RED,
                midpoint = null,
                naValue = Color.GRAY
            )

            val palette = provider.generatePalette(5)

            // The middle color should be white (midpoint color)
            assertEquals(Color.WHITE.toHexColor(), palette[2], "Middle color should be white (midpoint)")
        }

        // Midpoint = -1.0 (bottom of the range)
        run {
            val provider = ColorGradient2MapperProvider(
                low = Color.BLUE,
                mid = Color.WHITE,
                high = Color.RED,
                midpoint = -1.0,
                naValue = Color.GRAY
            )
            val palette = provider.generatePalette(5)
            assertEquals(Color.WHITE.toHexColor(), palette[0], "Bottom color should be white for midpoint=-1.0")
        }

        // Midpoint = 1.0 (top of the range)
        run {
            val provider = ColorGradient2MapperProvider(
                low = Color.BLUE,
                mid = Color.WHITE,
                high = Color.RED,
                midpoint = 1.0,
                naValue = Color.GRAY
            )
            val palette = provider.generatePalette(5)
            assertEquals(Color.WHITE.toHexColor(), palette[4], "Top color should be white for midpoint=1.0")
        }
    }
}
