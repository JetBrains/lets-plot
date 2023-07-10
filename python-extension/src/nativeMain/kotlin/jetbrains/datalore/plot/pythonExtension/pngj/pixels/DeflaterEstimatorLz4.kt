/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")

package jetbrains.datalore.plot.pythonExtension.pngj.pixels

import jetbrains.datalore.plot.pythonExtension.pngj.Zip.IS_BYTE_ORDER_BIG_ENDIAN
import kotlin.math.max

/**
 * This estimator actually uses the LZ4 compression algorithm, and hopes that
 * it's well correlated with Deflater. It's about 3 to 4 times faster than
 * Deflater.
 *
 * This is a modified heavily trimmed version of the
 * net.jpountz.lz4.LZ4JavaSafeCompressor class plus some methods from other
 * classes from LZ4 Java library: https://github.com/jpountz/lz4-java ,
 * originally licensed under the Apache License 2.0
 */
internal class DeflaterEstimatorLz4
/**
 * This object is stateless, it's thread safe and can be reused
 */
{
    /**
     * Estimates the length of the compressed bytes, as compressed by Lz4
     * WARNING: if larger than LZ4_64K_LIMIT it cuts it in fragments
     *
     * WARNING: if some part of the input is discarded, this should return the
     * proportional (so that returnValue/srcLen=compressionRatio)
     *
     * @param src
     * @param srcOff
     * @param srcLen
     * @return length of the compressed bytes
     */

    fun compressEstim(src: ByteArray, srcOff: Int = 0, srcLen: Int = src.size): Int {
        @Suppress("NAME_SHADOWING")
        var srcOff = srcOff
        if (srcLen < 10) return srcLen // too small
        var stride = LZ4_64K_LIMIT - 1
        val segments = (srcLen + stride - 1) / stride
        stride = srcLen / segments
        if (stride >= LZ4_64K_LIMIT - 1 || stride * segments > srcLen || segments < 1 || stride < 1) error("?? $srcLen")
        var bytesIn = 0
        var bytesOut = 0
        var len = srcLen
        while (len > 0) {
            if (len > stride) len = stride
            bytesOut += compress64k(src, srcOff, len)
            srcOff += len
            bytesIn += len
            len = srcLen - bytesIn
        }
        val ratio = bytesOut / bytesIn.toDouble()
        return if (bytesIn == srcLen) bytesOut else (ratio * srcLen + 0.5).toInt()
    }

    companion object {
        private const val MEMORY_USAGE = 14
        private const val NOT_COMPRESSIBLE_DETECTION_LEVEL = 6
        private const val MIN_MATCH = 4
        private const val HASH_LOG = MEMORY_USAGE - 2
        const val HASH_TABLE_SIZE = 1 shl HASH_LOG
        private val SKIP_STRENGTH: Int = max(NOT_COMPRESSIBLE_DETECTION_LEVEL, 2)
        private const val COPY_LENGTH = 8
        private const val LAST_LITERALS = 5
        private const val MF_LIMIT = COPY_LENGTH + MIN_MATCH
        private const val MIN_LENGTH = MF_LIMIT + 1
        const val MAX_DISTANCE = 1 shl 16
        private const val ML_BITS = 4
        private const val ML_MASK = (1 shl ML_BITS) - 1
        private const val RUN_BITS = 8 - ML_BITS
        private const val RUN_MASK = (1 shl RUN_BITS) - 1
        const val LZ4_64K_LIMIT = (1 shl 16) + (MF_LIMIT - 1)
        private const val HASH_LOG_64K = HASH_LOG + 1
        private const val HASH_TABLE_SIZE_64K = 1 shl HASH_LOG_64K
        private const val HASH_LOG_HC = 15
        const val HASH_TABLE_SIZE_HC = 1 shl HASH_LOG_HC
        const val OPTIMAL_ML = ML_MASK - 1 + MIN_MATCH
        fun compress64k(src: ByteArray, srcOff: Int, srcLen: Int): Int {
            val srcEnd = srcOff + srcLen
            val srcLimit = srcEnd - LAST_LITERALS
            val mflimit = srcEnd - MF_LIMIT
            var sOff = srcOff
            var dOff = 0
            var anchor = sOff
            if (srcLen >= MIN_LENGTH) {
                val hashTable = ShortArray(HASH_TABLE_SIZE_64K)
                ++sOff
                main@ while (true) {

                    // find a match
                    var forwardOff = sOff
                    var ref: Int
                    var findMatchAttempts = (1 shl SKIP_STRENGTH) + 3
                    do {
                        sOff = forwardOff
                        forwardOff += findMatchAttempts++ ushr SKIP_STRENGTH
                        if (forwardOff > mflimit) {
                            break@main
                        }
                        val h = hash64k(readInt(src, sOff))
                        ref = srcOff + readShort(hashTable, h)
                        writeShort(hashTable, h, sOff - srcOff)
                    } while (!readIntEquals(src, ref, sOff))

                    // catch up
                    val excess = commonBytesBackward(src, ref, sOff, srcOff, anchor)
                    sOff -= excess
                    ref -= excess
                    // sequence == refsequence
                    val runLen = sOff - anchor
                    dOff++
                    if (runLen >= RUN_MASK) {
                        if (runLen > RUN_MASK) dOff += (runLen - RUN_MASK) / 0xFF
                        dOff++
                    }
                    dOff += runLen
                    while (true) {
                        // encode offset
                        dOff += 2
                        // count nb matches
                        sOff += MIN_MATCH
                        ref += MIN_MATCH
                        val matchLen = commonBytes(src, ref, sOff, srcLimit)
                        sOff += matchLen
                        // encode match len
                        if (matchLen >= ML_MASK) {
                            if (matchLen >= ML_MASK + 0xFF) dOff += (matchLen - ML_MASK) / 0xFF
                            dOff++
                        }
                        // test end of chunk
                        if (sOff > mflimit) {
                            anchor = sOff
                            break@main
                        }
                        // fill table
                        writeShort(hashTable, hash64k(readInt(src, sOff - 2)), sOff - 2 - srcOff)
                        // test next position
                        val h = hash64k(readInt(src, sOff))
                        ref = srcOff + readShort(hashTable, h)
                        writeShort(hashTable, h, sOff - srcOff)
                        if (!readIntEquals(src, sOff, ref)) {
                            break
                        }
                        dOff++
                    }
                    // prepare next loop
                    anchor = sOff++
                }
            }
            val runLen = srcEnd - anchor
            if (runLen >= RUN_MASK + 0xFF) {
                dOff += (runLen - RUN_MASK) / 0xFF
            }
            dOff++
            dOff += runLen
            return dOff
        }

        fun maxCompressedLength(length: Int): Int {
            if (length < 0) {
                error("length must be >= 0, got $length")
            }
            return length + length / 255 + 16
        }

        fun hash(i: Int): Int {
            return i * -1640531535 ushr MIN_MATCH * 8 - HASH_LOG
        }

        private fun hash64k(i: Int): Int {
            return i * -1640531535 ushr MIN_MATCH * 8 - HASH_LOG_64K
        }

        fun readShortLittleEndian(buf: ByteArray, i: Int): Int {
            return buf[i].toInt() and 0xFF or (buf[i + 1].toInt() and 0xFF shl 8)
        }

        private fun readIntEquals(buf: ByteArray, i: Int, j: Int): Boolean {
            return buf[i] == buf[j] && buf[i + 1] == buf[j + 1] && buf[i + 2] == buf[j + 2] && buf[i + 3] == buf[j + 3]
        }

        private fun commonBytes(b: ByteArray, o1: Int, o2: Int, limit: Int): Int {
            @Suppress("NAME_SHADOWING")
            var o1 = o1
            @Suppress("NAME_SHADOWING")
            var o2 = o2
            var count = 0
            while (o2 < limit && b[o1++] == b[o2++]) {
                ++count
            }
            return count
        }

        private fun commonBytesBackward(b: ByteArray, o1: Int, o2: Int, l1: Int, l2: Int): Int {
            @Suppress("NAME_SHADOWING")
            var o1 = o1
            @Suppress("NAME_SHADOWING")
            var o2 = o2
            var count = 0
            while (o1 > l1 && o2 > l2 && b[--o1] == b[--o2]) {
                ++count
            }
            return count
        }

        private fun readShort(buf: ShortArray, off: Int): Int {
            return buf[off].toInt() and 0xFFFF
        }

        fun readByte(buf: ByteArray, i: Int): Byte {
            return buf[i]
        }

        private fun checkRange(buf: ByteArray, off: Int) {
            if (off < 0 || off >= buf.size) {
                throw IndexOutOfBoundsException(off.toString())
            }
        }

        fun checkRange(buf: ByteArray, off: Int, len: Int) {
            checkLength(len)
            if (len > 0) {
                checkRange(buf, off)
                checkRange(buf, off + len - 1)
            }
        }

        private fun checkLength(len: Int) {
            if (len < 0) {
                error("lengths must be >= 0")
            }
        }

        private fun readIntBE(buf: ByteArray, i: Int): Int {
            return buf[i].toInt() and 0xFF shl 24 or (buf[i + 1].toInt() and 0xFF shl 16) or (buf[i + 2].toInt() and 0xFF shl 8) or (buf[i + 3].toInt() and 0xFF)
        }

        private fun readIntLE(buf: ByteArray, i: Int): Int {
            return buf[i].toInt() and 0xFF or (buf[i + 1].toInt() and 0xFF shl 8) or (buf[i + 2].toInt() and 0xFF shl 16) or (buf[i + 3].toInt() and 0xFF shl 24)
        }

        private fun readInt(buf: ByteArray, i: Int): Int {
            return if (IS_BYTE_ORDER_BIG_ENDIAN) {
                readIntBE(buf, i)
            } else {
                readIntLE(buf, i)
            }
        }

        private fun writeShort(buf: ShortArray, off: Int, v: Int) {
            buf[off] = v.toShort()
        }
    }
}