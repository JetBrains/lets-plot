/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Copyright (c) 2009-2012, Hernán J. González.
 * Licensed under the Apache License, Version 2.0.
 *
 * The original PNGJ library is written in Java and can be found here: [PNGJ](https://github.com/leonbloy/pngj).
 */

package org.jetbrains.letsPlot.nat.encoding.png

import kotlinx.cinterop.*
import platform.zlib.*
import platform.posix.memcpy
import platform.posix.memset
import platform.posix.size_tVar
import platform.zlib.uLong


class NativeDeflater() : Deflater {
    private var data: ByteArray = byteArrayOf()
    private var finished: Boolean = false
    private var finish: Int = Z_FINISH
    private var strategy: Int = Z_DEFAULT_STRATEGY
    private var crc: Int = 0
    private fun Int.touLong(): uLong {
        val ul: uLong = 0U
        var a: Any = ul
        if (a is UInt){
            a = this.toUInt()
        } else if(a is ULong){
            a = this.toULong()
        } else {
            error("Incompatible type uLong $a")
        }
        return a as uLong
    }

    fun deflateByteArray(input: ByteArray): ByteArray {
        memScoped {
            // Allocate output buffer with maximum possible size
            val maxOutputSize = compressBound(input.size.touLong()).toInt()
            val output = ByteArray(maxOutputSize)

            // Prepare input and output pointers
            val inputPtr = input.refTo(0).getPointer(this)
            val outputPtr = output.refTo(0).getPointer(this)
            val outputSizePtr = alloc<size_tVar>()

            // Initialize the deflate stream
            val stream = alloc<z_stream>()
            // Clear the structure
            memset(stream.ptr, 0, sizeOf<z_stream>().convert())

            deflateInit(stream.ptr, Z_DEFAULT_COMPRESSION)
            stream.next_in = inputPtr.reinterpret()
            stream.avail_in = input.size.toUInt()
            stream.next_out = outputPtr.reinterpret()
            stream.avail_out = output.size.toUInt()

            // Compress the data
            crc = deflate(stream.ptr, finish)
            check(crc != Z_STREAM_ERROR) { "Failed to compress data" }
            // Update output buffer size
            outputSizePtr.value = (output.size - stream.avail_out.toInt()).toULong()
            // Finalize the deflate stream
            check(deflateEnd(stream.ptr) == Z_OK) { "Failed to finalize compression" }
            finished = true
            // Create a new byte array with the compressed data
            return output.copyOf(outputSizePtr.value.toInt())
        }
    }

    override fun deflate(buf: ByteArray, off: Int, n: Int): Int {
        memScoped {
            // Allocate output buffer with maximum possible size
            val maxOutputSize = compressBound(data.size.touLong()).toInt()
            val output = ByteArray(maxOutputSize)

            // Prepare input and output pointers
            val inputPtr = data.refTo(0).getPointer(this)
            val outputPtr = output.refTo(0).getPointer(this)
            val outputSizePtr = alloc<size_tVar>()

            // Initialize the deflate stream
            val stream = alloc<z_stream>()
            // Clear the structure
            memset(stream.ptr, 0, sizeOf<z_stream>().convert())

            deflateInit2(stream.ptr, Z_DEFAULT_COMPRESSION, Z_DEFLATED, 15, 8, strategy)
            stream.next_in = inputPtr.reinterpret()
            stream.avail_in = data.size.toUInt()
            stream.next_out = outputPtr.reinterpret()
            stream.avail_out = output.size.toUInt()

            // Compress the data
            check(deflate(stream.ptr, finish) != Z_STREAM_ERROR) { "Failed to compress data" }
            // Update output buffer size
            outputSizePtr.value = (output.size - stream.avail_out.toInt()).toULong()
            // Finalize the deflate stream
            check(deflateEnd(stream.ptr) == Z_OK) { "Failed to finalize compression" }
            finished = true
            // Create a new byte array with the compressed data
            memcpy(buf.refTo(off), output.refTo(0), stream.total_out.convert())
            return stream.total_out.convert()
        }
    }

    override fun setStrategy(deflaterStrategy: Int) {
        this.strategy = deflaterStrategy
    }

    override fun finished(): Boolean {
        return finished
    }

    override fun setInput(data: ByteArray, off: Int, len: Int) {
        this.data = this.data + data.copyOfRange(off, off + len)
    }

    override fun needsInput(): Boolean {
        return this.data.isEmpty()
    }

    override fun finish() {
        finish = Z_FINISH
    }

    override fun end() {
        reset()
    }

    override fun reset() {
        finished = false
        data = byteArrayOf()
    }
}