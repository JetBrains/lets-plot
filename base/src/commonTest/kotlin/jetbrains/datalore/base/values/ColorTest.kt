package jetbrains.datalore.base.values

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColorTest {

    @Test
    fun parseHex() {
        assertEquals(Color.RED, Color.parseHex(Color.RED.toHexColor()))
    }

    @Test
    fun parseRgba() {
        assertEquals(Color.RED, Color.parseColor("rgba(255,0,0,255)"))
    }

    @Test
    fun parseRgb() {
        assertEquals(Color.RED, Color.parseColor("rgb(255,0,0)"))
    }

    @Test
    fun parseColor() {
        assertEquals(Color.BLUE, Color.parseColor("color(0,0,255,255)"))
    }

    @Test
    fun parseColorRgb() {
        assertEquals(Color.BLUE, Color.parseColor("color(0,0,255)"))
    }

    @Test
    fun parseRgbWithSpaces() {
        assertEquals(Color.RED, Color.parseColor("rgb(255, 0, 0)"))
    }

    @Test
    fun noLastNumber() {
        assertFailsWith<IllegalArgumentException> {
            Color.parseColor("rgb(255, 0, )")
        }

    }

    @Test
    fun unknownPrefix() {
        assertFailsWith<IllegalArgumentException> {
            Color.parseColor("rbg(255, 0, )")
        }
    }
}