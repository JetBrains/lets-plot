/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import org.jetbrains.letsPlot.commons.colormodel.*
import kotlin.test.*

class ColorsTest {
    @Test
    fun namedColors() {
        assertTrue(Colors.isColorName("pink"))
        assertTrue(Colors.isColorName("pInk"))
        assertFalse(Colors.isColorName("unknown"))

        assertNotNull(Colors.forName("red"))
        assertNotNull(Colors.forName("rEd"))
    }

    @Test
    fun unknownColor() {
        assertFailsWith<IllegalArgumentException> {
            Colors.forName("unknown")
        }
    }

    @Test
    fun parseHex() {
        assertEquals(Color.RED, Colors.parseColor(Color.RED.toHexColor()))
    }

    @Test
    fun parseRGB() {
        assertEquals(Color.RED, Colors.parseColor("rgb(255,0,0)"))
    }

    @Test
    fun parseRGBA() {
        assertEquals(Color(0, 255, 0, 37), Colors.parseColor("rgba(0,255,0,0.145)"))
    }

    @Test
    fun parseColRGB() {
        assertEquals(Color.BLUE, Colors.parseColor("color(0,0,255)"))
    }

    @Test
    fun parseColRGBA() {
        assertEquals(Color(0, 0, 255, 37), Colors.parseColor("color(0,0,255,0.145)"))
    }

    @Test
    fun parseColName() {
        assertEquals(Color.MAGENTA, Colors.parseColor("magenta"))
    }

    @Test
    fun rgbFromHsv() {
        assertEquals(Color.BLACK, Colors.rgbFromHsv(0.0, 0.0, 0.0))
        assertEquals(Color.BLACK, Colors.rgbFromHsv(360.0, 0.0, 0.0))
        assertEquals(Color.WHITE, Colors.rgbFromHsv(0.0, 0.0, 1.0))
        assertEquals(Color.RED, Colors.rgbFromHsv(0.0, 1.0, 1.0))
        assertEquals(Color.GREEN, Colors.rgbFromHsv(120.0, 1.0, 1.0))
        assertEquals(Color.BLUE, Colors.rgbFromHsv(240.0, 1.0, 1.0))
        assertEquals(Color.YELLOW, Colors.rgbFromHsv(60.0, 1.0, 1.0), "YELLOW")
        assertEquals(Color.CYAN, Colors.rgbFromHsv(180.0, 1.0, 1.0), "CYAN")
        assertEquals(Color.MAGENTA, Colors.rgbFromHsv(300.0, 1.0, 1.0), "MAGENTA")
        assertEquals(Color(191, 191, 191), Colors.rgbFromHsv(0.0, 0.0, 0.75), "SILVER")
        assertEquals(Color(128, 128, 128), Colors.rgbFromHsv(0.0, 0.0, 0.5), "GRAY")
        assertEquals(Color(128, 0, 0), Colors.rgbFromHsv(0.0, 1.0, 0.5), "MAROON")
        assertEquals(Color(128, 128, 0), Colors.rgbFromHsv(60.0, 1.0, 0.5), "OLIVE")
        assertEquals(Color(0, 128, 0), Colors.rgbFromHsv(120.0, 1.0, 0.5), "GREEN")
        assertEquals(Color(128, 0, 128), Colors.rgbFromHsv(300.0, 1.0, 0.5), "PURPLE")
        assertEquals(Color(0, 128, 128), Colors.rgbFromHsv(180.0, 1.0, 0.5), "TEAL")
        assertEquals(Color(0, 0, 128), Colors.rgbFromHsv(240.0, 1.0, 0.5), "NAVY")
    }

    @Test
    fun hsl() {
        fun assertColors(rgb: Color, hsl: HSL) {
            assertEquals(hsl, hslFromRgb(rgb))
            assertEquals(rgb, rgbFromHsl(hsl))
        }

        assertColors(Color(0, 0, 0), HSL(0.0, 0.0, 0.0)) // black
        assertColors(Color(255, 255, 255), HSL(0.0, 0.0, 1.0)) // white
        assertColors(Color(255, 0, 0), HSL(0.0, 1.0, 0.5)) // red
        assertColors(Color(0, 255, 0), HSL(120.0, 1.0, 0.5)) // lime
        assertColors(Color(0, 0, 255), HSL(240.0, 1.0, 0.5)) // blue
        assertColors(Color(255, 255, 0), HSL(60.0, 1.0, 0.5)) // yellow
        assertColors(Color(0, 255, 255), HSL(180.0, 1.0, 0.5)) // cyan
        assertColors(Color(255, 0, 255), HSL(300.0, 1.0, 0.5)) // magenta
        assertColors(Color(191, 191, 191), HSL(0.0, 0.0, 0.75)) // silver
        assertColors(Color(128, 128, 128), HSL(0.0, 0.0, 0.5)) // gray
        assertColors(Color(128, 0, 0), HSL(0.0, 1.0, 0.25)) // maroon
        assertColors(Color(128, 128, 0), HSL(60.0, 1.0, 0.25)) // olive
        assertColors(Color(0, 128, 0), HSL(120.0, 1.0, 0.25)) // green
        assertColors(Color(128, 0, 128), HSL(300.0, 1.0, 0.25)) // purple
        assertColors(Color(0, 128, 128), HSL(180.0, 1.0, 0.25)) // teal
        assertColors(Color(0, 0, 128), HSL(240.0, 1.0, 0.25)) // navy
    }

    @Test
    fun hcl() {
        fun assertHclToRgb(hcl: HCL, hexRgb: String) {
            assertEquals(Color.parseHex(hexRgb), rgbFromHcl(hcl))
        }

        // ggplot2 palette
        assertHclToRgb(HCL(15.0, 100.0, 65.0), "#F8766D")
        assertHclToRgb(HCL(75.0, 100.0, 65.0), "#B79F00")
        assertHclToRgb(HCL(135.0, 100.0, 65.0), "#00BA38")
        assertHclToRgb(HCL(195.0, 100.0, 65.0), "#00BFC4")
        assertHclToRgb(HCL(255.0, 100.0, 65.0), "#619CFF")
        assertHclToRgb(HCL(315.0, 100.0, 65.0), "#F564E3")
    }
}

