/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

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
        assertEquals(Color(0,255,0,37), Colors.parseColor("rgba(0,255,0,0.145)"))
    }

    @Test
    fun parseColRGB() {
        assertEquals(Color.BLUE, Colors.parseColor("color(0,0,255)"))
    }

    @Test
    fun parseColRGBA() {
        assertEquals(Color(0,0,255,37), Colors.parseColor("color(0,0,255,0.145)"))
    }

    @Test
    fun parseColName() {
        assertEquals(Color.MAGENTA, Colors.parseColor("magenta"))
    }
}
