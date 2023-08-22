/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.png

open class OutputPngStream {
    private val buffer = mutableListOf<Byte>()

    open fun write(data: ByteArray, off: Int, len: Int) {
        (off until off + len).forEach { i ->
            buffer.add(data[i])
        }
    }

    open fun write(b: Int) {
        buffer.add(b.toByte())
    }

    open fun write(data: ByteArray) {
        write(data, 0, data.size)
    }

    fun close() {
        onClose()
    }

    open fun onClose() {}

    val byteArray get() = buffer.toByteArray()
}

internal abstract class AbstractOutputPngStream {
    val stream = mutableListOf<Byte>()

    open fun write(b: Int) {
        stream.add(b.toByte())
    }

    open fun write(data: ByteArray, offset: Int, len: Int) {
        data.asSequence().drop(offset).take(len).forEach { stream.add(it) }
    }

    open fun write(data: ByteArray) {
        data.forEach { stream.add(it) }
    }

    open fun close() {
    }
}
