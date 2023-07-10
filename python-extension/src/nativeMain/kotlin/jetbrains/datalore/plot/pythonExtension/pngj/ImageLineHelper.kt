/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj

import jetbrains.datalore.plot.pythonExtension.pngj.chunks.PngChunkPLTE
import jetbrains.datalore.plot.pythonExtension.pngj.chunks.PngChunkTRNS

/**
 * Bunch of utility static methods to proces an image line at the pixel level.
 *
 *
 * WARNING: this has little testing/optimizing, and this API is not stable. some
 * methods will probably be changed or removed if future releases.
 *
 *
 * WARNING: most methods for getting/setting values work currently only for
 * ImageLine or ImageLineByte
 */
@Suppress("UNUSED_PARAMETER")
internal object ImageLineHelper {
    private val DEPTH_UNPACK_1: IntArray = IntArray(2).also { for (i in 0..1) it[i] = i * 255 }
    private val DEPTH_UNPACK_2: IntArray = IntArray(4).also { for (i in 0..3) it[i] = i * 255 / 3 }
    private val DEPTH_UNPACK_4: IntArray = IntArray(16).also { for (i in 0..15) it[i] = i * 255 / 15 }
    private val DEPTH_UNPACK: Array<IntArray?> = arrayOf(null, DEPTH_UNPACK_1, DEPTH_UNPACK_2, null, DEPTH_UNPACK_4)

    /**
     * Given an indexed line with a palette, unpacks as a RGB array, or RGBA if
     * a non nul PngChunkTRNS chunk is passed
     *
     * @param line
     * ImageLine as returned from PngReader
     * @param pal
     * Palette chunk
     * @param trns
     * Transparency chunk, can be null (absent)
     * @param buf
     * Pre-allocated array, optional
     * @return R G B (A), one sample 0-255 per array element. Ready for
     * pngw.writeRowInt()
     */
    fun palette2rgb(line: ImageLineInt, pal: PngChunkPLTE, trns: PngChunkTRNS?, buf: IntArray?): IntArray {
        return palette2rgb(line, pal, trns, buf, false)
    }

    fun palette2rgb(line: ImageLineInt, pal: PngChunkPLTE, buf: IntArray): IntArray {
        return palette2rgb(line, pal, null, buf, false)
    }

    /** this is not very efficient, only for tests and troubleshooting  */
    fun convert2rgba(line: IImageLineArray, pal: PngChunkPLTE, trns: PngChunkTRNS?, buf: IntArray?): IntArray {
        @Suppress("NAME_SHADOWING")
        var buf = buf
        val imi: ImageInfo = line.imageInfo
        val nsamples: Int = imi.cols * 4
        if (buf == null || buf.size < nsamples) buf = IntArray(nsamples)
        val maxval = if (imi.bitDepth == 16) (1 shl 16) - 1 else 255
        fill(buf, maxval)
        if (imi.indexed) {
            val tlen = trns?.palletteAlpha?.size ?: 0
            for (s in 0 until imi.cols) {
                val index: Int = line.getElem(s)
                pal.getEntryRgb(index, buf, s * 4)
                if (index < tlen) {
                    buf[s * 4 + 3] = trns!!.palletteAlpha[index]
                }
            }
        } else if (imi.greyscale) {
            var unpack: IntArray? = null
            if (imi.bitDepth < 8) unpack = DEPTH_UNPACK[imi.bitDepth]
            var s = 0
            var i = 0
            var p = 0
            while (p < imi.cols) {
                buf[s++] = unpack?.get(line.getElem(i++)) ?: line.getElem(i++)
                buf[s] = buf[s - 1]
                s++
                buf[s] = buf[s - 1]
                s++
                if (imi.channels == 2) buf[s++] = unpack?.get(line.getElem(i++)) ?: line.getElem(i++) else buf[s++] =
                    maxval
                p++
            }
        } else {
            var s = 0
            var i = 0
            var p = 0
            while (p < imi.cols) {
                buf[s++] = line.getElem(i++)
                buf[s++] = line.getElem(i++)
                buf[s++] = line.getElem(i++)
                buf[s++] = if (imi.alpha) line.getElem(i++) else maxval
                p++
            }
        }
        return buf
    }

    fun clampTo_0_255(i: Int): Int {
        return if (i > 255) 255 else if (i < 0) 0 else i
    }


    private fun palette2rgb(
        line: IImageLine, pal: PngChunkPLTE, trns: PngChunkTRNS?, buf: IntArray?, alphaForced: Boolean
    ): IntArray {
        @Suppress("NAME_SHADOWING")
        var buf: IntArray? = buf
        val isalpha = trns != null
        val channels = if (isalpha) 4 else 3
        val linei: ImageLineInt? = if (line is ImageLineInt) line else null
        val lineb: ImageLineByte? = if (line is ImageLineByte) line else null
        val isbyte = lineb != null
        val cols: Int = linei?.imgInfo?.cols ?: lineb!!.imgInfo.cols
        val nsamples = cols * channels
        if (buf == null || buf.size < nsamples) buf = IntArray(nsamples)
        val nindexesWithAlpha = trns?.palletteAlpha?.size ?: 0
        for (c in 0 until cols) {
            val index: Int = if (isbyte) lineb!!.scanline[c].toInt() and 0xFF else linei!!.scanline[c]
            pal.getEntryRgb(index, buf, c * channels)
            if (isalpha) {
                val alpha = if (index < nindexesWithAlpha) trns!!.palletteAlpha[index] else 255
                buf[c * channels + 3] = alpha
            }
        }
        return buf
    }

    fun getMaskForPackedFormats(bitDepth: Int): Int { // Utility function for pack/unpack
        return if (bitDepth == 4) 0xf0 else if (bitDepth == 2) 0xc0 else 0x80 // bitDepth == 1
    }
}