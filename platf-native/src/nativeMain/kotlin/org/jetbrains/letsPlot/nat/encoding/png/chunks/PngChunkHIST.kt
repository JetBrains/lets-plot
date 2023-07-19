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
 * hIST chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11hIST <br></br>
 * only for palette images
 */
internal class PngChunkHIST(info: ImageInfo?) : PngChunkSingle(ID, info) {
    private var hist = IntArray(0) // should have same lenght as palette
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (!imgInfo.indexed) throw PngjException("only indexed images accept a HIST chunk")
        val nentries: Int = chunk.data!!.size / 2
        hist = IntArray(nentries)
        for (i in hist.indices) {
            hist[i] = PngHelperInternal.readInt2fromBytes(chunk.data, i * 2)
        }
    }

    override fun createRawChunk(): ChunkRaw {
        if (!imgInfo.indexed) throw PngjException("only indexed images accept a HIST chunk")
        val c: ChunkRaw = createEmptyChunk(hist.size * 2, true)
        for (i in hist.indices) {
            PngHelperInternal.writeInt2tobytes(hist[i], c.data, i * 2)
        }
        return c
    }

    companion object {
        const val ID: String = ChunkHelper.hIST
    }
}