/*
 * Copyright (c) 2019. JetBrains s.r.o.
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