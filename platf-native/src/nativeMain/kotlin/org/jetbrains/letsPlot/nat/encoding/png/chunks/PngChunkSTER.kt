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
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * sTER chunk.
 *
 *
 * see http://www.libpng.org/pub/png/spec/register/pngext-1.3.0-pdg.html#C.sTER
 */
internal class PngChunkSTER(info: ImageInfo?) : PngChunkSingle(ID, info) {
    /**
     * 0: cross-fuse layout 1: diverging-fuse layout
     */
    /**
     * 0: cross-fuse layout 1: diverging-fuse layout
     */
    // http://www.libpng.org/pub/png/spec/register/pngext-1.3.0-pdg.html#C.sTER
    var mode // 0: cross-fuse layout 1: diverging-fuse layout
            : Byte = 0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw = createEmptyChunk(1, true)
        c.data!![0] = mode
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 1) throw PngjException("bad chunk length $chunk")
        mode = chunk.data!![0]
    }

    companion object {
        const val ID = "sTER"
    }
}