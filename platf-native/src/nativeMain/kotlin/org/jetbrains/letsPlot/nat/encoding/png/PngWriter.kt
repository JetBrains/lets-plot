/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused", "KDocUnresolvedReference")
package org.jetbrains.letsPlot.nat.encoding.png

import org.jetbrains.letsPlot.nat.encoding.png.chunks.*
import org.jetbrains.letsPlot.nat.encoding.png.pixels.PixelsWriter
import org.jetbrains.letsPlot.nat.encoding.png.pixels.PixelsWriterDefault

/**
 * Writes a PNG image, line by line.
 */
open class PngWriter(outputStream: OutputPngStream, imgInfo: ImageInfo) {
    val imgInfo: ImageInfo

    /**
     * last writen row number, starting from 0
     */
    private var rowNum = -1
    private val chunksList: ChunksListForWrite
    private val metadata: PngMetadata

    /**
     * Current chunk group, (0-6) already written or currently writing (this is
     * advanced when just starting to write the new group, not when finalizing
     * the previous)
     *
     *
     * see [ChunksList]
     */
    private var currentChunkGroup = -1
    private val passes = 1 // Some writes might require two passes (NOT USED STILL)
    private var currentpass = 0 // numbered from 1
    private var shouldCloseStream = true
    private var idatMaxSize = 0 // 0=use default (PngIDatChunkOutputStream 64k)

    // private PngIDatChunkOutputStream datStream;
    private var pixelsWriter: PixelsWriter
    private val os: OutputPngStream
    private var copyFromPredicate: ChunkPredicate? = null
    private var copyFromList: ChunksList? = null


    init {
        os = outputStream
        this.imgInfo = imgInfo
        // prealloc
        chunksList = ChunksListForWrite(imgInfo)
        metadata = PngMetadata(chunksList)
        pixelsWriter = PixelsWriterDefault(imgInfo)
        setCompLevel(9)
    }

    private fun initIdat() { // this triggers the writing of first chunks
        pixelsWriter.setOs(os)
        pixelsWriter.setIdatMaxSize(idatMaxSize)
        writeSignatureAndIHDR()
        writeFirstChunks()
    }

    private fun writeEndChunk() {
        currentChunkGroup = ChunksList.CHUNK_GROUP_6_END
        val c = PngChunkIEND(imgInfo)
        c.createRawChunk().writeChunk(os)
        chunksList.chunks.add(c)
    }

    private fun writeFirstChunks() {
        if (currentChunkGroup >= ChunksList.CHUNK_GROUP_4_IDAT) return
        currentChunkGroup = ChunksList.CHUNK_GROUP_1_AFTERIDHR
        queueChunksFromOther()
        chunksList.writeChunks(os, currentChunkGroup)
        currentChunkGroup = ChunksList.CHUNK_GROUP_2_PLTE
        val nw = chunksList.writeChunks(os, currentChunkGroup)
        if (nw > 0 && imgInfo.greyscale) throw PngjOutputException("cannot write palette for this format")
        if (nw == 0 && imgInfo.indexed) throw PngjOutputException("missing palette")
        currentChunkGroup = ChunksList.CHUNK_GROUP_3_AFTERPLTE
        chunksList.writeChunks(os, currentChunkGroup)
    }

    private fun writeLastChunks() { // not including end
        currentChunkGroup = ChunksList.CHUNK_GROUP_5_AFTERIDAT
        queueChunksFromOther()
        chunksList.writeChunks(os, currentChunkGroup)
        // should not be unwriten chunks
        val pending: List<PngChunk> = chunksList.getQueuedChunks()
        if (pending.isNotEmpty()) throw PngjOutputException(
            pending.size.toString() + " chunks were not written! Eg: " + pending[0].toString()
        )
    }

    /**
     * Write id signature and also "IHDR" chunk
     */
    private fun writeSignatureAndIHDR() {
        PngHelperInternal.writeBytes(os, PngHelperInternal.pngIdSignature) // signature
        currentChunkGroup = ChunksList.CHUNK_GROUP_0_IDHR
        val ihdr = PngChunkIHDR(imgInfo)
        // http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html
        ihdr.createRawChunk().writeChunk(os)
        chunksList.chunks.add(ihdr)
    }

