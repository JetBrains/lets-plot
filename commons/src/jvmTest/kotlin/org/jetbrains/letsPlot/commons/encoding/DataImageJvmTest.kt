/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.encoding

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataImageJvmTest {

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

    @Test
    fun testImageDataUrl() {
        val raster = DataImage.decode("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAACXBIWXMAAC4jAAAuIwF4pT92AAAAGklEQVQI1y3BoQEAAAjAIIKPe/ksgkiiBusddmUGgIjqhKcAAAAASUVORK5CYII=")
        assertEquals(3, raster.width)
        assertEquals(2, raster.height)

        assertEquals(0xFFFF0000.toInt(), raster.argbIntArray[0])
        assertEquals(0xFF00FF00.toInt(), raster.argbIntArray[1])
        assertEquals(0xFF0000FF.toInt(), raster.argbIntArray[2])
        assertEquals(0x80FF0000.toInt(), raster.argbIntArray[3])
        assertEquals(0x8000FF00.toInt(), raster.argbIntArray[4])
        assertEquals(0x800000FF.toInt(), raster.argbIntArray[5])
    }

    @Ignore // re-check pixels and update expected values
    @Test
    fun testZeroCompression() {
        val raster = DataImage.decode("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAMAAAACCAYAAACddGYaAAAACXBIWXMAAC4jAAAuIwF4pT92AAAAJUlEQVQIHQEaAOX/AP8AAP8A/wD/AAD//wQAAACBAAAAAAAAAAB2ZQaAGFqUUAAAAABJRU5ErkJggg==")
        assertEquals(3, raster.width)
        assertEquals(2, raster.height)
        assertTrue(0xFFFF0000 == raster.argbIntArray[0].toLong(), "Expected first pixel to be red, but was ${raster.argbIntArray[0].toUInt().toString(16)}")
        assertTrue(0xFF00FF00 == raster.argbIntArray[1].toLong(), "Expected second pixel to be green, but was ${raster.argbIntArray[1].toUInt().toString(16)}")
        assertTrue(0xFF0000FF == raster.argbIntArray[2].toLong(), "Expected third pixel to be blue, but was ${raster.argbIntArray[2].toUInt().toString(16)}")
    }

    @Ignore // re-check pixels and update expected values
    @Test
    fun rgba6x2(){
        val raster = DataImage.decode("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAYAAAACCAYAAAB7Xa1eAAAACXBIWXMAAC4jAAAuIwF4pT92AAAAJElEQVQI1zXGsQ0AMAjAsETq34jL0wlPNsgAI9z7kxYkRJr7B2bADIfWTpmCAAAAAElFTkSuQmCC")
        assertEquals(6, raster.width)
        assertEquals(2, raster.height)

        assertEquals(0xFFFF0000.toInt(), raster.argbIntArray[0])
        assertEquals(0xFF00FF00.toInt(), raster.argbIntArray[1])
        assertEquals(0xFF0000FF.toInt(), raster.argbIntArray[2])
        assertEquals(0x80FF0000.toInt(), raster.argbIntArray[3])
        assertEquals(0x8000FF00.toInt(), raster.argbIntArray[4])
        assertEquals(0x800000FF.toInt(), raster.argbIntArray[5])
    }

}