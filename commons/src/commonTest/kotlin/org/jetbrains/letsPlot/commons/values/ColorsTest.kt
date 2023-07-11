/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import kotlin.math.abs
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
    fun hsvFromRgb() {
        assertEquals(HSV(0.0, 0.0, 0.0), Colors.hsvFromRgb(Color.BLACK))
        assertEquals(HSV(0.0, 0.0, 1.0), Colors.hsvFromRgb(Color.WHITE))
        assertEquals(HSV(0.0, 1.0, 1.0), Colors.hsvFromRgb(Color.RED))
        assertEquals(HSV(120.0, 1.0, 1.0), Colors.hsvFromRgb(Color.GREEN))
        assertEquals(HSV(240.0, 1.0, 1.0), Colors.hsvFromRgb(Color.BLUE))
        assertEquals(HSV(60.0, 1.0, 1.0), Colors.hsvFromRgb(Color.YELLOW), "YELLOW")
        assertEquals(HSV(180.0, 1.0, 1.0), Colors.hsvFromRgb(Color.CYAN), "CYAN")
        assertEquals(HSV(300.0, 1.0, 1.0), Colors.hsvFromRgb(Color.MAGENTA), "MAGENTA")
        assertEquals(HSV(0.0, 0.0, 0.75), Colors.hsvFromRgb(Color(191, 191, 191)), "SILVER")
        assertEquals(HSV(0.0, 0.0, 0.5), Colors.hsvFromRgb(Color(127, 127, 127)), "GRAY")
        assertEquals(HSV(0.0, 1.0, 0.5), Colors.hsvFromRgb(Color(127, 0, 0)), "MAROON")
        assertEquals(HSV(60.0, 1.0, 0.5), Colors.hsvFromRgb(Color(127, 127, 0)), "OLIVE")
        assertEquals(HSV(120.0, 1.0, 0.5), Colors.hsvFromRgb(Color(0, 127, 0)), "GREEN")
        assertEquals(HSV(300.0, 1.0, 0.5), Colors.hsvFromRgb(Color(127, 0, 127)), "PURPLE")
        assertEquals(HSV(180.0, 1.0, 0.5), Colors.hsvFromRgb(Color(0, 127, 127)), "TEAL")
        assertEquals(HSV(240.0, 1.0, 0.5), Colors.hsvFromRgb(Color(0, 0, 127)), "NAVY")
    }

    private fun assertContentEquals(expected: DoubleArray, actual: DoubleArray, message: String = "") {
        val allEquals = expected.zip(actual).all { it -> abs(it.first - it.second) < 1.0e-2 }
        if (!allEquals) {
            throw AssertionError(
                "$message expected:${expected.joinToString(",", "[", "]")} " +
                        "but was ${actual.joinToString(",", "[", "]")}}"
            )
        }
    }
}