    private fun queueChunksFromOther() {
        if (copyFromList == null || copyFromPredicate == null) return
        val idatDone: Boolean =
            currentChunkGroup >= ChunksList.CHUNK_GROUP_4_IDAT // we assume this method is not either
        // before
        // or after the IDAT writing, not in the
        // middle!
        for (chunk in copyFromList!!.chunks) {
            if (chunk.raw!!.data == null) continue  // we cannot copy skipped chunks?
            val groupOri: Int = chunk.chunkGroup
            if (groupOri <= ChunksList.CHUNK_GROUP_4_IDAT && idatDone) continue
            if (groupOri >= ChunksList.CHUNK_GROUP_4_IDAT && !idatDone) continue
            if (chunk.crit && chunk.id != PngChunkPLTE.ID) continue  // critical chunks (except perhaps PLTE) are never
            // copied
            val copy: Boolean = copyFromPredicate!!.match(chunk)
            if (copy) {
                // but if the chunk is already queued or writen, it's ommited!
                if (chunksList.getEquivalent(chunk).isEmpty() && chunksList.getQueuedEquivalent(chunk).isEmpty()) {
                    chunksList.queue(chunk)
                }
            }
        }
    }

    /**
     * Queues an ancillary chunk for writing.
     *
     *
     * If a "equivalent" chunk is already queued (see
     * [), this overwrites it.][ChunkHelper.equivalent]
     */
    fun queueChunk(chunk: PngChunk) {
        for (other in chunksList.getQueuedEquivalent(chunk)) {
            getChunksList().removeChunk(other)
        }
        chunksList.queue(chunk)
    }
    /**
     * Sets an origin (typically from a [PngReader]) of Chunks to be
     * copied. This should be called only once, before starting writing the
     * rows. It doesn't matter the current state of the PngReader reading, this
     * is a live object and what matters is that when the writer writes the
     * pixels (IDAT) the reader has already read them, and that when the writer
     * ends, the reader is already ended (all this is very natural).
     *
     *
     * Apart from the copyMask, there is some addional heuristics:
     *
     *
     * - The chunks will be queued, but will be written as late as possible
     * (unless you explicitly set priority=true)
     *
     *
     * - The chunk will not be queued if an "equivalent" chunk was already
     * queued explicitly. And it will be overwriten another is queued
     * explicitly.
     *
     * @param chunks
     * @param copyMask
     * Some bitmask from [ChunkCopyBehaviour]
     *
     * @see .copyChunksFrom
     */
    /**
     * Copy all chunks from origin. See [.copyChunksFrom]
     * for more info
     */
    fun copyChunksFrom(chunks: ChunksList?, copyMask: Int = ChunkCopyBehaviour.COPY_ALL) {
        copyChunksFrom(chunks, ChunkCopyBehaviour.createPredicate(copyMask, imgInfo))
    }

    /**
     * Copy chunks from origin depending on some [ChunkPredicate]
     *
     * @param chunks
     * @param predicate
     * The chunks (ancillary or PLTE) will be copied if and only if
     * predicate matches
     *
     * @see .copyChunksFrom
     */
    private fun copyChunksFrom(chunks: ChunksList?, predicate: ChunkPredicate?) {
        if (copyFromList != null && chunks != null) println("copyChunksFrom should only be called once")
        if (predicate == null) throw PngjOutputException("copyChunksFrom requires a predicate")
        copyFromList = chunks
        copyFromPredicate = predicate
    }

    /**
     * Computes compressed size/raw size, approximate.
     *
     *
     * Actually: compressed size = total size of IDAT data , raw size =
     * uncompressed pixel bytes = rows * (bytesPerRow + 1).
     *
     * This must be called after pngw.end()
     */
    fun computeCompressionRatio(): Double {
        if (currentChunkGroup < ChunksList.CHUNK_GROUP_5_AFTERIDAT) throw PngjOutputException("must be called after end()")
        return pixelsWriter.compression
    }

