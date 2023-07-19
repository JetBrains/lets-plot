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

package org.jetbrains.letsPlot.nat.encoding.png

import org.jetbrains.letsPlot.nat.encoding.png.chunks.ChunkRaw
import org.jetbrains.letsPlot.nat.encoding.png.chunks.PngChunk


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