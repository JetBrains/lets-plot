/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import org.jetbrains.letsPlot.commons.values.Bitmap
import kotlin.test.Test
import kotlin.test.assertEquals

class DataImageJvmTest {

    @Test
    fun selfGeneratedArgb6x2() {
        val bitmap = Bitmap(
            width = 6,
            height = 2,
            argbInts = intArrayOf(
                0xFFFF0000.toInt(), // Red
                0xFF00FF00.toInt(), // Green
                0xFF0000FF.toInt(), // Blue
                0x80FF0000.toInt(), // Semi-transparent Red
                0x8000FF00.toInt(), // Semi-transparent Green
                0x800000FF.toInt(),  // Semi-transparent Blue

                0x800000FF.toInt(),  // Semi-transparent Blue
                0x8000FF00.toInt(), // Semi-transparent Green
                0x80FF0000.toInt(), // Semi-transparent Red
                0xFF0000FF.toInt(), // Blue
                0xFF00FF00.toInt(), // Green
                0xFFFF0000.toInt(), // Red
            )
        )
        val img = Png.encodeDataImage(bitmap)

        val decodedBitmap = Png.decodeDataImage(img)

        assertEquals(6, decodedBitmap.width)
        assertEquals(2, decodedBitmap.height)

        val pixels = decodedBitmap.argbInts
        assertEquals(0xFFFF0000.toInt(), pixels[0])
        assertEquals(0xFF00FF00.toInt(), pixels[1])
        assertEquals(0xFF0000FF.toInt(), pixels[2])
        assertEquals(0x80FF0000.toInt(), pixels[3])
        assertEquals(0x8000FF00.toInt(), pixels[4])
        assertEquals(0x800000FF.toInt(), pixels[5])

        assertEquals(0x800000FF.toInt(), pixels[6])
        assertEquals(0x8000FF00.toInt(), pixels[7])
        assertEquals(0x80FF0000.toInt(), pixels[8])
        assertEquals(0xFF0000FF.toInt(), pixels[9])
        assertEquals(0xFF00FF00.toInt(), pixels[10])
        assertEquals(0xFFFF0000.toInt(), pixels[11])
    }

    @Test
    fun testImageDataUrl() {
        val bitmap = Png.decodeDataImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAAGUlEQVR4nGP4z8DwHwwZ/oPJBiDRAOQ0AACOEwp4fPM+tgAAAABJRU5ErkJggg==")
        assertEquals(3, bitmap.width)
        assertEquals(2, bitmap.height)

        assertEquals(0xFFFF0000.toInt(), bitmap.argbInts[0])
        assertEquals(0xFF00FF00.toInt(), bitmap.argbInts[1])
        assertEquals(0xFF0000FF.toInt(), bitmap.argbInts[2])
        assertEquals(0x80FF0000.toInt(), bitmap.argbInts[3])
        assertEquals(0x8000FF00.toInt(), bitmap.argbInts[4])
        assertEquals(0x800000FF.toInt(), bitmap.argbInts[5])
    }
}