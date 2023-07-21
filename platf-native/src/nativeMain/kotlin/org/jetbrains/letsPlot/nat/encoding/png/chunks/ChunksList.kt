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

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * All chunks that form an image, read or to be written.
 *
 *
 * chunks include all chunks, but IDAT is a single pseudo chunk without data
 */
open class ChunksList(imfinfo: ImageInfo) {
    /**
     * All chunks, read (or written)
     *
     * But IDAT is a single pseudo chunk without data
     */
    /**
     * WARNING: this does NOT return a copy, but the list itself. The called
     * should not modify this directly! Don't use this to manipulate the chunks.
     */
    val chunks: MutableList<PngChunk> = mutableListOf()
    val imageInfo // only required for writing
            : ImageInfo
    var withPlte = false

    init {
        imageInfo = imfinfo
    }

    /**
     * Adds chunk in next position. This is used onyl by the pngReader
     */
    fun appendReadChunk(chunk: PngChunk, chunkGroup: Int) {
        chunk.chunkGroup = chunkGroup
        chunks.add(chunk)
        if (chunk.id == PngChunkPLTE.ID) withPlte = true
    }

    /**
     * All chunks with this ID
     *
     * @param id
     * @return List, empty if none
     */
    fun getById(id: String): List<PngChunk> {
        return getById(id, null)
    }

    /**
     * If innerid!=null and the chunk is PngChunkTextVar or PngChunkSPLT, it's
     * filtered by that id
     *
     * @param id
     * @return innerid Only used for text and SPLT chunks
     * @return List, empty if none
     */
    fun getById(id: String, innerid: String?): List<PngChunk> {
        return getXById(chunks, id, innerid)
    }

    /**
     * Returns only one chunk
     *
     * @param id
     * @return First chunk found, null if not found
     */
    fun getById1(id: String): PngChunk? {
        return getById1(id, false)
    }

    /**
     * Returns only one chunk or null if nothing found - does not include queued
     *
     *
     * If more than one chunk is found, then an exception is thrown
     * (failifMultiple=true or chunk is single) or the last one is returned
     * (failifMultiple=false)
     */
    fun getById1(id: String, failIfMultiple: Boolean): PngChunk? {
        return getById1(id, null, failIfMultiple)
    }

    /**
     * Returns only one chunk or null if nothing found - does not include queued
     *
     *
     * If more than one chunk (after filtering by inner id) is found, then an
     * exception is thrown (failifMultiple=true or chunk is single) or the last
     * one is returned (failifMultiple=false)
     */
    fun getById1(id: String, innerid: String?, failIfMultiple: Boolean): PngChunk? {
        val list: List<PngChunk> = getById(id, innerid)
        if (list.isEmpty()) return null
        if (list.size > 1 && (failIfMultiple || !list[0].allowsMultiple())) throw PngjException("unexpected multiple chunks id=$id")
        return list[list.size - 1]
    }

    /**
     * Finds all chunks "equivalent" to this one
     *
     * @param c2
     * @return Empty if nothing found
     */
    fun getEquivalent(c2: PngChunk?): List<PngChunk> {
        return ChunkHelper.filterList(chunks, object : ChunkPredicate {
            override fun match(chunk: PngChunk): Boolean {
                return ChunkHelper.equivalent(chunk, c2)
            }
        })
    }

    override fun toString(): String {
        return "ChunkList: read: " + chunks.size
    }

    /**
     * for debugging
     */
    open fun toStringFull(): String {
        val sb = StringBuilder(toString())
        sb.append("\n Read:\n")
        for (chunk in chunks) {
            sb.append(chunk).append(
                """ G=${chunk.chunkGroup}
"""
            )
        }
        return sb.toString()
    }

    companion object {
        // ref: http://www.w3.org/TR/PNG/#table53
        const val CHUNK_GROUP_0_IDHR = 0 // required - single
        const val CHUNK_GROUP_1_AFTERIDHR = 1 // optional - multiple
        const val CHUNK_GROUP_2_PLTE = 2 // optional - single
        const val CHUNK_GROUP_3_AFTERPLTE = 3 // optional - multple
        const val CHUNK_GROUP_4_IDAT = 4 // required (single pseudo chunk)
        const val CHUNK_GROUP_5_AFTERIDAT = 5 // optional - multple
        const val CHUNK_GROUP_6_END = 6 // only 1 chunk - requried

        protected fun getXById(list: List<PngChunk>, id: String, innerid: String?): List<PngChunk> {
            return if (innerid == null) ChunkHelper.filterList(list, object : ChunkPredicate {
                override fun match(chunk: PngChunk): Boolean {
                    return chunk.id == id
                }
            }) else ChunkHelper.filterList(list, object : ChunkPredicate {
                override fun match(chunk: PngChunk): Boolean {
                    if (chunk.id != id) return false
                    if (chunk is PngChunkTextVar && !chunk.key.equals(innerid)) return false
                    return !(chunk is PngChunkSPLT && !chunk.palName.equals(innerid))
                }
            })
        }
    }
}