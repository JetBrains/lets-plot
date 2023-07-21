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

package org.jetbrains.letsPlot.nat.encoding.png.pixels

import org.jetbrains.letsPlot.nat.encoding.png.Deflater
import org.jetbrains.letsPlot.nat.encoding.png.IdatChunkWriter
import org.jetbrains.letsPlot.nat.encoding.png.PngjOutputException
import org.jetbrains.letsPlot.nat.encoding.png.Zip.newDeflater

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
        deflater.setInput(data, off, len)
        bytesIn += len
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

        if (deflater.finished() || isDone || isClosed) throw PngjOutputException("write beyond end of stream")
        val len: Int = deflater.deflate(buf, off, n)
        if (len > 0) {
            if (idatChunkWriter != null) idatChunkWriter!!.incrementOffset(len)
            bytesOut += len
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