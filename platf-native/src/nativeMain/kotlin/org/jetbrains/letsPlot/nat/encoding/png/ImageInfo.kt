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

package org.jetbrains.letsPlot.nat.encoding.png

/**
 * Simple immutable wrapper for basic image info.
 *
 *
 * Some parameters are redundant, but the constructor receives an 'orthogonal'
 * subset.
 *
 *
 * ref: http://www.w3.org/TR/PNG/#11IHDR
 */
class ImageInfo constructor(
    /**
     * Cols= Image width, in pixels.
     */
    val cols: Int,
    /**
     * Rows= Image height, in pixels
     */
    val rows: Int, bitdepth: Int,
    /**
     * Flag: true if has alpha channel (RGBA/GA)
     */
    val alpha: Boolean,
    /**
     * Flag: true if is grayscale (G/GA)
     */
    val greyscale: Boolean = false,
    /**
     * Flag: true if image is indexed, i.e., it has a palette
     */
    val indexed: Boolean = false
) {
    /**
     * Bits per sample (per channel) in the buffer (1-2-4-8-16). This is 8-16
     * for RGB/ARGB images, 1-2-4-8 for grayscale. For indexed images, number of
     * bits per palette index (1-2-4-8)
     */
    val bitDepth: Int

    /**
     * Number of channels, as used internally: 3 for RGB, 4 for RGBA, 2 for GA
     * (gray with alpha), 1 for grayscale or indexed.
     */
    val channels: Int

    /**
     * Flag: true if image internally uses less than one byte per sample (bit
     * depth 1-2-4)
     */
    val packed: Boolean

    /**
     * Bits used for each pixel in the buffer: channel * bitDepth
     */
    val bitspPixel: Int

    /**
     * rounded up value: this is only used internally for filter
     */
    val bytesPixel: Int

    /**
     * ceil(bitspp*cols/8) - does not include filter
     */
    val bytesPerRow: Int

    /**
     * Equals cols * channels
     */
    val samplesPerRow: Int

    /**
     * Amount of "packed samples" : when several samples are stored in a single
     * byte (bitdepth 1,2 4) they are counted as one "packed sample". This is
     * less that samplesPerRow only when bitdepth is 1-2-4 (flag packed = true)
     *
     *
     * This equals the number of elements in the scanline array if working with
     * packedMode=true
     *
     *
     * For internal use, client code should rarely access this.
     */
    val samplesPerRowPacked: Int
    var totalPixels: Long = -1 // lazy getter
        get() {
            if (field < 0) field = cols * rows.toLong()
            return field
        }
        private set

    /**
     * Total uncompressed bytes in IDAT, including filter byte. This is not
     * valid for interlaced.
     */
    var totalRawBytes: Long = -1 // lazy getter
        get() {
            if (field < 0) field = (bytesPerRow + 1) * rows.toLong()
            return field
        }
        private set
    /**
     * Full constructor
     *
     * @param cols
     * Width in pixels
     * @param rows
     * Height in pixels
     * @param bitdepth
     * Bits per sample, in the buffer : 8-16 for RGB true color and
     * greyscale
     * @param alpha
     * Flag: has an alpha channel (RGBA or GA)
     * @param greyscale
     * Flag: is gray scale (any bitdepth, with or without alpha)
     * @param indexed
     * Flag: has palette
     */
    /**
     * Short constructor: assumes truecolor (RGB/RGBA)
     */
    init {
        if (greyscale && indexed) throw PngjException("palette and greyscale are mutually exclusive")
        channels = if (greyscale || indexed) (if (alpha) 2 else 1) else if (alpha) 4 else 3
        // http://www.w3.org/TR/PNG/#11IHDR
        bitDepth = bitdepth
        packed = bitdepth < 8
        bitspPixel = channels * bitDepth
        bytesPixel = (bitspPixel + 7) / 8
        bytesPerRow = (bitspPixel * cols + 7) / 8
        samplesPerRow = channels * cols
        samplesPerRowPacked = if (packed) bytesPerRow else samplesPerRow
        when (bitDepth) {
            1, 2, 4 -> if (!(indexed || greyscale)) throw PngjException("only indexed or grayscale can have bitdepth=$bitDepth")
            8 -> {}
            16 -> if (indexed) throw PngjException("indexed can't have bitdepth=$bitDepth")
            else -> throw PngjException("invalid bitdepth=$bitDepth")
        }
        if (cols < 1 || cols > MAX_COLS_ROW) throw PngjException("invalid cols=$cols ???")
        if (rows < 1 || rows > MAX_COLS_ROW) throw PngjException("invalid rows=$rows ???")
        if (samplesPerRow < 1) throw PngjException("invalid image parameters (overflow?)")
    }

    override fun toString(): String {
        return ("ImageInfo [cols=" + cols + ", rows=" + rows + ", bitDepth=" + bitDepth + ", channels=" + channels
                + ", alpha=" + alpha + ", greyscale=" + greyscale + ", indexed=" + indexed + "]")
    }

    @Suppress("unused")
    fun toStringDetail(): String {
        return ("ImageInfo [cols=" + cols + ", rows=" + rows + ", bitDepth=" + bitDepth + ", channels=" + channels
                + ", bitspPixel=" + bitspPixel + ", bytesPixel=" + bytesPixel + ", bytesPerRow=" + bytesPerRow
                + ", samplesPerRow=" + samplesPerRow + ", samplesPerRowP=" + samplesPerRowPacked + ", alpha=" + alpha
                + ", greyscale=" + greyscale + ", indexed=" + indexed + ", packed=" + packed + "]")
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (alpha) 1231 else 1237
        result = prime * result + bitDepth
        result = prime * result + cols
        result = prime * result + if (greyscale) 1231 else 1237
        result = prime * result + if (indexed) 1231 else 1237
        result = prime * result + rows
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as ImageInfo
        if (alpha != other.alpha) return false
        if (bitDepth != other.bitDepth) return false
        if (cols != other.cols) return false
        if (greyscale != other.greyscale) return false
        if (indexed != other.indexed) return false
        return rows == other.rows
    }

    companion object {
        /**
         * Absolute allowed maximum value for rows and cols (2^24 ~16 million).
         * (bytesPerRow must fit in a 32bit integer, though total amount of pixels
         * not necessarily).
         */
        const val MAX_COLS_ROW = 16777216
    }
}