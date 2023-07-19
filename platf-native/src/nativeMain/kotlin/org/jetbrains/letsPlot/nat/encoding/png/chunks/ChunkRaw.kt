/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.nat.encoding.png.*
import org.jetbrains.letsPlot.nat.encoding.png.Zip.crc32

/**
 * Raw (physical) chunk.
 *
 *
 * Short lived object, to be created while serialing/deserializing Do not reuse
 * it for different chunks. <br></br>
 * See http://www.libpng.org/pub/png/spec/1.2/PNG-Structure.html
 */
class ChunkRaw(
    /**
     * The length counts only the data field, not itself, the chunk type code,
     * or the CRC. Zero is a valid length. Although encoders and decoders should
     * treat the length as unsigned, its value must not exceed 231-1 bytes.
     */
    val len: Int, id: String, alloc: Boolean
) {
    /**
     * A 4-byte chunk type code. uppercase and lowercase ASCII letters
     */
    val idbytes: ByteArray
    val id: String

    /**
     * The data bytes appropriate to the chunk type, if any. This field can be
     * of zero length. Does not include crc. If it's null, it means that the
     * data is ot available
     */
    var data: ByteArray? = null
    /**
     * offset in the full PNG stream, in bytes. only informational, for read
     * chunks (0=NA)
     */
    /**
     * @see ChunkRaw.offset
     */
    var offset: Long = 0

    /**
     * A 4-byte CRC (Cyclic Redundancy Check) calculated on the preceding bytes
     * in the chunk, including the chunk type code and chunk data fields, but
     * not including the length field.
     */
    var crcval = ByteArray(4)
    private var crcengine // lazily instantiated
            : Checksum? = null

    init {
        this.id = id
        idbytes = ChunkHelper.toBytesLatin1(id)
        for (i in 0..3) {
            if (idbytes[i] < 0x41 || idbytes[i] > 0x7a || idbytes[i] in 0x5b..0x60) throw PngjException(
                "Bad id chunk: must be ascii letters $id"
            )
        }
        if (alloc) allocData()
    }

    constructor(len: Int, idbytes: ByteArray, alloc: Boolean) : this(
        len,
        ChunkHelper.toStringLatin1(idbytes),
        alloc
    )

    fun allocData() { // TODO: not public
        if (data == null || data!!.size < len) data = ByteArray(len)
    }

    /**
     * this is called after setting data, before writing to os
     */
    private fun computeCrcForWriting() {
        crcengine = crc32()
        crcengine!!.update(idbytes, 0, 4)
        if (len > 0) crcengine!!.update(data!!, 0, len) //
        PngHelperInternal.writeInt4tobytes(crcengine!!.value.toInt(), crcval, 0)
    }

    /**
     * Computes the CRC and writes to the stream. If error, a
     * PngjOutputException is thrown
     *
     * Note that this is only used for non idat chunks
     */
    fun writeChunk(os: OutputPngStream) {
        writeChunkHeader(os)
        if (len > 0) {
            require(data != null) { "cannot write chunk, raw chunk data is null [$id]" }
            PngHelperInternal.writeBytes(os, data!!, 0, len)
        }
        computeCrcForWriting()
        writeChunkCrc(os)
    }

    private fun writeChunkHeader(os: OutputPngStream) {
        if (idbytes.size != 4) throw PngjOutputException("bad chunkid [$id]")
        PngHelperInternal.writeInt4(os, len)
        PngHelperInternal.writeBytes(os, idbytes)
    }

    private fun writeChunkCrc(os: OutputPngStream) {
        PngHelperInternal.writeBytes(os, crcval, 0, 4)
    }

    fun checkCrc(throwExcep: Boolean) {
        val crcComputed: Int = crcengine!!.value.toInt()
        val crcExpected: Int = PngHelperInternal.readInt4fromBytes(crcval, 0)
        if (crcComputed != crcExpected) {
            val msg =
                "Bad CRC in chunk: $id (offset:$offset). Expected:$crcExpected Got:$crcComputed"

            if (throwExcep) throw PngjBadCrcException(msg) else println(msg)
        }
    }

    fun updateCrc(buf: ByteArray, off: Int, len: Int) {
        if (crcengine == null) crcengine = crc32()
        crcengine!!.update(buf, off, len)
    }

    // only the data
    val asByteStream: InputPngStream
        get() =// only the data
            InputPngStream(data!!)

    override fun toString(): String {
        return "chunkid=" + ChunkHelper.toStringLatin1(idbytes) + " len=" + len
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + id.hashCode()
        result = prime * result + (offset xor (offset ushr 32)).toInt()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false
        other as ChunkRaw
        if (id != other.id) return false
        return offset == other.offset
    }
}