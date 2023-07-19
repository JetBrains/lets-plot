/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.OutputPngStream
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * iTXt chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11iTXt
 */
internal class PngChunkITXT  // http://www.w3.org/TR/PNG/#11iTXt
    (info: ImageInfo?) : PngChunkTextVar(ID, info) {
    private var isCompressed = false
    private var langtag = ""
    private var translatedTag = ""
    override fun createRawChunk(): ChunkRaw {
        if (key == null || key!!.trim { it <= ' ' }.isEmpty()) throw PngjException("Text chunk key must be non empty")

        val ba = OutputPngStream()
        ba.write(ChunkHelper.toBytesLatin1(key!!))
        ba.write(0) // separator
        ba.write(if (isCompressed) 1 else 0)
        ba.write(0) // compression method (always 0)
        ba.write(ChunkHelper.toBytesLatin1(langtag))
        ba.write(0) // separator
        ba.write(ChunkHelper.toBytesUTF8(translatedTag))
        ba.write(0) // separator
        var textbytes: ByteArray = ChunkHelper.toBytesUTF8(value!!)
        if (isCompressed) {
            textbytes = ChunkHelper.compressBytes(textbytes, true)
        }
        ba.write(textbytes)
        val b: ByteArray = ba.byteArray
        val chunk: ChunkRaw = createEmptyChunk(b.size, false)
        chunk.data = b
        return chunk
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        var nullsFound = 0
        val nullsIdx = IntArray(3)
        run {
            var i = 0
            while (i < chunk.data!!.size) {
                if (chunk.data!![i].toInt() != 0) {
                    i++
                    continue
                }
                nullsIdx[nullsFound] = i
                nullsFound++
                if (nullsFound == 1) i += 2
                if (nullsFound == 3) break
                i++
            }
        }
        if (nullsFound != 3) throw PngjException("Bad formed PngChunkITXT chunk")
        key = ChunkHelper.toStringLatin1(chunk.data!!, 0, nullsIdx[0])
        var i = nullsIdx[0] + 1
        isCompressed = chunk.data!![i] != 0.toByte()
        i++
        if (isCompressed && chunk.data!![i]
                .toInt() != 0
        ) throw PngjException("Bad formed PngChunkITXT chunk - bad compression method ")
        langtag = ChunkHelper.toStringLatin1(chunk.data!!, i, nullsIdx[1] - i)
        translatedTag = ChunkHelper.toStringUTF8(chunk.data!!, nullsIdx[1] + 1, nullsIdx[2] - nullsIdx[1] - 1)
        i = nullsIdx[2] + 1
        value = if (isCompressed) {
            val bytes: ByteArray = ChunkHelper.compressBytes(chunk.data!!, i, chunk.data!!.size - i, false)
            ChunkHelper.toStringUTF8(bytes)
        } else {
            ChunkHelper.toStringUTF8(chunk.data!!, i, chunk.data!!.size - i)
        }
    }

    companion object {
        const val ID = ChunkHelper.iTXt
    }
}