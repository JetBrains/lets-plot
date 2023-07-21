/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.common.twkb

object VarInt {
    private const val VARINT_EXPECT_NEXT_PART: Int = 7

    internal fun readVarInt(readByte: () -> Int): Int {
        val i = readVarUInt(readByte)
        return decodeZigZag(i)
    }

    fun readVarUInt(readByte: () -> Int): Int {
        var i = 0
        var shift = 0
        val mask = 127
        var part: Int

        do {
            part = readByte()
            i = i or (part and mask shl shift)
            shift += VARINT_EXPECT_NEXT_PART
        } while (part and (1 shl VARINT_EXPECT_NEXT_PART) != 0)
        return i
    }

    internal fun decodeZigZag(i: Int): Int {
        return i shr 1 xor -(i and 1)
    }
}
