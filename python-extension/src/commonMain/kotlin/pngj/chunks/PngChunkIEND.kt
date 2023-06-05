/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj.chunks

import org.jetbrains.letsPlot.util.pngj.ImageInfo

/**
 * IEND chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11IEND
 */
internal class PngChunkIEND  // http://www.w3.org/TR/PNG/#11IEND
// this is a dummy placeholder
    (info: ImageInfo?) : PngChunkSingle(ID, info) {
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.NA

    override fun createRawChunk(): ChunkRaw {
        return ChunkRaw(0, ChunkHelper.b_IEND, false)
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        // this is not used
    }

    companion object {
        const val ID: String = ChunkHelper.IEND
    }
}