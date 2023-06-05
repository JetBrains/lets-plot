/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.util.pngj.pixels

import org.jetbrains.letsPlot.util.pngj.Deflater
import org.jetbrains.letsPlot.util.pngj.IdatChunkWriter
import org.jetbrains.letsPlot.util.pngj.PngjOutputException
import org.jetbrains.letsPlot.util.pngj.Zip.newDeflater

/**
 * CompressorStream backed by a Deflater.
 *
 * Note that the Deflater is not disposed after done, you should either recycle
 * this with reset() or dispose it with close()
 *
 */
internal class CompressorStreamDeflater constructor(
    idatCw: IdatChunkWriter?,
    maxBlockLen: Int,
    totalLen: Long,
    def: Deflater? = null
) : CompressorStream(idatCw, maxBlockLen, totalLen) {
    private val deflater: Deflater
    // temporary storage of compressed bytes: only used if idatWriter is null
    private var buf1: ByteArray? = null
    private var deflaterIsOwn = true

    /**
     * if a deflater is passed, it must be already reset. It will not be
     * released on close
     */
    init {
        deflater = def ?: newDeflater()
        deflaterIsOwn = def == null
    }

    constructor(
        idatCw: IdatChunkWriter?, maxBlockLen: Int, totalLen: Long, deflaterCompLevel: Int,
        deflaterStrategy: Int
    ) : this(idatCw, maxBlockLen, totalLen, newDeflater(deflaterCompLevel)) {
        deflaterIsOwn = true
        deflater.setStrategy(deflaterStrategy)
    }

    override fun mywrite(data: ByteArray, off: Int, len: Int) {
        if (deflater.finished() || isDone || isClosed) throw PngjOutputException("write beyond end of stream")
        deflater.setInput(data, off, len)
        bytesIn += len
        while (!deflater.needsInput()) deflate()
    }

    private fun deflate() {
        val buf: ByteArray
        val off: Int
        val n: Int
        if (idatChunkWriter != null) {
            buf = idatChunkWriter!!.buf
            off = idatChunkWriter!!.offset
            n = idatChunkWriter!!.availLen

        } else {
            if (buf1 == null) buf1 = ByteArray(4096)
            buf = buf1!!
            off = 0
            n = buf1!!.size
        }
        val len: Int = deflater.deflate(buf, off, n)
        if (len > 0) {
            if (idatChunkWriter != null) idatChunkWriter!!.incrementOffset(len)
            //bytesOut += len
        }
    }

    /** automatically called when done  */
    override fun done() {
        if (isDone) return
        if (!deflater.finished()) {
            deflater.finish()
            while (!deflater.finished()) deflate()
        }
        isDone = true
        if (idatChunkWriter != null) idatChunkWriter!!.close()
    }

    override fun close() {
        done()
        try {
            if (deflaterIsOwn) {
                deflater.end()
            }
        } catch (e: Exception) {
            println(e)
        }
        super.close()
    }

    override fun reset() {
        deflater.reset()
        super.reset()
    }
}