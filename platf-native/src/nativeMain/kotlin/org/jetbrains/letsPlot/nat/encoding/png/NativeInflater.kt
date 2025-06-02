/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.png


import kotlinx.cinterop.*
import platform.posix.memset
import platform.zlib.*

class NativeInflater : Inflater {
    private var input: ByteArray = byteArrayOf()
    private var finished = false
    private var needsInput = true

    override fun setInput(data: ByteArray, off: Int, len: Int) {
        input = data.copyOfRange(off, off + len)
        needsInput = false
    }

    override fun needsInput(): Boolean = needsInput

    override fun inflate(output: ByteArray, offset: Int, length: Int): Int {
        memScoped {
            val stream = alloc<z_stream>()
            memset(stream.ptr, 0, sizeOf<z_stream>().convert())

            val inputPtr = input.refTo(0).getPointer(this)
            val outputPtr = output.refTo(offset).getPointer(this)

            stream.next_in = inputPtr.reinterpret()
            stream.avail_in = input.size.toUInt()
            stream.next_out = outputPtr.reinterpret()
            stream.avail_out = length.toUInt()

            val result = inflateInit2(stream.ptr, 15)
            check(result == Z_OK) { "inflateInit2 failed: $result" }

            val inflateResult = inflate(stream.ptr, Z_FINISH)
            check(inflateResult == Z_OK || inflateResult == Z_STREAM_END) {
                "inflate failed: $inflateResult"
            }

            val outputBytes = length - stream.avail_out.toInt()

            inflateEnd(stream.ptr)
            finished = true
            needsInput = true

            return outputBytes
        }
    }

    override fun finished(): Boolean = finished

    override fun reset() {
        finished = false
        needsInput = true
        input = byteArrayOf()
    }

    override fun end() {
        reset()
    }
}

interface Inflater {
    fun setInput(data: ByteArray, off: Int, len: Int)
    fun needsInput(): Boolean
    fun inflate(output: ByteArray, offset: Int, length: Int): Int
    fun finished(): Boolean
    fun reset()
    fun end()
}
