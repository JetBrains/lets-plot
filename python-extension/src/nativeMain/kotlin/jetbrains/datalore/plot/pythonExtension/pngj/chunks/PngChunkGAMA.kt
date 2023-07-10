/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.ImageInfo
import jetbrains.datalore.plot.pythonExtension.pngj.PngHelperInternal
import jetbrains.datalore.plot.pythonExtension.pngj.PngjException

/**
 * gAMA chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11gAMA
 */
internal class PngChunkGAMA(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11gAMA
    private var gamma = 0.0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw = createEmptyChunk(4, true)
        val g = (gamma * 100000 + 0.5).toInt()
        PngHelperInternal.writeInt4tobytes(g, c.data, 0)
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 4) throw PngjException("bad chunk $chunk")
        val g: Int = PngHelperInternal.readInt4fromBytes(chunk.data, 0)
        gamma = g.toDouble() / 100000.0
    }

    companion object {
        const val ID: String = ChunkHelper.gAMA
    }
}