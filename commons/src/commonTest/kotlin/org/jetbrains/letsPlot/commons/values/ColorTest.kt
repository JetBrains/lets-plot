/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColorTest {

    @Test
    fun parseHex() {
        assertEquals(Color.RED, Color.parseHex(Color.RED.toHexColor()))
    }

    @Test
    fun parseHexRRGGBBAA() {
        assertEquals(Color(0, 255, 0, 37), Color.parseHex("#00FF0025"))
    }

    @Test
    fun parseHexRGBA() {
        assertEquals(Color(0x11, 0x22, 0x33, 0x44), Color.parseHex("#1234"))
    }

    @Test
    fun parseHexRGB() {
        assertEquals(Color(0x11, 0x22, 0x33), Color.parseHex("#123"))
    }

    @Test
    fun toHexColorWithAlpha() {
        assertEquals("#11223344", Color(0x11, 0x22, 0x33, 0x44).toHexColor())
    }

    @Test
    fun toHexColorNoAlpha() {
        assertEquals("#112233", Color(0x11, 0x22, 0x33, 0x44).toHexColorNoAlpha())
    }

    @Test
    fun changeOpacityRoundsToNearestByte() {
        assertEquals(128, Color.RED.changeOpacity(0.5).alpha)
    }

    @Test
    fun parseRGB() {
        assertEquals(Color.RED, Color.parseRGB("rgb(255,0,0)"))
    }

    @Test
    fun parseRGBA() {
        assertEquals(Color.RED, Color.parseRGB("rgba(255,0,0,1.0)"))
    }

    @Test
    fun rgbaRequiresAlpha() {
        val e = assertFailsWith<IllegalArgumentException> {
            Color.parseRGB("rgba(220, 240, 255)")
        }

        assertEquals("RGBA color format requires exactly 4 components: rgba(220, 240, 255)", e.message)
    }

    @Test
    fun rgbRejectsExtraAlpha() {
        val e = assertFailsWith<IllegalArgumentException> {
            Color.parseRGB("rgb(220, 240, 255, 0.5)")
        }

        assertEquals("RGB color format requires exactly 3 components: rgb(220, 240, 255, 0.5)", e.message)
    }

    @Test
    fun parseColRGB() {
        assertEquals(Color.BLUE, Color.parseRGB("color(0,0,255)"))
    }

    @Test
    fun parseColRGBA() {
        assertEquals(Color.BLUE, Color.parseRGB("color(0,0,255,1.0)"))
    }

    @Test
    fun colorRejectsWrongComponentCount() {
        val e = assertFailsWith<IllegalArgumentException> {
            Color.parseRGB("color(0,0)")
        }

        assertEquals("'color()' format requires 3 or 4 components: color(0,0)", e.message)
    }

    @Test
    fun parseRgbWithSpaces() {
        assertEquals(Color.RED, Color.parseRGB("rgb(255, 0, 0)"))
    }

    @Test
    fun noLastNumber() {
        assertFailsWith<IllegalArgumentException> {
            Color.parseRGB("rgb(255, 0, )")
        }

    }

    @Test
    fun unknownPrefix() {
        assertFailsWith<IllegalArgumentException> {
            Color.parseRGB("rbg(255, 0, )")
        }
    }
}
