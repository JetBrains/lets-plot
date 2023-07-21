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

import org.jetbrains.letsPlot.nat.encoding.png.IChunkFactory
import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo

/**
 * Default chunk factory.
 *
 *
 * The user that wants to parse custom chunks can extend
 * [.createEmptyChunkExtended]
 */
internal class ChunkFactory constructor(var parse: Boolean = true) : IChunkFactory {
    override fun createChunk(chunkRaw: ChunkRaw, imgInfo: ImageInfo?): PngChunk {
        var c: PngChunk? = createEmptyChunkKnown(chunkRaw.id, imgInfo)
        if (c == null) c = createEmptyChunkExtended(chunkRaw.id, imgInfo)
        if (c == null) c = createEmptyChunkUnknown(chunkRaw.id, imgInfo)
        c.raw = chunkRaw
        if (parse && chunkRaw.data != null) c.parseFromRaw(chunkRaw)
        return c
    }

    private fun createEmptyChunkKnown(id: String, imgInfo: ImageInfo?): PngChunk? {
        if (id == ChunkHelper.IDAT) return PngChunkIDAT(imgInfo)
        if (id == ChunkHelper.IHDR) return PngChunkIHDR(imgInfo)
        if (id == ChunkHelper.PLTE) return PngChunkPLTE(imgInfo)
        if (id == ChunkHelper.IEND) return PngChunkIEND(imgInfo)
        if (id == ChunkHelper.tEXt) return PngChunkTEXT(imgInfo)
        if (id == ChunkHelper.iTXt) return PngChunkITXT(imgInfo)
        if (id == ChunkHelper.zTXt) return PngChunkZTXT(imgInfo)
        if (id == ChunkHelper.bKGD) return PngChunkBKGD(imgInfo)
        if (id == ChunkHelper.gAMA) return PngChunkGAMA(imgInfo)
        if (id == ChunkHelper.pHYs) return PngChunkPHYS(imgInfo)
        if (id == ChunkHelper.iCCP) return PngChunkICCP(imgInfo)
        if (id == ChunkHelper.tIME) return PngChunkTIME(imgInfo)
        if (id == ChunkHelper.tRNS) return PngChunkTRNS(imgInfo)
        if (id == ChunkHelper.cHRM) return PngChunkCHRM(imgInfo)
        if (id == ChunkHelper.sBIT) return PngChunkSBIT(imgInfo)
        if (id == ChunkHelper.sRGB) return PngChunkSRGB(imgInfo)
        if (id == ChunkHelper.hIST) return PngChunkHIST(imgInfo)
        if (id == ChunkHelper.sPLT) return PngChunkSPLT(imgInfo)
        return null
    }

    /**
     * This is used as last resort factory method.
     *
     *
     * It creates a [PngChunkUNKNOWN] chunk.
     */
    private fun createEmptyChunkUnknown(id: String, imgInfo: ImageInfo?): PngChunk {
        return PngChunkUNKNOWN(id, imgInfo)
    }

    /**
     * Factory for chunks that are not in the original PNG standard. This can be
     * overriden (but dont forget to call this also)
     *
     * @param id
     * Chunk id , 4 letters
     * @param imgInfo
     * Usually not needed
     * @return null if chunk id not recognized
     */
    private fun createEmptyChunkExtended(id: String, imgInfo: ImageInfo?): PngChunk? {
        if (id == PngChunkOFFS.ID) return PngChunkOFFS(imgInfo)
        return if (id == PngChunkSTER.ID) PngChunkSTER(imgInfo) else null
        // extend!
    }
}