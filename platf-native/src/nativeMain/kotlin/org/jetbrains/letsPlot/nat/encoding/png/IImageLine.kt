/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.nat.encoding.png

/**
 * General format-translated image line.
 *
 *
 * The methods from this interface provides translation from/to PNG raw
 * unfiltered pixel data, for each image line. This doesn't make any assumptions
 * of underlying storage.
 *
 *
 * The user of this library will not normally use this methods, but instead will
 * cast to a more concrete implementation, as [ImageLineInt] or
 * [ImageLineByte] with its methods for accessing the pixel values.
 */
interface IImageLine {
    /**
     * Extract pixels from a raw unlfiltered PNG row. Len is the total amount of
     * bytes in the array, including the first byte (filter type)
     *
     * Arguments offset and step (0 and 1 for non interlaced) are in PIXELS.
     * It's guaranteed that when step==1 then offset=0
     *
     * Notice that when step!=1 the data is partial, this method will be called
     * several times
     *
     * Warning: the data in array 'raw' starts at position 0 and has 'len'
     * consecutive bytes. 'offset' and 'step' refer to the pixels in destination
     */
    fun readFromPngRaw(raw: ByteArray, len: Int, offset: Int, step: Int)

    /**
     * This is called when the read for the line has been completed (eg for
     * interlaced). It's called exactly once for each line. This is provided in
     * case the class needs to to some postprocessing.
     */
    fun endReadFromPngRaw()

    /**
     * Writes the line to a PNG raw byte array, in the unfiltered PNG format
     * Notice that the first byte is the filter type, you should write it only
     * if you know it.
     *
     */
    fun writeToPngRaw(raw: ByteArray)
}