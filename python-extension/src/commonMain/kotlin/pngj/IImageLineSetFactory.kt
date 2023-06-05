/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj

/**
 * Factory of [IImageLineSet], used by [PngReader].
 *
 *
 *
 * @param <T>
 * Generic type of IImageLine
</T> */
internal interface IImageLineSetFactory<T : IImageLine> {
    /**
     * Creates a new [IImageLineSet]
     *
     * If singleCursor=true, the caller will read and write one row fully at a
     * time, in order (it'll never try to read out of order lines), so the
     * implementation can opt for allocate only one line.
     *
     * @param imgInfo
     * Image info
     * @param singleCursor
     * : will read/write one row at a time
     * @param nlines
     * : how many lines we plan to read
     * @param noffset
     * : how many lines we want to skip from the original image
     * (normally 0)
     * @param step
     * : row step (normally 1)
     */
    fun create(
        imgInfo: ImageInfo?,
        singleCursor: Boolean,
        nlines: Int,
        noffset: Int,
        step: Int
    ): IImageLineSet<T>
}