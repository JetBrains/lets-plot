/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

@file:Suppress("KDocUnresolvedReference")

package org.jetbrains.letsPlot.util.pngj

import org.jetbrains.letsPlot.util.pngj.ImageLineHelper.getMaskForPackedFormats

/**
 * Represents an image line, integer format (one integer by sample). See
 * [.scanline] to understand the format.
 */
internal class ImageLineInt(imgInfo: ImageInfo, sci: IntArray?) : IImageLine, IImageLineArray {
    val imgInfo: ImageInfo
    /**
     * @return see [.scanline]
     */
    /**
     * The 'scanline' is an array of integers, corresponds to an image line
     * (row).
     *
     *
     * Each `int` is a "sample" (one for channel), (0-255 or 0-65535)
     * in the corresponding PNG sequence: `R G B R G B...` or
     * `R G B A R G B A... or `g g g ...` or
     * `i i i` (palette index)
    ` *
     *
     * For bitdepth=1/2/4 the value is not scaled (hence, eg, if bitdepth=2 the
     * range will be 0-4)
     *
     *
     * To convert a indexed line to RGB values, see
     * [ImageLineHelper.palette2rgb]
     * (you can't do the reverse)
     */
    val scanline: IntArray
    /**
     * @see .size
     */
    /**
     * number of elements in the scanline
     */
    override val size: Int

    /**
     * informational ; only filled by the reader. not meaningful for interlaced
     */
    override var filterType: FilterType = FilterType.FILTER_UNKNOWN

    /**
     * @param imgInfo
     * Inmutable ImageInfo, basic parameters of the image we are
     * reading or writing
     */
    constructor(imgInfo: ImageInfo) : this(imgInfo, null)

    /**
     * @param imgInfo
     * Inmutable ImageInfo, basic parameters of the image we are
     * reading or writing
     * @param sci
     * prealocated buffer (can be null)
     */
    init {
        this.imgInfo = imgInfo
        filterType = FilterType.FILTER_UNKNOWN
        size = imgInfo.samplesPerRow
        scanline = if (sci != null && sci.size >= size) sci else IntArray(size)
    }

    /**
     * Basic info
     */
    override fun toString(): String {
        return " cols=" + imgInfo.cols + " bpc=" + imgInfo.bitDepth + " size=" + scanline.size
    }

    override fun readFromPngRaw(raw: ByteArray, len: Int, offset: Int, step: Int) {
        filterType = FilterType.getByVal(raw[0].toInt())
        val len1 = len - 1
        val step1: Int = (step - 1) * imgInfo.channels
        if (imgInfo.bitDepth == 8) {
            if (step == 1) { // 8bispp non-interlaced: most important case, should be optimized
                for (i in 0 until size) {
                    scanline[i] = raw[i + 1].toInt() and 0xff
                }
            } else { // 8bispp interlaced
                var s = 1
                var c = 0
                var i: Int = offset * imgInfo.channels
                while (s <= len1) {
                    scanline[i] = raw[s].toInt() and 0xff
                    c++
                    if (c == imgInfo.channels) {
                        c = 0
                        i += step1
                    }
                    s++
                    i++
                }
            }
        } else if (imgInfo.bitDepth == 16) {
            if (step == 1) { // 16bispp non-interlaced
                var i = 0
                var s = 1
                while (i < size) {
                    scanline[i] = raw[s++].toInt() and 0xFF shl 8 or (raw[s++].toInt() and 0xFF) // 16 bitspc
                    i++
                }
            } else {
                var s = 1
                var c = 0
                var i = if (offset != 0) offset * imgInfo.channels else 0
                while (s <= len1) {
                    scanline[i] = raw[s++].toInt() and 0xFF shl 8 or (raw[s].toInt() and 0xFF) // 16 bitspc
                    c++
                    if (c == imgInfo.channels) {
                        c = 0
                        i += step1
                    }
                    s++
                    i++
                }
            }
        } else { // packed formats
            val mask0: Int
            var mask: Int
            var shi: Int
            val bd: Int = imgInfo.bitDepth
            mask0 = getMaskForPackedFormats(bd)
            var i: Int = offset * imgInfo.channels
            var r = 1
            var c = 0
            while (r < len) {
                mask = mask0
                shi = 8 - bd
                do {
                    scanline[i++] = raw[r].toInt() and mask shr shi
                    mask = mask shr bd
                    shi -= bd
                    c++
                    if (c == imgInfo.channels) {
                        c = 0
                        i += step1
                    }
                } while (mask != 0 && i < size)
                r++
            }
        }
    }

    override fun writeToPngRaw(raw: ByteArray) {
        raw[0] = filterType.value.toByte()
        when (imgInfo.bitDepth) {
            8 -> {
                for (i in 0 until size) {
                    raw[i + 1] = scanline[i].toByte()
                }
            }
            16 -> {
                var i = 0
                var s = 1
                while (i < size) {
                    raw[s++] = (scanline[i] shr 8).toByte()
                    raw[s++] = (scanline[i] and 0xff).toByte()
                    i++
                }
            }
            else -> { // packed formats
                var shi: Int
                var v: Int
                val bd: Int = imgInfo.bitDepth
                shi = 8 - bd
                v = 0
                var i = 0
                var r = 1
                while (i < size) {
                    v = v or (scanline[i] shl shi)
                    shi -= bd
                    if (shi < 0 || i == size - 1) {
                        raw[r++] = v.toByte()
                        shi = 8 - bd
                        v = 0
                    }
                    i++
                }
            }
        }
    }

    /**
     * Does nothing in this implementation
     */
    override fun endReadFromPngRaw() {}
    override fun getElem(i: Int): Int {
        return scanline[i]
    }

    override val imageInfo: ImageInfo
        get() = imgInfo

    companion object {
        /**
         * Helper method, returns a default factory for this object
         *
         */
        val factory: IImageLineFactory<ImageLineInt>
            get() = object : IImageLineFactory<ImageLineInt> {
                override fun createImageLine(iminfo: ImageInfo): ImageLineInt {
                    return ImageLineInt(iminfo)
                }
            }
    }
}