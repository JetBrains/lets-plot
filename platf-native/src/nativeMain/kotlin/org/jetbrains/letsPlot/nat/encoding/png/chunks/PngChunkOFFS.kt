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
import org.jetbrains.letsPlot.nat.encoding.png.PngHelperInternal
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * oFFs chunk.
 *
 *
 * see http://www.libpng.org/pub/png/spec/register/pngext-1.3.0-pdg.html#C.oFFs
 */
internal class PngChunkOFFS(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.libpng.org/pub/png/spec/register/pngext-1.3.0-pdg.html#C.oFFs
    var posX: Long = 0
    var posY: Long = 0
    /**
     * 0: pixel, 1:micrometer
     */
    /**
     * 0: pixel, 1:micrometer
     */
    var units // 0: pixel 1:micrometer
            = 0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw = createEmptyChunk(9, true)
        PngHelperInternal.writeInt4tobytes(posX.toInt(), c.data, 0)
        PngHelperInternal.writeInt4tobytes(posY.toInt(), c.data, 4)
        c.data!![8] = units.toByte()
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 9) throw PngjException("bad chunk length $chunk")
        posX = PngHelperInternal.readInt4fromBytes(chunk.data, 0).toLong()
        if (posX < 0) posX += 0x100000000L
        posY = PngHelperInternal.readInt4fromBytes(chunk.data, 4).toLong()
        if (posY < 0) posY += 0x100000000L
        units = PngHelperInternal.readInt1fromByte(chunk.data, 8)
    }

    companion object {
        const val ID = "oFFs"
    }
}