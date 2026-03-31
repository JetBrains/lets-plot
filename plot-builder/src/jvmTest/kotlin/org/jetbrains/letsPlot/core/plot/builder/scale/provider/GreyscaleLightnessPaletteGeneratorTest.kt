/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GreyscaleLightnessPaletteGeneratorTest {

    @Test
    fun `generates greyscale gradient`() {
        val provider = GreyscaleLightnessMapperProvider(
            start = 0.0,
            end = 1.0,
            naValue = Color.GRAY
        )

        val palette = provider.generatePalette(3)

        // First should be dark (close to black)
        val first = Color.parseHex(palette.first())
        assertTrue(
            first.red < 5 && first.green < 5 && first.blue < 5,
            "First color should be dark, got: ${palette.first()}"
        )

        // Last should be light (close to white)
        val last = Color.parseHex(palette.last())
        assertTrue(
            last.red > 250 && last.green > 250 && last.blue > 250,
            "Last color should be light, got: ${palette.last()}"
        )

        // All should be greyscale (R=G=B)
        palette.forEach { hex ->
            val color = Color.parseHex(hex)
            assertEquals(color.red, color.green, "Not greyscale: $hex")
            assertEquals(color.green, color.blue, "Not greyscale: $hex")
        }
    }
}
