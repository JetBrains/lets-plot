/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.common.testUtils

object HexParser {
    fun parseHex(hex: String): ByteArray {
        val bin = ByteArray(hex.length / 2)

        var i = 0
        val n = hex.length
        while (i < n) {
            bin[i / 2] = (parseHex(hex[i]) shl 4 or parseHex(hex[i + 1])).toByte()
            i += 2
        }

        return bin
    }

    private fun parseHex(ch: Char): Int {
        return when (ch) {
            in '0'..'9' -> (ch - '0')
            in 'A'..'Z' -> (ch - 'A' + 10)
            in 'a'..'z' -> (ch - 'a' + 10)
            else -> throw IllegalStateException()
        }
    }
}
