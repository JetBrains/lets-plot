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

@file:Suppress("unused")
package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.nat.encoding.png.PngjException


/**
 * We consider "image metadata" every info inside the image except for the most
 * basic image info (IHDR chunk - ImageInfo class) and the pixels values.
 *
 *
 * This includes the palette (if present) and all the ancillary chunks
 *
 *
 * This class provides a wrapper over the collection of chunks of a image (read
 * or to write) and provides some high level methods to access them
 */
class PngMetadata(chunks: ChunksList) {
    private val chunkList: ChunksList
    private var readonly = false

    init {
        chunkList = chunks
        readonly = chunks !is ChunksListForWrite
    }

    /**
     * Queues the chunk at the writer
     *
     *
     * lazyOverwrite: if true, checks if there is a queued "equivalent" chunk
     * and if so, overwrites it. However if that not check for already written
     * chunks.
     */
    fun queueChunk(c: PngChunk, lazyOverwrite: Boolean = true) {
        val cl: ChunksListForWrite = chunkListW
        if (readonly) throw PngjException("cannot set chunk : readonly metadata")
        if (lazyOverwrite) {
            ChunkHelper.trimList(cl.getQueuedChunks(), object : ChunkPredicate {
                override fun match(chunk: PngChunk): Boolean {
                    return ChunkHelper.equivalent(c, chunk)
                }
            })
        }
        cl.queue(c)
    }

    private val chunkListW: ChunksListForWrite
        get() = chunkList as ChunksListForWrite
    // ///// high level utility methods follow ////////////
    // //////////// DPI
    /**
     * returns -1 if not found or dimension unknown
     */
    val dpi: DoubleArray
        get() {
            val c: PngChunk? = chunkList.getById1(ChunkHelper.pHYs, true)
            return if (c == null) doubleArrayOf(-1.0, -1.0) else (c as PngChunkPHYS).asDpi2
        }

    fun setDpi(x: Double) {
        setDpi(x, x)
    }

    private fun setDpi(x: Double, y: Double) {
        val c = PngChunkPHYS(chunkList.imageInfo)
        c.setAsDpi2(x, y)
        queueChunk(c)
    }
    // //////////// TIME
    /**
     * Creates a time chunk with current time, less secsAgo seconds
     *
     *
     *
     * @return Returns the created-queued chunk, just in case you want to
     * examine or modify it
     */
    fun setTimeNow(dateTime: DateTime): PngChunkTIME {
        val c = PngChunkTIME(chunkList.imageInfo)
        c.setNow(dateTime)
        queueChunk(c)
        return c
    }

    /**
     * Creates a time chunk with diven date-time
     *
     *
     *
     * @return Returns the created-queued chunk, just in case you want to
     * examine or modify it
     */
    fun setTimeYMDHMS(yearx: Int, monx: Int, dayx: Int, hourx: Int, minx: Int, secx: Int): PngChunkTIME {
        val c = PngChunkTIME(chunkList.imageInfo)
        c.setYMDHMS(yearx, monx, dayx, hourx, minx, secx)
        queueChunk(c, true)
        return c
    }

    /**
     * null if not found
     */
    val time: PngChunkTIME?
        get() = chunkList.getById1(ChunkHelper.tIME) as? PngChunkTIME
    val timeAsString: String
        get() {
            return time?.asString ?: ""
        }
    // //////////// TEXT
    /**
     * Creates a text chunk and queue it.
     *
     *
     *
     * @param k
     * : key (latin1)
     * @param val
     * (arbitrary, should be latin1 if useLatin1)
     * @param useLatin1
     * @param compress
     * @return Returns the created-queued chunks, just in case you want to
     * examine, touch it
     */
    private fun setText(k: String?, `val`: String?, useLatin1: Boolean, compress: Boolean): PngChunkTextVar {
        if (compress && !useLatin1) throw PngjException("cannot compress non latin text")
        val c: PngChunkTextVar = if (useLatin1) {
            if (compress) {
                PngChunkZTXT(chunkList.imageInfo)
            } else {
                PngChunkTEXT(chunkList.imageInfo)
            }
        } else {
            PngChunkITXT(chunkList.imageInfo)
            //((PngChunkITXT) c).setTranslatedTag(k); // we use the same orig tag (this is not quite right)
        }
        c.setKeyVal(k, `val`)
        queueChunk(c, true)
        return c
    }

    fun setText(k: String?, `val`: String?): PngChunkTextVar {
        return setText(k, `val`, useLatin1 = false, compress = false)
    }

    /**
     * gets all text chunks with a given key
     *
     *
     * returns null if not found
     *
     *
     * Warning: this does not check the "lang" key of iTxt
     */
    private fun getTxtsForKey(k: String?): List<PngChunkTextVar> {
        val c = mutableListOf<PngChunk>()
        c.addAll(chunkList.getById(ChunkHelper.tEXt, k))
        c.addAll(chunkList.getById(ChunkHelper.zTXt, k))
        c.addAll(chunkList.getById(ChunkHelper.iTXt, k))
        return c.map { it as PngChunkTextVar }
    }

    /**
     * Returns empty if not found, concatenated (with newlines) if multiple! -
     * and trimmed
     *
     *
     * Use getTxtsForKey() if you don't want this behaviour
     */
    fun getTxtForKey(k: String?): String {
        val li: List<PngChunkTextVar> = getTxtsForKey(k)
        if (li.isEmpty()) return ""
        val t = StringBuilder()
        for (c in li) t.append(c.value).append("\n")
        return t.toString().trim { it <= ' ' }
    }

    /**
     * Returns the palette chunk, if present
     *
     * @return null if not present
     */
    val pLTE: PngChunkPLTE
        get() = chunkList.getById1(PngChunkPLTE.ID) as PngChunkPLTE

    /**
     * Creates a new empty palette chunk, queues it for write and return it to
     * the caller, who should fill its entries
     */
    fun createPLTEChunk(): PngChunkPLTE {
        val plte = PngChunkPLTE(chunkList.imageInfo)
        queueChunk(plte)
        return plte
    }

    /**
     * Returns the TRNS chunk, if present
     *
     * @return null if not present
     */
    val tRNS: PngChunkTRNS?
        get() = chunkList.getById1(PngChunkTRNS.ID) as? PngChunkTRNS

    /**
     * Creates a new empty TRNS chunk, queues it for write and return it to the
     * caller, who should fill its entries
     */
    fun createTRNSChunk(): PngChunkTRNS {
        val trns = PngChunkTRNS(chunkList.imageInfo)
        queueChunk(trns)
        return trns
    }
}