/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj

import jetbrains.datalore.plot.pythonExtension.pngj.chunks.ChunkHelper
import jetbrains.datalore.plot.pythonExtension.pngj.chunks.ChunkRaw

/**
 * Outputs a sequence of IDAT-like chunk, that is filled progressively until the
 * max chunk length is reached (or until flush())
 */
internal class IdatChunkWriter constructor(private val outputStream: OutputPngStream, maxChunkLength: Int = 0) {
    private val maxChunkLen: Int

    /**
     * You can write directly to this buffer, using [.getOffset] and
     * [.getAvailLen]. You should call [.incrementOffset]
     * inmediately after.
     */
    var buf: ByteArray
        private set
    var offset = 0
        private set
    var availLen: Int
        private set
    private var totalBytesWriten: Long = 0 // including header+crc
    private var chunksWriten = 0

    private val chunkId: ByteArray
        get() = ChunkHelper.b_IDAT

    /**
     * Writes a chhunk if there is more than minLenToWrite.
     *
     * This is normally called internally, but can be called explicitly to force
     * flush.
     */
    private fun flush() {
        if (offset > 0 && offset >= minLenToWrite()) {
            val c = ChunkRaw(offset, chunkId, false)
            c.data = buf
            c.writeChunk(outputStream)
            totalBytesWriten += c.len + 12
            chunksWriten++
            offset = 0
            availLen = maxChunkLen
            postReset()
        }
    }

    /** triggers an flush+reset if appropiate  */
    fun incrementOffset(n: Int) {
        offset += n
        availLen -= n
        if (availLen < 0) throw PngjOutputException("Anomalous situation")
        if (availLen == 0) {
            flush()
        }
    }

    /**
     * this should rarely be used, the normal way (to avoid double copying) is
     * to get the buffer and write directly to it
     */
    fun write(b: ByteArray, o: Int, len: Int) {
        @Suppress("NAME_SHADOWING")
        var o = o
        @Suppress("NAME_SHADOWING")
        var len = len
        while (len > 0) {
            val n = if (len <= availLen) len else availLen
            arraycopy(b, o, buf, offset, n)
            incrementOffset(n)
            len -= n
            o += n
        }
    }

    /** this will be called after reset  */
    private fun postReset() {
        // fdat could override this (and minLenToWrite) to add a prefix
    }

    private fun minLenToWrite(): Int {
        return 1
    }

    fun close() {
        flush()
        offset = 0
    }

    companion object {
        private const val MAX_LEN_DEFAULT = 32768 // 32K rather arbitrary - data only
    }

    init {
        maxChunkLen = if (maxChunkLength > 0) maxChunkLength else MAX_LEN_DEFAULT
        buf = ByteArray(maxChunkLen)
        availLen = maxChunkLen - offset
        postReset()
    }
}