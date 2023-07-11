/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj

import jetbrains.datalore.plot.pythonExtension.pngj.chunks.ChunkRaw
import jetbrains.datalore.plot.pythonExtension.pngj.chunks.PngChunk


/**
 * Factory to create a [PngChunk] from a [ChunkRaw].
 *
 *
 * Used by [PngReader]
 */
internal interface IChunkFactory {
    /**
     * @param chunkRaw
     * Chunk in raw form. Data can be null if it was skipped or
     * processed directly (eg IDAT)
     * @param imgInfo
     * Not normally necessary, but some chunks want this info
     * @return should never return null.
     */
    fun createChunk(chunkRaw: ChunkRaw, imgInfo: ImageInfo?): PngChunk
}