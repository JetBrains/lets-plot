/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

@file:Suppress("unused")
package org.jetbrains.letsPlot.util.pngj.chunks

import org.jetbrains.letsPlot.util.pngj.ImageInfo
import org.jetbrains.letsPlot.util.pngj.PngjException


/**
 * PLTE chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11PLTE
 *
 *
 * Critical chunk
 */
class PngChunkPLTE(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11PLTE
    private var nentries = 0

    /**
     * RGB8 packed in one integer
     */
    private var entries: IntArray? = null
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.NA

    override fun createRawChunk(): ChunkRaw {
        val len = 3 * nentries
        val rgb = IntArray(3)
        val c: ChunkRaw = createEmptyChunk(len, true)
        var n = 0
        var i = 0
        while (n < nentries) {
            getEntryRgb(n, rgb)
            c.data!![i++] = rgb[0].toByte()
            c.data!![i++] = rgb[1].toByte()
            c.data!![i++] = rgb[2].toByte()
            n++
        }
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        setNentries(chunk.len / 3)
        var n = 0
        var i = 0
        while (n < nentries) {
            setEntry(
                n,
                (chunk.data!![i++].toInt() and 0xff),
                (chunk.data!![i++].toInt() and 0xff),
                (chunk.data!![i++].toInt() and 0xff)
            )
            n++
        }
    }

    fun setNentries(n: Int) {
        nentries = n
        if (nentries < 1 || nentries > 256) throw PngjException("invalid pallette - nentries=$nentries")
        if (entries == null || entries!!.size != nentries) { // alloc
            entries = IntArray(nentries)
        }
    }

    fun getNentries(): Int {
        return nentries
    }

    fun setEntry(n: Int, r: Int, g: Int, b: Int) {
        entries!![n] = r shl 16 or (g shl 8) or b
    }

    fun getEntry(n: Int): Int {
        return entries!![n]
    }

    private fun getEntryRgb(n: Int, rgb: IntArray) {
        getEntryRgb(n, rgb, 0)
    }

    fun getEntryRgb(n: Int, rgb: IntArray, offset: Int) {
        val v = entries!![n]
        rgb[offset + 0] = v and 0xff0000 shr 16
        rgb[offset + 1] = v and 0xff00 shr 8
        rgb[offset + 2] = v and 0xff
    }

    fun minBitDepth(): Int {
        return if (nentries <= 2) 1 else if (nentries <= 4) 2 else if (nentries <= 16) 4 else 8
    }

    companion object {
        const val ID: String = ChunkHelper.PLTE
    }
}