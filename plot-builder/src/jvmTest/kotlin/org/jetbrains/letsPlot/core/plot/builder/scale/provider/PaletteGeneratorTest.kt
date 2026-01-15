/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.builder.scale.PaletteGenerator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PaletteGeneratorTest {

    @Test
    fun `all providers handle n=1 correctly`() {
        val providers = listOf<PaletteGenerator>(
            ColorHueMapperProvider(DoubleSpan(0.0, 360.0), 100.0, 65.0, 0.0, false, Color.GRAY),
            ColorBrewerMapperProvider("seq", 0, 1.0, Color.GRAY),
            ColorGradientMapperProvider(Color.BLUE, Color.RED, Color.GRAY),
            ColorGradient2MapperProvider(Color.BLUE, Color.WHITE, Color.RED, 0.0, Color.GRAY),
            ColorGradientnMapperProvider(listOf(Color.BLUE, Color.RED), Color.GRAY),
            ColormapMapperProvider("viridis", 1.0, 0.0, 1.0, 1.0, Color.GRAY),
            GreyscaleLightnessMapperProvider(0.2, 0.8, Color.GRAY)
        )

        providers.forEach { provider ->
            val palette = provider.generatePalette(1)
            assertEquals(1, palette.size, "Provider ${provider::class.simpleName} failed for n=1")
            assertValidHexColor(palette.first())
        }
    }

    @Test
    fun `all providers handle large n correctly`() {
        val providers = listOf<PaletteGenerator>(
            ColorHueMapperProvider(DoubleSpan(0.0, 360.0), 100.0, 65.0, 0.0, false, Color.GRAY),
            ColorBrewerMapperProvider("seq", 0, 1.0, Color.GRAY),
            ColorGradientMapperProvider(Color.BLUE, Color.RED, Color.GRAY),
            ColorGradient2MapperProvider(Color.BLUE, Color.WHITE, Color.RED, 0.0, Color.GRAY),
            ColorGradientnMapperProvider(listOf(Color.BLUE, Color.RED), Color.GRAY),
            ColormapMapperProvider("viridis", 1.0, 0.0, 1.0, 1.0, Color.GRAY),
            GreyscaleLightnessMapperProvider(0.2, 0.8, Color.GRAY)
        )

        val largeN = 100
        providers.forEach { provider ->
            val palette = provider.generatePalette(largeN)
            assertEquals(largeN, palette.size, "Provider ${provider::class.simpleName} failed for n=$largeN")
            palette.forEach { assertValidHexColor(it) }
        }
    }

    private fun assertValidHexColor(hex: String) {
        assertTrue(hex.startsWith("#"), "Hex color should start with #: $hex")
        assertTrue(
            hex.length == 7 || hex.length == 9,
            "Hex color should be 7 or 9 characters long: $hex"
        )
        val hexDigits = hex.substring(1)
        assertTrue(
            hexDigits.all { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' },
            "Hex color should only contain hex digits: $hex"
        )
    }
}
