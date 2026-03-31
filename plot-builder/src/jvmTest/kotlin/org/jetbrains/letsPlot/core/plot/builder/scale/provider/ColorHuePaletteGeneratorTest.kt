/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale.provider

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorHuePaletteGeneratorTest {
    companion object {
        const val DEF_C = 100.0
        const val DEF_L = 65.0

        // all colors are in the HCL model with C=65, L=100
        // Generated with R:
        // grDevices::hcl(h, 100, 65)
        private val HCL_COLOR_0 = Color.parseHex("#FF6C91")
        private val HCL_COLOR_60 = Color.parseHex("#CD9600")
        private val HCL_COLOR_120 = Color.parseHex("#49B500")
        private val HCL_COLOR_180 = Color.parseHex("#00C1A9")
        private val HCL_COLOR_240 = Color.parseHex("#00A9FF")
        private val HCL_COLOR_300 = Color.parseHex("#E36EF6")
    }

    private fun createMapperProvider(
        hueRange: DoubleSpan,
        chroma: Double = DEF_C,
        luminance: Double = DEF_L,
        startHue: Double = 0.0,
        reversed: Boolean = false,
        naValue: Color = Color.GRAY
    ): ColorHueMapperProvider {
        return ColorHueMapperProvider(
            hueRange = hueRange,
            chroma = chroma,
            luminance = luminance,
            startHue = startHue,
            reversed = reversed,
            naValue = naValue
        )
    }

    @Test
    fun `full hue wheel`() {
        val provider = createMapperProvider(hueRange = DoubleSpan(0.0, 360.0))

        val palette = provider.generatePalette(6)

        assertEquals(6, palette.size)
        assertEquals(HCL_COLOR_0.toHexColor(), palette[0])
        assertEquals(HCL_COLOR_60.toHexColor(), palette[1])
        assertEquals(HCL_COLOR_120.toHexColor(), palette[2])
        assertEquals(HCL_COLOR_180.toHexColor(), palette[3])
        assertEquals(HCL_COLOR_240.toHexColor(), palette[4])
        assertEquals(HCL_COLOR_300.toHexColor(), palette[5])
    }

    @Test
    fun reversed() {
        val provider = createMapperProvider(hueRange = DoubleSpan(120.0, 480.0), reversed = true)

        val palette = provider.generatePalette(6)

        assertEquals(6, palette.size)
        assertEquals(HCL_COLOR_60.toHexColor(), palette[0])
        assertEquals(HCL_COLOR_0.toHexColor(), palette[1])
        assertEquals(HCL_COLOR_300.toHexColor(), palette[2])
        assertEquals(HCL_COLOR_240.toHexColor(), palette[3])
        assertEquals(HCL_COLOR_180.toHexColor(), palette[4])
        assertEquals(HCL_COLOR_120.toHexColor(), palette[5])
    }

    @Test
    fun `h_start works as offset`() {
        val provider = createMapperProvider(hueRange = DoubleSpan(120.0, 240.0), startHue = 60.0)

        val palette = provider.generatePalette(3)

        assertEquals(3, palette.size)
        assertEquals(HCL_COLOR_180.toHexColor(), palette[0])
        assertEquals(HCL_COLOR_240.toHexColor(), palette[1])
        assertEquals(HCL_COLOR_300.toHexColor(), palette[2])
    }

    @Test
    fun `respects reversed parameter`() {
        val provider = ColorHueMapperProvider(
            hueRange = DoubleSpan(0.0, 360.0),
            chroma = 100.0,
            luminance = 65.0,
            startHue = 0.0,
            reversed = false,
            naValue = Color.GRAY
        )

        val providerReversed = ColorHueMapperProvider(
            hueRange = DoubleSpan(0.0, 360.0),
            chroma = 100.0,
            luminance = 65.0,
            startHue = 0.0,
            reversed = true,
            naValue = Color.GRAY
        )

        val palette = provider.generatePalette(6)
        val paletteReversed = providerReversed.generatePalette(6)

        assertEquals(palette.reversed(), paletteReversed)
    }
}
