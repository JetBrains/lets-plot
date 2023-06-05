/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.util.pngj

import kotlin.math.min

class InputPngStream(
    private val data: ByteArray
) {
    private var i = 0
    private val available get() = data.size - i

    fun read(): Int {
        return data[i++].toInt() and 0xFF
    }

    fun read(b: ByteArray, off: Int, len: Int): Int {
        var read = 0
        val end = min(len, available)
        while (read < end) {
            b[off + read++] = read().toByte()
        }

        return read
    }

    fun skip(len: Long): Long {
        return min(available, len.toInt()).also { i += it }.toLong()
    }

    fun read(outBuffer: ByteArray): Int {
        return read(outBuffer, 0, outBuffer.size)
    }

    fun close() {

    }
}

internal abstract class AbstractInputPngStream(
    val stream: InputPngStream
) {
    open fun read(): Int {
        return stream.read()
    }

    open fun read(b: ByteArray, off: Int, len: Int): Int {
        return stream.read(b, off, len)
    }

    open fun skip(len: Long): Long {
        return stream.skip(len)
    }

    open fun read(outBuffer: ByteArray): Int {
        return stream.read(outBuffer)
    }

    open fun close() {
        stream.close()
    }
}
