/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.PngjException
import org.jetbrains.letsPlot.nat.encoding.png.arraycopy

/**
 * iCCP chunk.
 *
 *
 */
internal class PngChunkICCP(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11iCCP
    private lateinit var profileName: String
    // copmression/decopmresion is done in getter/setter
    private lateinit var compressedProfile : ByteArray
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw = createEmptyChunk(profileName.length + compressedProfile.size + 2, true)
        org.jetbrains.letsPlot.nat.encoding.png.arraycopy(
            ChunkHelper.toBytesLatin1(profileName),
            0,
            c.data!!,
            0,
            profileName.length
        )
        c.data!![profileName.length] = 0
        c.data!![profileName.length + 1] = 0
        org.jetbrains.letsPlot.nat.encoding.png.arraycopy(
            compressedProfile,
            0,
            c.data!!,
            profileName.length + 2,
            compressedProfile.size
        )
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        val pos0: Int = ChunkHelper.posNullByte(chunk.data!!)
        profileName = ChunkHelper.toStringLatin1(chunk.data!!, 0, pos0)
        val comp: Int = chunk.data!![pos0 + 1].toInt() and 0xff
        if (comp != 0) throw PngjException("bad compression for ChunkTypeICCP")
        val compdatasize: Int = chunk.data!!.size - (pos0 + 2)
        compressedProfile = ByteArray(compdatasize)
        org.jetbrains.letsPlot.nat.encoding.png.arraycopy(chunk.data!!, pos0 + 2, compressedProfile, 0, compdatasize)
    }

    companion object {
        const val ID: String = ChunkHelper.iCCP
    }
}