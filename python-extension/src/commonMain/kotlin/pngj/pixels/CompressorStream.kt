/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

@file:Suppress("unused")
package org.jetbrains.letsPlot.util.pngj.pixels

import org.jetbrains.letsPlot.util.pngj.AbstractOutputPngStream
import org.jetbrains.letsPlot.util.pngj.IdatChunkWriter

/**
 * This is an OutputStream that compresses (via Deflater or a deflater-like
 * object), and optionally passes the compressed stream to another output
 * stream.
 *
 * It allows to compute in/out/ratio stats.
 *
 * It works as a stream (similar to DeflaterOutputStream), but it's peculiar in
 * that it expects that each writes has a fixed length (other lenghts are
 * accepted, but it's less efficient) and that the total amount of bytes is
 * known (so it can close itself, but it can also be closed on demand) In PNGJ
 * use, the block is typically a row (including filter byte).
 *
 * We use this to do the real compression (with Deflate) but also to compute
 * tentative estimators
 *
 * If not closed, it can be recicled via reset()
 *
 *
 */
internal abstract class CompressorStream(
    idatCw: IdatChunkWriter?,
    blockLen: Int,
    totalbytes: Long
) : AbstractOutputPngStream() {
    protected var idatChunkWriter: IdatChunkWriter?
    val blockLen: Int
    val totalbytes: Long
    var isClosed = false
    var isDone = false
        protected set

    protected var bytesIn: Long = 0
    //protected var bytesOut: Long = 0

    /**
     * raw (input) bytes. This should be called only when done
     */
    private var bytesRaw: Long = 0

    /**
     * compressed (out) bytes. This should be called only when done
     */
    private var bytesCompressed: Long = 0

    protected var block = -1

    /** optionally stores the first byte of each block (row)  */
    var firstBytes: ByteArray? = null
        private set

    private var storeFirstByte = false

    init {
        @Suppress("NAME_SHADOWING")
        var blockLen = blockLen
        @Suppress("NAME_SHADOWING")
        var totalbytes = totalbytes

        idatChunkWriter = idatCw
        if (blockLen < 0) blockLen = 4096
        if (totalbytes < 0) totalbytes = Long.MAX_VALUE
        if (blockLen < 1 || totalbytes < 1) error(" maxBlockLen or totalLen invalid")
        this.blockLen = blockLen
        this.totalbytes = totalbytes
    }

    /** Releases resources. Idempotent.  */
    override fun close() {
        done()
        if (idatChunkWriter != null) idatChunkWriter!!.close()
        isClosed = true
    }

    /**
     * Will be called automatically when the number of bytes reaches the total
     * expected Can be also be called from outside. This should set the flag
     * done=true
     */
    abstract fun done()
    override fun write(data: ByteArray) {
        write(data, 0, data.size)
    }

    override fun write(data: ByteArray, offset: Int, len: Int) {
        var off = offset
        @Suppress("NAME_SHADOWING")
        var len = len
        block++
        if (len <= blockLen) { // normal case
            mywrite(data, off, len)
            if (storeFirstByte && block < firstBytes!!.size) {
                firstBytes!![block] = data[off] // only makes sense in this case
            }
        }
        if (bytesRaw >= totalbytes) done()
    }

    /**
     * same as write, but guarantedd to not exceed blockLen The implementation
     * should update bytesOut and bytesInt but not check for totalBytes
     */
    abstract fun mywrite(data: ByteArray, off: Int, len: Int)

    /**
     * compressed/raw. This should be called only when done
     */
    val compressionRatio: Double
        get() = if (bytesCompressed == 0L) 1.0 else bytesCompressed / bytesRaw.toDouble()

    fun setStoreFirstByte(storeFirstByte: Boolean, nblocks: Int) {
        this.storeFirstByte = storeFirstByte
        if (this.storeFirstByte) {
            if (firstBytes == null || firstBytes!!.size < nblocks) firstBytes = ByteArray(nblocks)
        } else firstBytes = null
    }

    open fun reset() {
        done()
        bytesRaw = 0
        bytesCompressed = 0
        block = -1
        isDone = false
    }

    override fun write(b: Int) { // should not be used
        write(byteArrayOf(b.toByte()))
    }
}