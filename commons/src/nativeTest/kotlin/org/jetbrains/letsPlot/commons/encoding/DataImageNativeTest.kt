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

        val raster = DataImage.decode(img)

        assertEquals(6, raster.width)
        assertEquals(2, raster.height)
        assertEquals(0xFFFF0000.toInt(), raster.argbIntArray[0])
        assertEquals(0xFF00FF00.toInt(), raster.argbIntArray[1])
        assertEquals(0xFF0000FF.toInt(), raster.argbIntArray[2])
        assertEquals(0x80FF0000.toInt(), raster.argbIntArray[3])
        assertEquals(0x8000FF00.toInt(), raster.argbIntArray[4])
        assertEquals(0x800000FF.toInt(), raster.argbIntArray[5])

        assertEquals(0x800000FF.toInt(), raster.argbIntArray[6])
        assertEquals(0x8000FF00.toInt(), raster.argbIntArray[7])
        assertEquals(0x80FF0000.toInt(), raster.argbIntArray[8])
        assertEquals(0xFF0000FF.toInt(), raster.argbIntArray[9])
        assertEquals(0xFF00FF00.toInt(), raster.argbIntArray[10])
        assertEquals(0xFFFF0000.toInt(), raster.argbIntArray[11])
    }

}