/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.assertj.core.api.Assertions
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorGradientnPaletteGeneratorTest {

    @Test
    fun `exact colors at all positions`() {
        val provider = ColorGradientnMapperProvider(
            listOf(Color.BLUE, Color.YELLOW, Color.ORANGE, Color.RED),
            Color.GRAY
        )

        val palette = provider.generatePalette(4)

        assertEquals(4, palette.size)
        assertEquals(Color.BLUE.toHexColor(), palette[0])
        assertEquals(Color.YELLOW.toHexColor(), palette[1])
        assertEquals(Color.ORANGE.toHexColor(), palette[2])
        assertEquals(Color.RED.toHexColor(), palette[3])
    }

    @Test
    fun `intermediate values interpolated`() {
        val provider = ColorGradientnMapperProvider(listOf(Color.BLACK, Color.WHITE), Color.GRAY)

        val palette = provider.generatePalette(3)

        assertEquals(3, palette.size)
        assertEquals(Color.BLACK.toHexColor(), palette[0])
        assertEquals(Color.WHITE.toHexColor(), palette[2])

        // The middle should be gray
        assertEquals("#777777", palette[1])
    }
}
