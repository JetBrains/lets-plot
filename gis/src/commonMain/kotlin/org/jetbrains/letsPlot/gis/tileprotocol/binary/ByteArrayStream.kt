/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.binary

class ByteArrayStream (private val bytes: ByteArray) {
    private val count = bytes.size
    private var position = 0

    fun available(): Int {
        return count - position
    }

    fun read(): Byte =
        if (position < count)
            bytes[position++]
        else
            throw IllegalStateException("Array size exceeded.")

    fun read(len: Int): ByteArray {
        if (position >= count)
            throw IllegalStateException("Array size exceeded.")

        if (len > available())
            throw IllegalStateException("Expected to read $len bytea, but read ${available()}")

        if (len <= 0)
            return ByteArray(0)

        val startIndex = position
        position += len

        return bytes.copyOfRange(startIndex, position)
    }
}