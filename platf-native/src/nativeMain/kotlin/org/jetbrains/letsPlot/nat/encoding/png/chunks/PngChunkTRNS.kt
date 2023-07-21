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

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.PngHelperInternal
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * tRNS chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11tRNS
 *
 *
 * this chunk structure depends on the image type
 */
@Suppress("unused")
class PngChunkTRNS(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11tRNS
    // only one of these is meaningful, depending on the image type
    private var gray = 0
    private var red = 0
    private var green = 0
    private var blue = 0

    /**
     * WARNING: non deep copy
     */
    var palletteAlpha = intArrayOf()
        private set
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw
        if (imgInfo.greyscale) {
            c = createEmptyChunk(2, true)
            PngHelperInternal.writeInt2tobytes(gray, c.data!!, 0)
        } else if (imgInfo.indexed) {
            c = createEmptyChunk(palletteAlpha.size, true)
            for (n in 0 until c.len) {
                c.data!![n] = palletteAlpha[n].toByte()
            }
        } else {
            c = createEmptyChunk(6, true)
            PngHelperInternal.writeInt2tobytes(red, c.data!!, 0)
            PngHelperInternal.writeInt2tobytes(green, c.data!!, 0)
            PngHelperInternal.writeInt2tobytes(blue, c.data!!, 0)
        }
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (imgInfo.greyscale) {
            gray = PngHelperInternal.readInt2fromBytes(chunk.data!!, 0)
        } else if (imgInfo.indexed) {
            val nentries: Int = chunk.data!!.size
            palletteAlpha = IntArray(nentries)
            for (n in 0 until nentries) {
                palletteAlpha[n] = (chunk.data!![n].toInt() and 0xff)
            }
        } else {
            red = PngHelperInternal.readInt2fromBytes(chunk.data!!, 0)
            green = PngHelperInternal.readInt2fromBytes(chunk.data!!, 2)
            blue = PngHelperInternal.readInt2fromBytes(chunk.data!!, 4)
        }
    }

    /**
     * Set rgb values
     *
     */
    fun setRGB(r: Int, g: Int, b: Int) {
        if (imgInfo.greyscale || imgInfo.indexed) throw PngjException("only rgb or rgba images support this")
        red = r
        green = g
        blue = b
    }

    val rGB: IntArray
        get() {
            if (imgInfo.greyscale || imgInfo.indexed) throw PngjException("only rgb or rgba images support this")
            return intArrayOf(red, green, blue)
        }
    val rGB888: Int
        get() {
            if (imgInfo.greyscale || imgInfo.indexed) throw PngjException("only rgb or rgba images support this")
            return red shl 16 or (green shl 8) or blue
        }

    fun setGray(g: Int) {
        if (!imgInfo.greyscale) throw PngjException("only grayscale images support this")
        gray = g
    }

    fun getGray(): Int {
        if (!imgInfo.greyscale) throw PngjException("only grayscale images support this")
        return gray
    }

    /**
     * Sets the length of the palette alpha. This should be followed by
     * #setNentriesPalAlpha
     *
     * @param idx
     * index inside the table
     * @param val
     * alpha value (0-255)
     */
    fun setEntryPalAlpha(idx: Int, `val`: Int) {
        palletteAlpha[idx] = `val`
    }

    fun setNentriesPalAlpha(len: Int) {
        palletteAlpha = IntArray(len)
    }

    /**
     * WARNING: non deep copy. See also [.setNentriesPalAlpha]
     * [.setEntryPalAlpha]
     */
    fun setPalAlpha(palAlpha: IntArray) {
        if (!imgInfo.indexed) throw PngjException("only indexed images support this")
        palletteAlpha = palAlpha
    }

    /**
     * to use when only one pallete index is set as totally transparent
     */
    fun setIndexEntryAsTransparent(palAlphaIndex: Int) {
        if (!imgInfo.indexed) throw PngjException("only indexed images support this")
        palletteAlpha = intArrayOf(palAlphaIndex + 1)
        for (i in 0 until palAlphaIndex) palletteAlpha[i] = 255
        palletteAlpha[palAlphaIndex] = 0
    }

    companion object {
        const val ID: String = ChunkHelper.tRNS
    }
}