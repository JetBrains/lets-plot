/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config.conversion

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.spec.conversion.ColorOptionConverter
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorOptionConverterTest {
    private fun assertRed(`val`: Any) {
        assertColor(Color.RED, `val`)
    }

    private fun assertLime(`val`: Any) {
        assertColor(Color.LIME, `val`)
    }

    private fun assertBlue(`val`: Any) {
        assertColor(Color.BLUE, `val`)
    }

    private fun assertBlack(`val`: Any) {
        assertColor(Color.BLACK, `val`)
    }

    private fun assertColor(expected: Color, value: Any) {
        assertEquals(expected, ColorOptionConverter.demoAndTest.apply(value))
    }

    @Test
    fun convertHEX() {
        assertLime("#00ff00")
    }

    @Test
    fun convertRGB() {
        assertLime("rgb(0,255,0)")
    }

    @Test
    fun convertName() {
        assertLime("lime")
    }

    @Test
    fun convertDoubleRGBBitPack() {
        assertRed(0xff0000)
        assertLime(0x00ff00)
        assertBlue(0x0000ff)
        assertBlack(0.0)
    }

    @Test
    fun convertDoubleRGBBitPackNeg() {
        assertRed(-0xff0000)
        assertLime(-0x00ff00)
        assertBlue(-0x0000ff)
        assertBlack(-0.0)
    }
}
