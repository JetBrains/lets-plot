/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")
package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.Zip

// see http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html
// http://www.w3.org/TR/PNG/#5Chunk-naming-conventions
// http://www.w3.org/TR/PNG/#table53

/**
 * Helper methods and constants related to Chunk processing.
 *
 *
 * This should only be of interest to developers doing special chunk processing
 * or extending the ChunkFactory
 */
internal object ChunkHelper {
    const val IHDR = "IHDR"
    const val PLTE = "PLTE"
    const val IDAT = "IDAT"
    const val IEND = "IEND"
    const val cHRM = "cHRM"
    const val gAMA = "gAMA"
    const val iCCP = "iCCP"
    const val sBIT = "sBIT"
    const val sRGB = "sRGB"
    const val bKGD = "bKGD"
    const val hIST = "hIST"
    const val tRNS = "tRNS"
    const val pHYs = "pHYs"
    const val sPLT = "sPLT"
    const val tIME = "tIME"
    const val iTXt = "iTXt"
    const val tEXt = "tEXt"
    const val zTXt = "zTXt"
    val b_IHDR = toBytesLatin1(IHDR)
    val b_PLTE = toBytesLatin1(PLTE)
    val b_IDAT = toBytesLatin1(IDAT)
    val b_IEND = toBytesLatin1(IEND)

    /*
	 * static auxiliary buffer. any method that uses this should synchronize against
	 * this
	 */
    private val tmpbuffer = ByteArray(4096)

    /**
     * Converts to bytes using Latin1 (ISO-8859-1)
     */
    fun toBytesLatin1(x: String): ByteArray {
        // TODO: check me
        //return x.getBytes(PngHelperInternal.charsetLatin1)
        return x.encodeToByteArray()
    }

    /**
     * Converts to String using Latin1 (ISO-8859-1)
     */
    fun toStringLatin1(x: ByteArray): String {
        // TODO: check me
        //return String(x, PngHelperInternal.charsetLatin1)
        return x.decodeToString()
    }

    /**
     * Converts to String using Latin1 (ISO-8859-1)
     */
    fun toStringLatin1(x: ByteArray, offset: Int, len: Int): String {
        // TODO: check me
        //return String(x, offset, len, PngHelperInternal.charsetLatin1)
        return x.decodeToString(offset, offset + len)
    }

    /**
     * Converts to bytes using UTF-8
     */
    fun toBytesUTF8(x: String): ByteArray {
        // TODO: check me
        //return x.getBytes(PngHelperInternal.charsetUTF8)
        return x.encodeToByteArray()
    }

    /**
     * Converts to string using UTF-8
     */
    fun toStringUTF8(x: ByteArray): String {
        // TODO: check me
        //return String(x, PngHelperInternal.charsetUTF8)
        return x.decodeToString()
    }

    /**
     * Converts to string using UTF-8
     */
    fun toStringUTF8(x: ByteArray, offset: Int, len: Int): String {
        // TODO: check me
        //return String(x, offset, len, PngHelperInternal.charsetUTF8)
        return x.decodeToString(offset, offset + len)
    }

    /**
     * critical chunk : first letter is uppercase
     */
    fun isCritical(id: String): Boolean {
        //return java.lang.Character.isUpperCase(id[0])
        return id[0].isUpperCase()
    }

    /**
     * public chunk: second letter is uppercase
     */
    fun isPublic(id: String): Boolean { //
        //return java.lang.Character.isUpperCase(id[1])
        return id[1].isUpperCase()
    }

    /**
     * Safe to copy chunk: fourth letter is lower case
     */
    fun isSafeToCopy(id: String): Boolean {
        //return !java.lang.Character.isUpperCase(id[3])
        return !id[3].isUpperCase()
    }

    /**
     * "Unknown" just means that our chunk factory (even when it has been
     * augmented by client code) did not recognize its id
     */
    fun isUnknown(c: PngChunk?): Boolean {
        return c is PngChunkUNKNOWN
    }

    /**
     * Finds position of null byte in array
     *
     * @param b
     * @return -1 if not found
     */
    fun posNullByte(b: ByteArray): Int {
        for (i in b.indices) if (b[i].toInt() == 0) return i
        return -1
    }

    /**
     * Decides if a chunk should be loaded, according to a ChunkLoadBehaviour
     *
     * @param id
     * @param behav
     * @return true/false
     */
    fun shouldLoad(id: String, behav: ChunkLoadBehaviour): Boolean {
        if (isCritical(id)) return true
        return when (behav) {
            ChunkLoadBehaviour.LOAD_CHUNK_ALWAYS -> true
            ChunkLoadBehaviour.LOAD_CHUNK_IF_SAFE -> isSafeToCopy(id)
            ChunkLoadBehaviour.LOAD_CHUNK_NEVER -> false
            ChunkLoadBehaviour.LOAD_CHUNK_MOST_IMPORTANT -> id == PngChunkTRNS.ID
        }
    }

    fun compressBytes(ori: ByteArray, compress: Boolean): ByteArray {
        return compressBytes(ori, 0, ori.size, compress)
    }

    fun compressBytes(ori: ByteArray, offset: Int, len: Int, compress: Boolean): ByteArray {
        return Zip.compressBytes(ori, offset, len, compress)
    }


    /**
     * Returns only the chunks that "match" the predicate
     *
     * See also trimList()
     */
    fun filterList(target: List<PngChunk>, predicateKeep: ChunkPredicate): List<PngChunk> {
        val result = mutableListOf<PngChunk>()
        for (element in target) {
            if (predicateKeep.match(element)) {
                result.add(element)
            }
        }
        return result
    }

    /**
     * Remove (in place) the chunks that "match" the predicate
     *
     * See also filterList
     */
    fun trimList(target: MutableList<PngChunk>, predicateRemove: ChunkPredicate): Int {
        val it: MutableIterator<PngChunk> = target.iterator()
        var cont = 0
        while (it.hasNext()) {
            val c: PngChunk = it.next()
            if (predicateRemove.match(c)) {
                it.remove()
                cont++
            }
        }
        return cont
    }

    /**
     * Adhoc criteria: two ancillary chunks are "equivalent" ("practically same
     * type") if they have same id and (perhaps, if multiple are allowed) if the
     * match also in some "internal key" (eg: key for string values, palette for
     * sPLT, etc)
     *
     * When we use this method, we implicitly assume that we don't allow/expect
     * two "equivalent" chunks in a single PNG
     *
     * Notice that the use of this is optional, and that the PNG standard
     * actually allows text chunks that have same key
     *
     * @return true if "equivalent"
     */
    fun equivalent(c1: PngChunk?, c2: PngChunk?): Boolean {
        if (c1 === c2) return true
        if (c1 == null || c2 == null || c1.id != c2.id) return false
        if (c1.crit) return false
        // same id
        if (c1::class != c2::class) return false // should not happen
        if (!c2.allowsMultiple()) return true
        if (c1 is PngChunkTextVar) {
            return c1.key.equals((c2 as PngChunkTextVar?)?.key)
        }
        return if (c1 is PngChunkSPLT) {
            c1.palName.equals((c2 as PngChunkSPLT?)?.palName)
        } else false
        // unknown chunks that allow multiple? consider they don't match
    }

    /**
     * Convert four bytes to String (chunk id)
     */
    fun idFromBytes(buf: ByteArray?, offset: Int): String {
        return if (buf == null || buf.size < 4 + offset) "?" else toStringLatin1(
            buf,
            offset,
            4
        )
    }
}