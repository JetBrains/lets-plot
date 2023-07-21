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

package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.nat.encoding.png.*

/**
 * IHDR chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11IHDR
 *
 *
 * This is a special critical Chunk.
 */
internal class PngChunkIHDR(info: ImageInfo?) : PngChunkSingle(ID, info) {
    var cols = 0
    var rows = 0
    private var bitspc = 0
    private var colormodel = 0
    private var compmeth = 0
    private var filmeth = 0
    var interlaced = 0

    // http://www.w3.org/TR/PNG/#11IHDR
    //
    init { // argument is normally null here, if not null is used to fill the fields
        if (info != null) fillFromInfo(info)
    }

    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.NA

    override fun createRawChunk(): ChunkRaw {
        val c = ChunkRaw(13, ChunkHelper.b_IHDR, true)
        var offset = 0
        PngHelperInternal.writeInt4tobytes(cols, c.data, offset)
        offset += 4
        PngHelperInternal.writeInt4tobytes(rows, c.data, offset)
        offset += 4
        c.data!![offset++] = bitspc.toByte()
        c.data!![offset++] = colormodel.toByte()
        c.data!![offset++] = compmeth.toByte()
        c.data!![offset++] = filmeth.toByte()
        c.data!![offset] = interlaced.toByte()
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 13) throw PngjException("Bad IDHR len " + chunk.len)
        val st: InputPngStream = chunk.asByteStream
        cols = PngHelperInternal.readInt4(st)
        rows = PngHelperInternal.readInt4(st)
        // bit depth: number of bits per channel
        bitspc = PngHelperInternal.readByte(st)
        colormodel = PngHelperInternal.readByte(st)
        compmeth = PngHelperInternal.readByte(st)
        filmeth = PngHelperInternal.readByte(st)
        interlaced = PngHelperInternal.readByte(st)
    }

    fun isInterlaced(): Boolean {
        return interlaced == 1
    }

    private fun fillFromInfo(info: ImageInfo) {
        cols = info.cols
        rows = info.rows
        bitspc = imgInfo.bitDepth
        var colormodel = 0
        if (info.alpha) colormodel += 0x04
        if (info.indexed) colormodel += 0x01
        if (!info.greyscale) colormodel += 0x02
        this.colormodel = colormodel
        compmeth = 0 // compression method 0=deflate
        filmeth = 0 // filter method (0)
        interlaced = 0 // we never interlace
    }

    /** throws PngInputException if unexpected values  */
    fun createImageInfo(): ImageInfo {
        check()
        val alpha = colormodel and 0x04 != 0
        val palette = colormodel and 0x01 != 0
        val grayscale = colormodel == 0 || colormodel == 4
        // creates ImgInfo and imgLine, and allocates buffers
        return ImageInfo(cols, rows, bitspc, alpha, grayscale, palette)
    }

    fun check() {
        if (cols < 1 || rows < 1 || compmeth != 0 || filmeth != 0) throw PngjInputException("bad IHDR: col/row/compmethod/filmethod invalid")
        if (bitspc != 1 && bitspc != 2 && bitspc != 4 && bitspc != 8 && bitspc != 16) throw PngjInputException("bad IHDR: bitdepth invalid")
        if (interlaced < 0 || interlaced > 1) throw PngjInputException("bad IHDR: interlace invalid")
        when (colormodel) {
            0 -> {}
            3 -> if (bitspc == 16) throw PngjInputException("bad IHDR: bitdepth invalid")
            2, 4, 6 -> if (bitspc != 8 && bitspc != 16) throw PngjInputException("bad IHDR: bitdepth invalid")
            else -> throw PngjInputException("bad IHDR: invalid colormodel")
        }
    }

    companion object {
        const val ID: String = ChunkHelper.IHDR
    }
}