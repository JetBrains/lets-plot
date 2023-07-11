/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.ImageInfo
import jetbrains.datalore.plot.pythonExtension.pngj.PngHelperInternal
import jetbrains.datalore.plot.pythonExtension.pngj.PngjException

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