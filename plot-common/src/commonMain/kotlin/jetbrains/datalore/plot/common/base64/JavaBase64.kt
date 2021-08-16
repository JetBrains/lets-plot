/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.base64

/**
 * Used to replace guava's BaseEncoding
 *
 * ToDo: use `common` Base64
 */
object JavaBase64 {

    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    private val toInt = IntArray(128)

    init {
        for (i in ALPHABET.indices) {
            toInt[ALPHABET[i].code] = i
        }
    }

    fun decode(s: String): ByteArray {
        val delta = if (s.endsWith("==")) 2 else if (s.endsWith("=")) 1 else 0
        val buffer = ByteArray(s.length * 3 / 4 - delta)
        val mask = 0xFF
        var index = 0
        var i = 0
        while (i < s.length) {
            val c0 = toInt[s[i].code]
            val c1 = toInt[s[i + 1].code]
            buffer[index++] = (c0 shl 2 or (c1 shr 4) and mask).toByte()
            if (index >= buffer.size) {
                return buffer
            }
            val c2 = toInt[s[i + 2].code]
            buffer[index++] = (c1 shl 4 or (c2 shr 2) and mask).toByte()
            if (index >= buffer.size) {
                return buffer
            }
            val c3 = toInt[s[i + 3].code]
            buffer[index++] = (c2 shl 6 or c3 and mask).toByte()
            i += 4
        }
        return buffer
    }
}
