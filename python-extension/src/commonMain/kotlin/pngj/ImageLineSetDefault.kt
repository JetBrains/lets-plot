/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj

/**
 * Default implementation of [IImageLineSet].
 * <P>
 * Supports all modes: single cursor, full rows, or partial. This should not be
 * used for
</P> */
internal abstract class ImageLineSetDefault<T : IImageLine>(
    imgInfo: ImageInfo, singleCursor: Boolean, nlinesx: Int, noffsetx: Int,
    stepx: Int
) : IImageLineSet<T> {
    protected val imgInfo: ImageInfo
    private val singleCursor: Boolean
    private var nlines = 0
    private var offset = 0
    private var step = 0
    private var imageLines: MutableList<T>? = null // null if single cursor
    private var imageLine: T? = null // null unless single cursor
    private var currentRow = -1 // only relevant (and not much) for cursor

    init {
        this.imgInfo = imgInfo
        this.singleCursor = singleCursor
        if (singleCursor) {
            nlines = 1 // we store only one line, no matter how many will be read
            offset = 0
            step = 1 // don't matter
        } else {
            nlines = nlinesx // note that it can also be 1
            offset = noffsetx
            step = stepx // don't matter
        }
        createImageLines()
    }

    private fun createImageLines() {
        if (singleCursor) imageLine = createImageLine() else {
            imageLines = mutableListOf()
            for (i in 0 until nlines) imageLines!!.add(createImageLine())
        }
    }

    protected abstract fun createImageLine(): T

    /**
     * Retrieves the image line
     *
     *
     * Warning: the argument is the row number in the original image
     *
     *
     * If this is a cursor, no check is done, always the same row is returned
     */
    override fun getImageLine(n: Int): T {
        currentRow = n
        return if (singleCursor) imageLine!! else {
            val r = imageRowToMatrixRowStrict(n)
            if (r < 0) throw PngjException("Invalid row number")
            imageLines!![r]
        }
    }

    /**
     * does not check for valid range
     */
    override fun getImageLineRawNum(n: Int): T {
        return if (singleCursor) imageLine!! else imageLines!![n]
    }

    /**
     * True if the set contains this image line
     *
     *
     * Warning: the argument is the row number in the original image
     *
     *
     * If this works as cursor, this returns true only if that is the number of
     * its "current" line
     */
    override fun hasImageLine(n: Int): Boolean {
        return if (singleCursor) currentRow == n else imageRowToMatrixRowStrict(n) >= 0
    }

    /**
     * How many lines does this object contain?
     */
    override fun size(): Int {
        return nlines
    }

    /**
     * Same as [.imageRowToMatrixRow], but returns negative if
     * invalid
     */
    private fun imageRowToMatrixRowStrict(imrow: Int): Int {
        @Suppress("NAME_SHADOWING")
        var imrow = imrow
        imrow -= offset
        val mrow = if (imrow >= 0 && (step == 1 || imrow % step == 0)) imrow / step else -1
        return if (mrow < nlines) mrow else -1
    }

    /**
     * Converts from matrix row number (0 : nRows-1) to image row number
     *
     * @param mrow
     * Matrix row number
     * @return Image row number. Returns trash if mrow is invalid
     */
    fun matrixRowToImageRow(mrow: Int): Int {
        return mrow * step + offset
    }

    /**
     * Converts from real image row to this object row number.
     *
     *
     * Warning: this always returns a valid matrix row (clamping on 0 : nrows-1,
     * and rounding down)
     *
     *
     * Eg: rowOffset=4,rowStep=2 imageRowToMatrixRow(17) returns 6 ,
     * imageRowToMatrixRow(1) returns 0
     */
    fun imageRowToMatrixRow(imrow: Int): Int {
        val r = (imrow - offset) / step
        return if (r < 0) 0 else if (r < nlines) r else nlines - 1
    }

    companion object {
        /**
         * utility function, given a factory for one line, returns a factory for a
         * set
         */
        private fun <T : IImageLine> createImageLineSetFactoryFromImageLineFactory(
            ifactory: IImageLineFactory<T>
        ): IImageLineSetFactory<T> { // ugly method must have ugly name. don't let this intimidate you
            return object : IImageLineSetFactory<T> {
                override fun create(
                    imgInfo: ImageInfo?, singleCursor: Boolean, nlines: Int, noffset: Int,
                    step: Int
                ): IImageLineSet<T> {
                    return object : ImageLineSetDefault<T>(imgInfo!!, singleCursor, nlines, noffset, step) {
                        override fun createImageLine(): T {
                            return ifactory.createImageLine(imgInfo!!)
                        }
                    }
                }
            }
        }

        /** utility function, returns default factory for [ImageLineInt]  */
        val factoryInt: IImageLineSetFactory<ImageLineInt>
            get() = createImageLineSetFactoryFromImageLineFactory(ImageLineInt.factory)

        /** utility function, returns default factory for [ImageLineByte]  */
        val factoryByte: IImageLineSetFactory<ImageLineByte>
            get() = createImageLineSetFactoryFromImageLineFactory(ImageLineByte.factory)
    }
}