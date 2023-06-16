/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj

/**
 * Lightweight wrapper for an image scanline, used for read and write.
 *
 *
 * This object can be (usually it is) reused while iterating over the image
 * lines.
 *
 *
 * See `scanline` field, to understand the format.
 *
 * Format: byte (one bytes per sample) (for 16bpp the extra byte is placed in an
 * extra array)
 */
class ImageLineByte(imgInfo: ImageInfo, sci: ByteArray?) : IImageLine, IImageLineArray {
    val imgInfo: ImageInfo

    /**
     * One byte per sample. This can be used also for 16bpp images, but in this
     * case this loses the less significant 8-bits ; see also getScanlineByte2
     * and getElem.
     */
    val scanline: ByteArray

    /**
     * only for 16bpp (less significant byte)
     *
     * @return null for less than 16bpp
     */
    val scanline2 // only used for 16 bpp (less significant byte) Normally you'd prefer
            : ByteArray?

    // ImageLineInt in this case
    override lateinit var filterType // informational ; only filled by the reader. not significant for
            : FilterType

    // interlaced
    override val size // = imgInfo.samplePerRowPacked, if packed:imgInfo.samplePerRow elswhere
            : Int

    constructor(imgInfo: ImageInfo) : this(imgInfo, null)

    init {
        this.imgInfo = imgInfo
        filterType = FilterType.FILTER_UNKNOWN
        size = imgInfo.samplesPerRow
        scanline = if (sci != null && sci.size >= size) sci else ByteArray(size)
        scanline2 = if (imgInfo.bitDepth == 16) ByteArray(size) else null
    }

    @Suppress("unused")
    val filterUsed: FilterType
        get() = filterType

    /**
     * Basic info
     */
    override fun toString(): String {
        return " cols=" + imgInfo.cols + " bpc=" + imgInfo.bitDepth + " size=" + scanline.size
    }

    override fun readFromPngRaw(raw: ByteArray, len: Int, offset: Int, step: Int) {
        filterType = FilterType.getByVal(raw[0].toInt()) // only for non interlaced line the filter is significative
        val len1 = len - 1
        val step1: Int = (step - 1) * imgInfo.channels
        if (imgInfo.bitDepth == 8) {
            if (step == 1) { // 8bispp non-interlaced: most important case, should be optimized
                arraycopy(raw, 1, scanline, 0, len1)
            } else { // 8bispp interlaced
                var s = 1
                var c = 0
                var i: Int = offset * imgInfo.channels
                while (s <= len1) {
                    scanline[i] = raw[s]
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
                while (i < imgInfo.samplesPerRow) {
                    scanline[i] = raw[s++] // get the first byte
                    scanline2!![i] = raw[s++] // get the first byte
                    i++
                }
            } else {
                var s = 1
                var c = 0
                var i = if (offset != 0) offset * imgInfo.channels else 0
                while (s <= len1) {
                    scanline[i] = raw[s++]
                    scanline2!![i] = raw[s++]
                    c++
                    if (c == imgInfo.channels) {
                        c = 0
                        i += step1
                    }
                    i++
                }
            }
        } else { // packed formats
            val mask0: Int
            var mask: Int
            var shi: Int
            val bd: Int
            bd = imgInfo.bitDepth
            mask0 = ImageLineHelper.getMaskForPackedFormats(bd)
            var i: Int = offset * imgInfo.channels
            var r = 1
            var c = 0
            while (r < len) {
                mask = mask0
                shi = 8 - bd
                do {
                    scanline[i] = (raw[r].toInt() and mask shr shi).toByte()
                    mask = mask shr bd
                    shi -= bd
                    i++
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
        if (imgInfo.bitDepth == 8) {
            arraycopy(scanline, 0, raw, 1, size)
        } else if (imgInfo.bitDepth == 16) {
            var i = 0
            var s = 1
            while (i < size) {
                raw[s++] = scanline[i]
                raw[s++] = scanline2!![i]
                i++
            }
        } else { // packed formats
            var shi: Int
            val bd: Int
            var v: Int
            bd = imgInfo.bitDepth
            shi = 8 - bd
            v = 0
            var i = 0
            var r = 1
            while (i < size) {
                v = v or (scanline[i].toInt() shl shi)
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

    override fun endReadFromPngRaw() {}
    override fun getElem(i: Int): Int {
        return if (scanline2 == null) scanline[i].toInt() and 0xFF else scanline[i].toInt() and 0xFF shl 8 or (scanline2[i].toInt() and 0xFF)
    }

    override val imageInfo: ImageInfo
        get() = imgInfo

    companion object {
        /**
         * Returns a factory for this object
         */
        val factory: IImageLineFactory<ImageLineByte>
            get() = object : IImageLineFactory<ImageLineByte> {
                override fun createImageLine(iminfo: ImageInfo): ImageLineByte {
                    return ImageLineByte(iminfo)
                }
            }
    }
}