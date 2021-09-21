/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.aes

import jetbrains.datalore.base.values.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorOptionConverterTest {
    private fun assertRed(`val`: Any) {
        assertColor(Color.RED, `val`)
    }

    private fun assertGreen(`val`: Any) {
        assertColor(Color.GREEN, `val`)
    }

    private fun assertBlue(`val`: Any) {
        assertColor(Color.BLUE, `val`)
    }

    private fun assertBlack(`val`: Any) {
        assertColor(Color.BLACK, `val`)
    }

    private fun assertColor(expected: Color, `val`: Any) {
        assertEquals(expected, ColorOptionConverter().apply(`val`))
    }

    @Test
    fun convertHEX() {
        assertGreen("#00ff00")
    }

    @Test
    fun convertRGB() {
        assertGreen("rgb(0,255,0)")
    }

    @Test
    fun convertName() {
        assertGreen("green")
    }

    @Test
    fun convertDoubleRGBBitPack() {
        assertRed(0xff0000)
        assertGreen(0x00ff00)
        assertBlue(0x0000ff)
        assertBlack(0.0)
    }

    @Test
    fun convertDoubleRGBBitPackNeg() {
        assertRed(-0xff0000)
        assertGreen(-0x00ff00)
        assertBlue(-0x0000ff)
        assertBlack(-0.0)
    }
}
