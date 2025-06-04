/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import kotlin.test.Test
import kotlin.test.assertEquals

class DataImageNativeTest {

    @Test
    fun selfGeneratedArgb6x2() {
        val img = DataImage.encode(width = 6, height = 2, rgba = intArrayOf(
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
        ))

        val bitmap = DataImage.decode(img)

        assertEquals(6, bitmap.width)
        assertEquals(2, bitmap.height)
        assertEquals(0xFFFF0000.toInt(), bitmap.argbInts[0])
        assertEquals(0xFF00FF00.toInt(), bitmap.argbInts[1])
        assertEquals(0xFF0000FF.toInt(), bitmap.argbInts[2])
        assertEquals(0x80FF0000.toInt(), bitmap.argbInts[3])
        assertEquals(0x8000FF00.toInt(), bitmap.argbInts[4])
        assertEquals(0x800000FF.toInt(), bitmap.argbInts[5])

        assertEquals(0x800000FF.toInt(), bitmap.argbInts[6])
        assertEquals(0x8000FF00.toInt(), bitmap.argbInts[7])
        assertEquals(0x80FF0000.toInt(), bitmap.argbInts[8])
        assertEquals(0xFF0000FF.toInt(), bitmap.argbInts[9])
        assertEquals(0xFF00FF00.toInt(), bitmap.argbInts[10])
        assertEquals(0xFFFF0000.toInt(), bitmap.argbInts[11])
    }
}
