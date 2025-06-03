/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.values

class Bitmap(
    val width: Int,
    val height: Int,
    val argbInts: IntArray
) {
    fun rgbaBytes(): ByteArray {
        val rgba = ByteArray(width * height * 4)
        var i = 0
        argbInts.forEach { pixel ->
            val a = (pixel shr 24) and 0xFF
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF
            rgba[i++] = r.toByte()
            rgba[i++] = g.toByte()
            rgba[i++] = b.toByte()
            rgba[i++] = a.toByte()
        }
        return rgba
    }

    companion object {
        fun fromRGBABytes(w: Int, h: Int, rgba: ByteArray): Bitmap {
            val argbValues = IntArray(w * h)
            var i = 0
            for (y in 0 until h) {
                for (x in 0 until w) {
                    val r = rgba[i++].toInt() and 0xFF
                    val g = rgba[i++].toInt() and 0xFF
                    val b = rgba[i++].toInt() and 0xFF
                    val a = rgba[i++].toInt() and 0xFF
                    argbValues[y * w + x] = (a shl 24) or (r shl 16) or (g shl 8) or b
                }
            }

            return Bitmap(w, h, argbValues)
        }
    }
}
