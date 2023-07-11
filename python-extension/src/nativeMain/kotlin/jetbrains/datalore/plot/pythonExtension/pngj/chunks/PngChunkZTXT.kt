/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.ImageInfo
import jetbrains.datalore.plot.pythonExtension.pngj.OutputPngStream
import jetbrains.datalore.plot.pythonExtension.pngj.PngjException

/**
 * zTXt chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11zTXt
 */
internal class PngChunkZTXT  // http://www.w3.org/TR/PNG/#11zTXt
    (info: ImageInfo?) : PngChunkTextVar(ID, info) {
    override fun createRawChunk(): ChunkRaw {
        if (key == null || key!!.trim { it <= ' ' }.isEmpty()) throw PngjException("Text chunk key must be non empty")
        val ba = OutputPngStream()
        ba.write(ChunkHelper.toBytesLatin1(key!!))
        ba.write(0) // separator
        ba.write(0) // compression method: 0
        val textbytes: ByteArray = ChunkHelper.compressBytes(ChunkHelper.toBytesLatin1(value!!), true)
        ba.write(textbytes)
        val b: ByteArray = ba.byteArray
        val chunk: ChunkRaw = createEmptyChunk(b.size, false)
        chunk.data = b
        return chunk
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        var nullsep = -1
        for (i in chunk.data!!.indices) { // look for first zero
            if (chunk.data!![i].toInt() != 0) continue
            nullsep = i
            break
        }
        if (nullsep < 0 || nullsep > chunk.data!!.size - 2) throw PngjException("bad zTXt chunk: no separator found")
        key = ChunkHelper.toStringLatin1(chunk.data!!, 0, nullsep)
        val compmet: Int = chunk.data!![nullsep + 1].toInt()
        if (compmet != 0) throw PngjException("bad zTXt chunk: unknown compression method")
        val uncomp: ByteArray =
            ChunkHelper.compressBytes(chunk.data!!, nullsep + 2, chunk.data!!.size - nullsep - 2, false) // uncompress
        value = ChunkHelper.toStringLatin1(uncomp)
    }

    companion object {
        const val ID: String = ChunkHelper.zTXt
    }
}