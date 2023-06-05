/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.util.pngj

import kotlinx.cinterop.*
import pngj.CRC32
import pngj.NativeDeflater

internal actual object Zip {

    actual fun compressBytes(
        ori: ByteArray,
        offset: Int,
        len: Int,
        compress: Boolean
    ): ByteArray {
        return if (compress) NativeDeflater().deflateByteArray(ori) else ori
    }

    actual fun newDeflater(deflaterCompLevel: Int): Deflater {
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

    actual fun crc32(): Checksum {
        return CRC32()
    }

    actual val IS_BYTE_ORDER_BIG_ENDIAN: Boolean
        get() = memScoped {
            val array = allocArray<ByteVar>(2)
            array[0] = 1
            array[1] = 0
            val value = array.reinterpret<ShortVar>()[0]
            value == 1.toShort()
        }
}
