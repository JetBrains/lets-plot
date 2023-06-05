/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.util.pngj

internal expect object Zip {
    fun compressBytes(ori: ByteArray, offset: Int, len: Int, compress: Boolean): ByteArray
    fun newDeflater(deflaterCompLevel: Int = DEFLATER_DEFAULT_STRATEGY): Deflater
    fun crc32(): Checksum

    val IS_BYTE_ORDER_BIG_ENDIAN: Boolean
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
