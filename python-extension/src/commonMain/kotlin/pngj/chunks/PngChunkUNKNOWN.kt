/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj.chunks

import org.jetbrains.letsPlot.util.pngj.ImageInfo


/**
 * Placeholder for UNKNOWN (custom or not) chunks.
 *
 *
 * For PngReader, a chunk is unknown if it's not registered in the chunk factory
 */
internal class PngChunkUNKNOWN  // unkown, custom or not
    (id: String, info: ImageInfo?) : PngChunkMultiple(id, info) {
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.NONE

    override fun createRawChunk(): ChunkRaw {
        error("Unsupported operation")
    }

    override fun parseFromRaw(chunk: ChunkRaw) {}
}
