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
 * gAMA chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11gAMA
 */
internal class PngChunkGAMA(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11gAMA
    private var gamma = 0.0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw = createEmptyChunk(4, true)
        val g = (gamma * 100000 + 0.5).toInt()
        PngHelperInternal.writeInt4tobytes(g, c.data, 0)
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 4) throw PngjException("bad chunk $chunk")
        val g: Int = PngHelperInternal.readInt4fromBytes(chunk.data, 0)
        gamma = g.toDouble() / 100000.0
    }

    companion object {
        const val ID: String = ChunkHelper.gAMA
    }
}