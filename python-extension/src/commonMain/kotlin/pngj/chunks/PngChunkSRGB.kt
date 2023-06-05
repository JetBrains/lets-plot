/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

@file:Suppress("unused")
package org.jetbrains.letsPlot.util.pngj.chunks

import org.jetbrains.letsPlot.util.pngj.ImageInfo
import org.jetbrains.letsPlot.util.pngj.PngHelperInternal
import org.jetbrains.letsPlot.util.pngj.PngjException

/**
 * sRGB chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11sRGB
 */
internal class PngChunkSRGB(info: ImageInfo?) : PngChunkSingle(ID, info) {
    private var intent = 0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 1) throw PngjException("bad chunk length $chunk")
        intent = PngHelperInternal.readInt1fromByte(chunk.data, 0)
    }

    override fun createRawChunk(): ChunkRaw {
        val c = createEmptyChunk(1, true)
        c.data!![0] = intent.toByte()
        return c
    }

    companion object {
        const val ID: String = ChunkHelper.sRGB

        // http://www.w3.org/TR/PNG/#11sRGB
        const val RENDER_INTENT_Perceptual = 0
        const val RENDER_INTENT_Relative_colorimetric = 1
        const val RENDER_INTENT_Saturation = 2
        const val RENDER_INTENT_Absolute_colorimetric = 3
    }
}