/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
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