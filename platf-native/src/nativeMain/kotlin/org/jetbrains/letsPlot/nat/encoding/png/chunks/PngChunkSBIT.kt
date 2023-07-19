/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")
package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.PngHelperInternal
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * sBIT chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11sBIT
 *
 *
 * this chunk structure depends on the image type
 */
internal class PngChunkSBIT(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11sBIT
    // significant bits
    private var graysb = 0
    private var alphasb = 0
    private var redsb = 0
    private var greensb = 0
    private var bluesb = 0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT
    private val cLen: Int
        get() {
            var len = if (imgInfo.greyscale) 1 else 3
            if (imgInfo.alpha) len += 1
            return len
        }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != cLen) throw PngjException("bad chunk length $chunk")
        if (imgInfo.greyscale) {
            graysb = PngHelperInternal.readInt1fromByte(chunk.data, 0)
            if (imgInfo.alpha) alphasb = PngHelperInternal.readInt1fromByte(chunk.data, 1)
        } else {
            redsb = PngHelperInternal.readInt1fromByte(chunk.data, 0)
            greensb = PngHelperInternal.readInt1fromByte(chunk.data, 1)
            bluesb = PngHelperInternal.readInt1fromByte(chunk.data, 2)
            if (imgInfo.alpha) alphasb = PngHelperInternal.readInt1fromByte(chunk.data, 3)
        }
    }

    override fun createRawChunk(): ChunkRaw {
        val c = createEmptyChunk(cLen, true)
        if (imgInfo.greyscale) {
            c.data!![0] = graysb.toByte()
            if (imgInfo.alpha) c.data!![1] = alphasb.toByte()
        } else {
            c.data!![0] = redsb.toByte()
            c.data!![1] = greensb.toByte()
            c.data!![2] = bluesb.toByte()
            if (imgInfo.alpha) c.data!![3] = alphasb.toByte()
        }
        return c
    }

    fun setGraysb(gray: Int) {
        if (!imgInfo.greyscale) throw PngjException("only greyscale images support this")
        graysb = gray
    }

    fun getGraysb(): Int {
        if (!imgInfo.greyscale) throw PngjException("only greyscale images support this")
        return graysb
    }

    fun setAlphasb(a: Int) {
        if (!imgInfo.alpha) throw PngjException("only images with alpha support this")
        alphasb = a
    }

    fun getAlphasb(): Int {
        if (!imgInfo.alpha) throw PngjException("only images with alpha support this")
        return alphasb
    }

    /**
     * Set rgb values
     *
     */
    fun setRGB(r: Int, g: Int, b: Int) {
        if (imgInfo.greyscale || imgInfo.indexed) throw PngjException("only rgb or rgba images support this")
        redsb = r
        greensb = g
        bluesb = b
    }

    val rGB: IntArray
        get() {
            if (imgInfo.greyscale || imgInfo.indexed) throw PngjException("only rgb or rgba images support this")
            return intArrayOf(redsb, greensb, bluesb)
        }

    companion object {
        const val ID: String = ChunkHelper.sBIT
    }
}