    /**
     * Finalizes all the steps and closes the stream. This must be called after
     * writing the lines. Idempotent
     */
    fun end() {
        if (rowNum != imgInfo.rows - 1 || !pixelsWriter.isDone) throw PngjOutputException("all rows have not been written")
        try {
            pixelsWriter.close()
            if (currentChunkGroup < ChunksList.CHUNK_GROUP_5_AFTERIDAT) writeLastChunks()
            if (currentChunkGroup < ChunksList.CHUNK_GROUP_6_END) writeEndChunk()
        } finally {
            close()
        }
    }

    /**
     * Closes and releases resources
     *
     *
     * This is normally called internally from [.end], you should only
     * call this for aborting the writing and release resources (close the
     * stream).
     *
     *
     * Idempotent and secure - never throws exceptions
     */
    fun close() {
        pixelsWriter.close()
        if (shouldCloseStream) try {
            os.close()
        } catch (e: Exception) {
            println("Error closing writer $e")
        }
    }

    /**
     * returns the chunks list (queued and writen chunks)
     */
    fun getChunksList(): ChunksListForWrite {
        return chunksList
    }

    /**
     * Retruns a high level wrapper over for metadata handling
     */
    fun getMetadata(): PngMetadata {
        return metadata
    }

    /**
     * Sets internal prediction filter type, or strategy to choose it.
     *
     *
     * This must be called just after constructor, before starting writing.
     *
     *
     */
    fun setFilterType(filterType: FilterType) {
        pixelsWriter.filterType = filterType
    }

    /**
     * This is kept for backwards compatibility, now the PixelsWriter object
     * should be used for setting compression/filtering options
     *
     * @see PixelsWriter.setCompressionFactor
     * @param compLevel
     * between 0 (no compression, max speed) and 9 (max compression)
     */
    private fun setCompLevel(complevel: Int) {
        pixelsWriter.deflaterCompLevel = complevel
    }

    /**
     *
     */
    fun setFilterPreserve(filterPreserve: Boolean) {
        if (filterPreserve) {
            pixelsWriter.filterType = FilterType.FILTER_PRESERVE
        }
    }

    /**
     * Sets maximum size of IDAT fragments. Incrementing this from the default
     * has very little effect on compression and increments memory usage. You
     * should rarely change this.
     *
     *
     *
     * @param idatMaxSize
     * default=0 : use defaultSize (32K)
     */
    fun setIdatMaxSize(idatMaxSize: Int) {
        this.idatMaxSize = idatMaxSize
    }

    /**
     * If true, output stream will be closed after ending write
     *
     *
     * default=true
     */
    fun setShouldCloseStream(shouldCloseStream: Boolean) {
        this.shouldCloseStream = shouldCloseStream
    }

    /**
     * Writes next row, does not check row number.
     *
     * @param imgline
     */
    fun writeRow(imgline: IImageLine) {
        writeRow(imgline, rowNum + 1)
    }

    /**
     * Writes the full set of row. The ImageLineSet should contain (allow to
     * acces) imgInfo.rows
     */
    fun writeRows(imglines: IImageLineSet<out IImageLine>) {
        for (i in 0 until imgInfo.rows) writeRow(imglines.getImageLineRawNum(i))
    }

    fun writeRow(imgline: IImageLine, rownumber: Int) {
        @Suppress("NAME_SHADOWING")
        var rownumber = rownumber
        rowNum++
        if (rowNum == imgInfo.rows) rowNum = 0
        if (rownumber == imgInfo.rows) rownumber = 0
        if (rownumber >= 0 && rowNum != rownumber) throw PngjOutputException("rows must be written in order: expected:$rowNum passed:$rownumber")
        if (rowNum == 0) currentpass++
        if (rownumber == 0 && currentpass == passes) {
            initIdat()
            currentChunkGroup = ChunksList.CHUNK_GROUP_4_IDAT // we just begin writing IDAT
        }
        val rowb: ByteArray = pixelsWriter.getCurentRowb()
        imgline.writeToPngRaw(rowb)
        pixelsWriter.processRow(rowb)
    }

    /**
     * Utility method, uses internaly a ImageLineInt
     */
    fun writeRowInt(buf: IntArray?) {
        writeRow(ImageLineInt(imgInfo, buf))
    }

}