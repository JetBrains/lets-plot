/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")
package org.jetbrains.letsPlot.util.pngj.chunks

import org.jetbrains.letsPlot.util.pngj.ImageInfo
import org.jetbrains.letsPlot.util.pngj.OutputPngStream
import org.jetbrains.letsPlot.util.pngj.PngjException
import org.jetbrains.letsPlot.util.pngj.PngjOutputException

class ChunksListForWrite(imfinfo: ImageInfo) : ChunksList(imfinfo) {
    /**
     * chunks not yet writen - does not include IHDR, IDAT, END, perhaps yes
     * PLTE
     */
    private val queuedChunks: MutableList<PngChunk> = mutableListOf()

    // redundant, just for eficciency
    private val alreadyWrittenKeys: MutableMap<String, Int> = mutableMapOf()

    /**
     * Same as getById(), but looking in the queued chunks
     */
    fun getQueuedById(id: String): List<PngChunk> {
        return getQueuedById(id, null)
    }

    /**
     * Same as getById(), but looking in the queued chunks
     */
    private fun getQueuedById(id: String, innerid: String?): List<PngChunk> {
        return getXById(queuedChunks, id, innerid)
    }

    /**
     * Same as getById1(), but looking in the queued chunks
     */
    private fun getQueuedById1(id: String, innerid: String?, failIfMultiple: Boolean): PngChunk? {
        val list: List<PngChunk> = getQueuedById(id, innerid)
        if (list.isEmpty()) return null
        if (list.size > 1 && (failIfMultiple || !list[0].allowsMultiple())) throw PngjException("unexpected multiple chunks id=$id")
        return list[list.size - 1]
    }

    /**
     * Same as getById1(), but looking in the queued chunks
     */
    private fun getQueuedById1(id: String, failIfMultiple: Boolean): PngChunk? {
        return getQueuedById1(id, null, failIfMultiple)
    }

    /**
     * Same as getById1(), but looking in the queued chunks
     */
    fun getQueuedById1(id: String): PngChunk? {
        return getQueuedById1(id, false)
    }

    /**
     * Finds all chunks "equivalent" to this one
     *
     * @param c2
     * @return Empty if nothing found
     */
    fun getQueuedEquivalent(c2: PngChunk?): List<PngChunk> {
        return ChunkHelper.filterList(queuedChunks, object : ChunkPredicate {
            override fun match(chunk: PngChunk): Boolean {
                return ChunkHelper.equivalent(chunk, c2)
            }
        })
    }

    /**
     * Remove Chunk: only from queued
     *
     * WARNING: this depends on c.equals() implementation, which is
     * straightforward for SingleChunks. For MultipleChunks, it will normally
     * check for reference equality!
     */
    fun removeChunk(c: PngChunk?): Boolean {
        return if (c == null) false else queuedChunks.remove(c)
    }

    /**
     * Adds chunk to queue
     *
     * If there
     *
     * @param c
     */
    fun queue(c: PngChunk): Boolean {
        queuedChunks.add(c)
        return true
    }

    fun writeChunks(os: OutputPngStream, currentGroup: Int): Int {
        var cont = 0
        val it: MutableIterator<PngChunk> = queuedChunks.iterator()
        while (it.hasNext()) {
            val c: PngChunk = it.next()
            if (!shouldWrite(c, currentGroup)) continue
            if (ChunkHelper.isCritical(c.id) && c.id != ChunkHelper.PLTE) throw PngjOutputException("bad chunk queued: $c")
            if (alreadyWrittenKeys.containsKey(c.id) && !c.allowsMultiple()) throw PngjOutputException("duplicated chunk does not allow multiple: $c")
            c.write(os)
            chunks.add(c)
            alreadyWrittenKeys[c.id] = if (alreadyWrittenKeys.containsKey(c.id)) alreadyWrittenKeys[c.id]!! + 1 else 1
            c.chunkGroup = currentGroup
            it.remove()
            cont++
        }
        return cont
    }

    /**
     * warning: this is NOT a copy, do not modify
     */
    fun getQueuedChunks(): MutableList<PngChunk> {
        return queuedChunks
    }

    override fun toString(): String {
        return "ChunkList: written: " + chunks.size + " queue: " + queuedChunks.size
    }

    /**
     * for debugging
     */
    override fun toStringFull(): String {
        val sb = StringBuilder(toString())
        sb.append("\n Written:\n")
        for (chunk in chunks) {
            sb.append(chunk).append(
                """ G=${chunk.chunkGroup}
"""
            )
        }
        if (queuedChunks.isNotEmpty()) {
            sb.append(" Queued:\n")
            for (chunk in queuedChunks) {
                sb.append(chunk).append("\n")
            }
        }
        return sb.toString()
    }

    companion object {
        /**
         * this should be called only for ancillary chunks and PLTE (groups 1 - 3 -
         * 5)
         */
        private fun shouldWrite(c: PngChunk, currentGroup: Int): Boolean {
            if (currentGroup == CHUNK_GROUP_2_PLTE) return c.id == ChunkHelper.PLTE
            if (currentGroup % 2 == 0) throw PngjOutputException("bad chunk group?")
            val minChunkGroup: Int
            val maxChunkGroup: Int
            if (c.orderingConstraint.mustGoBeforePLTE()) {
                maxChunkGroup = CHUNK_GROUP_1_AFTERIDHR
                minChunkGroup = maxChunkGroup
            } else if (c.orderingConstraint.mustGoBeforeIDAT()) {
                maxChunkGroup = CHUNK_GROUP_3_AFTERPLTE
                minChunkGroup =
                    if (c.orderingConstraint.mustGoAfterPLTE()) CHUNK_GROUP_3_AFTERPLTE else CHUNK_GROUP_1_AFTERIDHR
            } else {
                maxChunkGroup = CHUNK_GROUP_5_AFTERIDAT
                minChunkGroup = CHUNK_GROUP_1_AFTERIDHR
            }
            var preferred = maxChunkGroup
            if (c.hasPriority()) preferred = minChunkGroup
            if (ChunkHelper.isUnknown(c) && c.chunkGroup > 0) preferred = c.chunkGroup
            if (currentGroup == preferred) return true
            return currentGroup in (preferred + 1)..maxChunkGroup
        }
    }
}