/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj

import org.jetbrains.letsPlot.util.pngj.chunks.ChunkRaw
import org.jetbrains.letsPlot.util.pngj.chunks.PngChunk


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