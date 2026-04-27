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
    fun parseRGB() {
        assertEquals(Color.RED, Color.parseRGB("rgb(255,0,0)"))
    }

    @Test
    fun parseRGBA() {
        assertEquals(Color.RED, Color.parseRGB("rgba(255,0,0,1.0)"))
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