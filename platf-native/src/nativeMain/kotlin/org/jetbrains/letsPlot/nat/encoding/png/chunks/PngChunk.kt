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

@file:Suppress("unused", "KDocUnresolvedReference")
package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.OutputPngStream
import org.jetbrains.letsPlot.nat.encoding.png.PngjExceptionInternal


/**
 * Represents a instance of a PNG chunk.
 *
 *
 * See
 * [http://www
 * .libpng.org/pub/png/spec/1.2/PNG-Chunks .html](http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html)
 *
 *
 * Concrete classes should extend [PngChunkSingle] or
 * [PngChunkMultiple]
 *
 *
 * Note that some methods/fields are type-specific (getOrderingConstraint(),
 * allowsMultiple()),<br></br>
 * some are 'almost' type-specific (id,crit,pub,safe; the exception is
 * PngUKNOWN), <br></br>
 * and the rest are instance-specific
 */
abstract class PngChunk(
    /**
     * Chunk-id: 4 letters
     */
    val id: String,
    private val imageInfo: ImageInfo?
) {
    /**
     * Autocomputed at creation time
     */
    val crit: Boolean
    val pub: Boolean
    val safe: Boolean
    protected val imgInfo: ImageInfo get() = imageInfo!!
    var raw: ChunkRaw? = null

    // For writing. Queued chunks with high priority will be written as soon as possible
    private var priority = false
    /**
     * In which "chunkGroup" (see [ChunksList]for definition) this chunks
     * instance was read or written.
     *
     *
     * -1 if not read or written (eg, queued)
     */
    /**
     * @see .getChunkGroup
     */
    var chunkGroup = -1 // chunk group where it was read or writen

    /**
     * Possible ordering constraint for a PngChunk type -only relevant for
     * ancillary chunks. Theoretically, there could be more general constraints,
     * but these cover the constraints for standard chunks.
     */
    enum class ChunkOrderingConstraint {
        /**
         * no ordering constraint
         */
        NONE,

        /**
         * Must go before PLTE (and hence, also before IDAT)
         */
        BEFORE_PLTE_AND_IDAT,

        /**
         * Must go after PLTE (if exists) but before IDAT
         */
        AFTER_PLTE_BEFORE_IDAT,

        /**
         * Must go after PLTE (and it must exist) but before IDAT
         */
        AFTER_PLTE_BEFORE_IDAT_PLTE_REQUIRED,

        /**
         * Must before IDAT (before or after PLTE)
         */
        BEFORE_IDAT,

        /**
         * After IDAT (this restriction does not apply to the standard PNG
         * chunks)
         */
        AFTER_IDAT,

        /**
         * Does not apply
         */
        NA;

        fun mustGoBeforePLTE(): Boolean {
            return this == BEFORE_PLTE_AND_IDAT
        }

        fun mustGoBeforeIDAT(): Boolean {
            return this == BEFORE_IDAT || this == BEFORE_PLTE_AND_IDAT || this == AFTER_PLTE_BEFORE_IDAT
        }

        /**
         * after pallete, if exists
         */
        fun mustGoAfterPLTE(): Boolean {
            return this == AFTER_PLTE_BEFORE_IDAT || this == AFTER_PLTE_BEFORE_IDAT_PLTE_REQUIRED
        }

        fun mustGoAfterIDAT(): Boolean {
            return this == AFTER_IDAT
        }

        fun isOk(currentChunkGroup: Int, hasplte: Boolean): Boolean {
            if (this == NONE) return true else if (this == BEFORE_IDAT) return currentChunkGroup < ChunksList.CHUNK_GROUP_4_IDAT else if (this == BEFORE_PLTE_AND_IDAT) return currentChunkGroup < ChunksList.CHUNK_GROUP_2_PLTE else if (this == AFTER_PLTE_BEFORE_IDAT) return if (hasplte) currentChunkGroup < ChunksList.CHUNK_GROUP_4_IDAT else (currentChunkGroup < ChunksList.CHUNK_GROUP_4_IDAT
                    && currentChunkGroup > ChunksList.CHUNK_GROUP_2_PLTE) else if (this == AFTER_IDAT) return currentChunkGroup > ChunksList.CHUNK_GROUP_4_IDAT
            return false
        }
    }

    init {
        crit = ChunkHelper.isCritical(id)
        pub = ChunkHelper.isPublic(id)
        safe = ChunkHelper.isSafeToCopy(id)
    }

    protected fun createEmptyChunk(len: Int, alloc: Boolean): ChunkRaw {
        return ChunkRaw(len, ChunkHelper.toBytesLatin1(id), alloc)
    }

    fun hasPriority(): Boolean {
        return priority
    }

    fun setPriority(priority: Boolean) {
        this.priority = priority
    }

    fun write(os: OutputPngStream) {
        if (raw == null || raw?.data == null) raw = createRawChunk()
        if (raw == null) throw PngjExceptionInternal("null chunk ! creation failed for $this")
        raw!!.writeChunk(os)
    }

    /**
     * Creates the physical chunk. This is used when writing (serialization).
     * Each particular chunk class implements its own logic.
     *
     * @return A newly allocated and filled raw chunk
     */
    abstract fun createRawChunk(): ChunkRaw

    /**
     * Parses raw chunk and fill inside data. This is used when reading
     * (deserialization). Each particular chunk class implements its own logic.
     */
    abstract fun parseFromRaw(chunk: ChunkRaw)

    /**
     * See [PngChunkMultiple] and [PngChunkSingle]
     *
     * @return true if PNG accepts multiple chunks of this class
     */
    abstract fun allowsMultiple(): Boolean

    /**
     * @see ChunkRaw.len
     */
    val len: Int
        get() = raw?.len ?: -1

    /**
     * @see ChunkRaw.getOffset
     */
    val offset: Long
        get() = raw?.offset ?: -1

    /**
     * This signals that the raw chunk (serialized data) as invalid, so that
     * it's regenerated on write. This should be called for the (infrequent)
     * case of chunks that were copied from a PngReader and we want to manually
     * modify it.
     */
    fun invalidateRawData() {
        raw = null
    }

    /**
     * see [ChunkOrderingConstraint]
     */
    abstract val orderingConstraint: ChunkOrderingConstraint
    override fun toString(): String {
        return "chunk id= " + id + " (len=" + len + " offset=" + offset + ")"
    }

    companion object {
        const val ID_FDAT = "fdAT"
        const val ID_FCTL = "fcTL"
    }
}