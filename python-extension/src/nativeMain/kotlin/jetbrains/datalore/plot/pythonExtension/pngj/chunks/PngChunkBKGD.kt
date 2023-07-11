/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.ImageInfo
import jetbrains.datalore.plot.pythonExtension.pngj.PngHelperInternal
import jetbrains.datalore.plot.pythonExtension.pngj.PngjException


/**
 * bKGD Chunk.
 *
 *
 *
 * This chunk structure depends on the image type
 */
internal class PngChunkBKGD(info: ImageInfo?) : PngChunkSingle(ChunkHelper.bKGD, info) {
    // only one of these is meaningful
    private var gray = 0
    private var red = 0
    private var green = 0
    private var blue = 0
    private var paletteIndex = 0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw
        if (imgInfo.greyscale) {
            c = createEmptyChunk(2, true)
            PngHelperInternal.writeInt2tobytes(gray, c.data, 0)
        } else if (imgInfo.indexed) {
            c = createEmptyChunk(1, true)
            c.data!![0] = paletteIndex.toByte()
        } else {
            c = createEmptyChunk(6, true)
            PngHelperInternal.writeInt2tobytes(red, c.data, 0)
            PngHelperInternal.writeInt2tobytes(green, c.data, 0)
            PngHelperInternal.writeInt2tobytes(blue, c.data, 0)
        }
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (imgInfo.greyscale) {
            gray = PngHelperInternal.readInt2fromBytes(chunk.data, 0)
        } else if (imgInfo.indexed) {
            paletteIndex = (chunk.data!![0].toInt() and 0xff)
        } else {
            red = PngHelperInternal.readInt2fromBytes(chunk.data, 0)
            green = PngHelperInternal.readInt2fromBytes(chunk.data, 2)
            blue = PngHelperInternal.readInt2fromBytes(chunk.data, 4)
        }
    }

    /**
     * Set gray value (0-255 if bitdept=8)
     *
     * @param gray
     */
    fun setGray(gray: Int) {
        if (!imgInfo.greyscale) throw PngjException("only gray images support this")
        this.gray = gray
    }

    fun getGray(): Int {
        if (!imgInfo.greyscale) throw PngjException("only gray images support this")
        return gray
    }

    /**
     * Set pallette index
     *
     */
    fun setPaletteIndex(i: Int) {
        if (!imgInfo.indexed) throw PngjException("only indexed (pallete) images support this")
        paletteIndex = i
    }

    fun getPaletteIndex(): Int {
        if (!imgInfo.indexed) throw PngjException("only indexed (pallete) images support this")
        return paletteIndex
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

    companion object {
        const val ID: String = ChunkHelper.bKGD
    }
}