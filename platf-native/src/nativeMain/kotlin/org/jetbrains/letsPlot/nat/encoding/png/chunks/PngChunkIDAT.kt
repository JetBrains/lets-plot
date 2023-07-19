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

/**
 * IDAT chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11IDAT
 *
 *
 * This is dummy placeholder - we write/read this chunk (actually several) by
 * special code.
 */
internal class PngChunkIDAT  // http://www.w3.org/TR/PNG/#11IDAT
    (i: ImageInfo?) : PngChunkMultiple(ID, i) {
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.NA

    override fun createRawChunk(): ChunkRaw { // does nothing
        return createEmptyChunk(0, false)
    }

    override fun parseFromRaw(chunk: ChunkRaw) { // does nothing
    }

    companion object {
        const val ID: String = ChunkHelper.IDAT
    }
}