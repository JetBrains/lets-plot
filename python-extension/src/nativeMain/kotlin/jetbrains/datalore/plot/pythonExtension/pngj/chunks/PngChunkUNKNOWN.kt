/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.ImageInfo


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
