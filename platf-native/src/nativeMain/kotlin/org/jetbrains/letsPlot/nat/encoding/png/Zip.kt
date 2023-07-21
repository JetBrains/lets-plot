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

internal object Zip {

    fun compressBytes(
        ori: ByteArray,
        offset: Int,
        len: Int,
        compress: Boolean
    ): ByteArray {
        return if (compress) NativeDeflater().deflateByteArray(ori) else ori
    }

    fun newDeflater(deflaterCompLevel: Int = DEFLATER_DEFAULT_STRATEGY): Deflater {
        return object : Deflater {
            private val deflater = NativeDeflater()
            override fun setStrategy(deflaterStrategy: Int) {
                deflater.setStrategy(deflaterStrategy)
            }

            override fun finished(): Boolean {
                return deflater.finished()
            }

            override fun setInput(data: ByteArray, off: Int, len: Int) {
                deflater.setInput(data, off, len)
            }

            override fun needsInput(): Boolean {
                return deflater.needsInput()
            }

            override fun deflate(buf: ByteArray, off: Int, n: Int): Int {
                return deflater.deflate(buf, off, n)
            }

            override fun finish() {
                deflater.finish()
            }

            override fun end() {
                deflater.end()
            }

            override fun reset() {
                deflater.reset()
            }
        }
    }

    fun crc32(): Checksum {
        return CRC32()
    }

    val IS_BYTE_ORDER_BIG_ENDIAN: Boolean
        get() = memScoped {
            val array = allocArray<ByteVar>(2)
            array[0] = 1
            array[1] = 0
            val value = array.reinterpret<ShortVar>()[0]
            value == 1.toShort()
        }
}


internal interface Deflater {
    fun setStrategy(deflaterStrategy: Int)
    fun finished(): Boolean
    fun setInput(data: ByteArray, off: Int, len: Int)
    fun needsInput(): Boolean
    fun deflate(buf: ByteArray, off: Int, n: Int): Int
    fun finish()
    fun end()
    fun reset()
}

internal interface Checksum {
    fun update(b: ByteArray, off: Int = 0, len: Int = b.size)
    val value: Long
    fun reset()
}

internal const val DEFLATER_DEFAULT_STRATEGY: Int = 0
