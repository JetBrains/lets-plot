/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BitmapTest {

    @Test
    fun simple() {
        val bitmap = Bitmap.fromRGBABytes(2, 2, byteArrayOf(
            0, 0, 0, 255.toByte(), // Pixel (0, 0) - black
            255.toByte(), 255.toByte(), 255.toByte(), 255.toByte(), // Pixel (1, 0) - white
            255.toByte(), 0, 0, 127.toByte(), // Pixel (0, 1) - semi-transparent red
            0, 255.toByte(), 0, 127.toByte() // Pixel (1, 1) - semi-transparent green
        ))

        assertEquals(2, bitmap.width)
        assertEquals(2, bitmap.height)
        assertEquals(Color(0, 0, 0, 255), bitmap.getPixel(0, 0)) // Black
        assertEquals(Color(255, 255, 255, 255), bitmap.getPixel(1, 0)) // White
        assertEquals(Color(255, 0, 0, 127), bitmap.getPixel(0, 1)) // Semi-transparent red
        assertEquals(Color(0, 255, 0, 127), bitmap.getPixel(1, 1)) // Semi-transparent green

        assertNull(bitmap.getPixel(-1, -1)) // Out of bounds
        assertNull(bitmap.getPixel(2, 2)) // Out of bounds
    }

    @Test
    fun zeroSize() {
        val bitmap = Bitmap.fromRGBABytes(0, 0, byteArrayOf())

        assertEquals(0, bitmap.width)
        assertEquals(0, bitmap.height)
        assertNull(bitmap.getPixel(0, 0)) // No pixels to get
    }
}