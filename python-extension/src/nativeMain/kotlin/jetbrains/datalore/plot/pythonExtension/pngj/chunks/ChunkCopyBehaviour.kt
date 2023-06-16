/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.ImageInfo
import jetbrains.datalore.plot.pythonExtension.pngj.chunks.ChunkHelper.isUnknown

internal object ChunkCopyBehaviour {
    /** Don't copy anything  */
    const val COPY_NONE = 0

    /** copy the palette  */
    const val COPY_PALETTE = 1

    /** copy all 'safe to copy' chunks  */
    const val COPY_ALL_SAFE = 1 shl 2

    /**
     * copy all, including palette
     */
    const val COPY_ALL = 1 shl 3 // includes palette!

    /**
     * Copy PHYS chunk (physical resolution)
     */
    const val COPY_PHYS = 1 shl 4 // dpi

    /**
     * Copy al textual chunks.
     */
    const val COPY_TEXTUAL = 1 shl 5 // all textual types

    /**
     * Copy TRNS chunk
     */
    const val COPY_TRANSPARENCY = 1 shl 6 //

    /**
     * Copy unknown chunks (unknown by our factory)
     */
    const val COPY_UNKNOWN = 1 shl 7 // all unknown (by the factory!)

    /**
     * Copy almost all: excepts only HIST (histogram) TIME and TEXTUAL chunks
     */
    const val COPY_ALMOSTALL = 1 shl 8
    private fun maskMatch(v: Int, mask: Int): Boolean {
        return v and mask != 0
    }

    /**
     * Creates a predicate equivalent to the copy mask
     *
     *
     * Given a copy mask (see static fields) and the ImageInfo of the target
     * PNG, returns a predicate that tells if a chunk should be copied.
     *
     *
     * This is a handy helper method, you can also create and set your own
     * predicate
     */
    fun createPredicate(copyFromMask: Int, imgInfo: ImageInfo): ChunkPredicate {
        return object : ChunkPredicate {
            override fun match(chunk: PngChunk): Boolean {
                if (chunk.crit) {
                    if (chunk.id == ChunkHelper.PLTE) {
                        if (imgInfo.indexed && maskMatch(copyFromMask, COPY_PALETTE)) return true
                        if (!imgInfo.greyscale && maskMatch(copyFromMask, COPY_ALL)) return true
                    }
                } else { // ancillary
                    val text = chunk is PngChunkTextVar
                    val safe: Boolean = chunk.safe
                    // notice that these if are not exclusive
                    if (maskMatch(copyFromMask, COPY_ALL)) return true
                    if (safe && maskMatch(copyFromMask, COPY_ALL_SAFE)) return true
                    if (chunk.id == ChunkHelper.tRNS && maskMatch(copyFromMask, COPY_TRANSPARENCY)) return true
                    if (chunk.id == ChunkHelper.pHYs && maskMatch(copyFromMask, COPY_PHYS)) return true
                    if (text && maskMatch(copyFromMask, COPY_TEXTUAL)) return true
                    if (
                        maskMatch(copyFromMask, COPY_ALMOSTALL)
                        && !(
                                isUnknown(chunk)
                                || text
                                || chunk.id == ChunkHelper.hIST
                                || chunk.id == ChunkHelper.tIME
                                )
                    ) return true
                    if (maskMatch(copyFromMask, COPY_UNKNOWN) && isUnknown(chunk)) return true
                }
                return false
            }
        }
    }
}