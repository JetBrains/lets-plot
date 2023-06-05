/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

@file:Suppress("unused")
package org.jetbrains.letsPlot.util.pngj.chunks

import org.jetbrains.letsPlot.util.pngj.ImageInfo
import org.jetbrains.letsPlot.util.pngj.PngHelperInternal
import org.jetbrains.letsPlot.util.pngj.PngjException

/**
 * pHYs chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11pHYs
 */
internal class PngChunkPHYS(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11pHYs
    var pixelsxUnitX: Long = 0
    var pixelsxUnitY: Long = 0
    var units // 0: unknown 1:metre
            = 0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw = createEmptyChunk(9, true)
        PngHelperInternal.writeInt4tobytes(pixelsxUnitX.toInt(), c.data, 0)
        PngHelperInternal.writeInt4tobytes(pixelsxUnitY.toInt(), c.data, 4)
        c.data!![8] = units.toByte()
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 9) throw PngjException("bad chunk length $chunk")
        pixelsxUnitX = PngHelperInternal.readInt4fromBytes(chunk.data, 0).toLong()
        if (pixelsxUnitX < 0) pixelsxUnitX += 0x100000000L
        pixelsxUnitY = PngHelperInternal.readInt4fromBytes(chunk.data, 4).toLong()
        if (pixelsxUnitY < 0) pixelsxUnitY += 0x100000000L
        units = PngHelperInternal.readInt1fromByte(chunk.data, 8)
    }
    // special getters / setters
    /**
     * returns -1 if the physicial unit is unknown, or X-Y are not equal
     */
    var asDpi: Double
        get() = if (units != 1 || pixelsxUnitX != pixelsxUnitY) (-1).toDouble() else pixelsxUnitX.toDouble() * 0.0254
        set(dpi) {
            units = 1
            pixelsxUnitX = (dpi / 0.0254 + 0.5).toLong()
            pixelsxUnitY = pixelsxUnitX
        }

    /**
     * returns -1 if the physicial unit is unknown
     */
    val asDpi2: DoubleArray
        get() = if (units != 1) doubleArrayOf(-1.0, -1.0) else doubleArrayOf(
            pixelsxUnitX.toDouble() * 0.0254,
            pixelsxUnitY.toDouble() * 0.0254
        )

    fun setAsDpi2(dpix: Double, dpiy: Double) {
        units = 1
        pixelsxUnitX = (dpix / 0.0254 + 0.5).toLong()
        pixelsxUnitY = (dpiy / 0.0254 + 0.5).toLong()
    }

    companion object {
        const val ID: String = ChunkHelper.pHYs
    }
}