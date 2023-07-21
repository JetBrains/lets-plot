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

/**
 * What to do with ancillary (non-critical) chunks when reading.
 *
 *
 *
 */
internal enum class ChunkLoadBehaviour {
    /**
     * All non-critical chunks are skipped
     */
    LOAD_CHUNK_NEVER,

    /**
     * Load chunk if "safe to copy"
     */
    LOAD_CHUNK_IF_SAFE,

    /**
     * Load only most important chunk: TRNS
     */
    LOAD_CHUNK_MOST_IMPORTANT,

    /**
     * Load all chunks. <br></br>
     * Notice that other restrictions might apply, see
     * PngReader.skipChunkMaxSize PngReader.skipChunkIds
     */
    LOAD_CHUNK_ALWAYS
}