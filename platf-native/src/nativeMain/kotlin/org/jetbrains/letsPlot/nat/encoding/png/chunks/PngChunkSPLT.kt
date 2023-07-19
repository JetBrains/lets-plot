/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.OutputPngStream
import org.jetbrains.letsPlot.nat.encoding.png.PngHelperInternal
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * sPLT chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11sPLT
 */
internal class PngChunkSPLT(info: ImageInfo?) : PngChunkMultiple(ID, info) {
    // http://www.w3.org/TR/PNG/#11sPLT
    var palName: String? = null
    var sampledepth // 8/16
            = 0
    var palette // 5 elements per entry
            : IntArray = IntArray(5)
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.BEFORE_IDAT

    override fun createRawChunk(): ChunkRaw {
        val ba = OutputPngStream()
        ba.write(ChunkHelper.toBytesLatin1(palName!!))
        ba.write(0) // separator
        ba.write(sampledepth.toByte().toInt())
        val nentries = nentries
        for (n in 0 until nentries) {
            for (i in 0..3) {
                if (sampledepth == 8) PngHelperInternal.writeByte(
                    ba,
                    palette[n * 5 + i].toByte()
                ) else PngHelperInternal.writeInt2(ba, palette[n * 5 + i])
            }
            PngHelperInternal.writeInt2(ba, palette[n * 5 + 4])
        }
        val b: ByteArray = ba.byteArray
        val chunk: ChunkRaw = createEmptyChunk(b.size, false)
        chunk.data = b
        return chunk
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        var t = -1
        for (i in chunk.data!!.indices) { // look for first zero
            if (chunk.data!![i].toInt() == 0) {
                t = i
                break
            }
        }
        if (t <= 0 || t > chunk.data!!.size - 2) throw PngjException("bad sPLT chunk: no separator found")
        palName = ChunkHelper.toStringLatin1(chunk.data!!, 0, t)
        sampledepth = PngHelperInternal.readInt1fromByte(chunk.data, t + 1)
        t += 2
        val nentries: Int = (chunk.data!!.size - t) / if (sampledepth == 8) 6 else 10
        palette = IntArray(nentries * 5)
        var r: Int
        var g: Int
        var b: Int
        var a: Int
        var f: Int
        var ne = 0
        for (i in 0 until nentries) {
            if (sampledepth == 8) {
                r = PngHelperInternal.readInt1fromByte(chunk.data, t++)
                g = PngHelperInternal.readInt1fromByte(chunk.data, t++)
                b = PngHelperInternal.readInt1fromByte(chunk.data, t++)
                a = PngHelperInternal.readInt1fromByte(chunk.data, t++)
            } else {
                r = PngHelperInternal.readInt2fromBytes(chunk.data, t)
                t += 2
                g = PngHelperInternal.readInt2fromBytes(chunk.data, t)
                t += 2
                b = PngHelperInternal.readInt2fromBytes(chunk.data, t)
                t += 2
                a = PngHelperInternal.readInt2fromBytes(chunk.data, t)
                t += 2
            }
            f = PngHelperInternal.readInt2fromBytes(chunk.data, t)
            t += 2
            palette[ne++] = r
            palette[ne++] = g
            palette[ne++] = b
            palette[ne++] = a
            palette[ne++] = f
        }
    }

    private val nentries: Int
        get() = palette.size / 5

    companion object {
        const val ID: String = ChunkHelper.sPLT
    }
}