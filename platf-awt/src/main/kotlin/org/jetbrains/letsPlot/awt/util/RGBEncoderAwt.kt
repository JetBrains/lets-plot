/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.util

import org.jetbrains.letsPlot.commons.encoding.RGBEncoder

class RGBEncoderAwt : RGBEncoder {
    override fun toDataUrl(width: Int, height: Int, argbValues: IntArray): String {

        //return ImageDataUrl.encodePngToDataUrl(width, height, intArrayToByteArray(argbValues))
        return RgbToDataUrl.png(width, height, argbValues)
    }

    private fun intArrayToByteArray(argbValues: IntArray): ByteArray {
        val byteArray = ByteArray(argbValues.size * 4)
        for (i in argbValues.indices) {
            val pixel = argbValues[i]
            byteArray[i * 4] = ((pixel shr 16) and 0xFF).toByte()  // Red component
            byteArray[i * 4 + 1] = ((pixel shr 8) and 0xFF).toByte()   // Green component
            byteArray[i * 4 + 2] = (pixel and 0xFF).toByte()           // Blue component
            byteArray[i * 4 + 3] = ((pixel shr 24) and 0xFF).toByte()  // Alpha component
        }
        return byteArray
    }
}