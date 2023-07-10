/*
 * Copyright (c) 2023. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")
package jetbrains.datalore.plot.pythonExtension.pngj

internal class Deinterlacer(
    private val imi: ImageInfo
) {
    // 1-7
    var pass: Int = 0; private set

    /**
     * How many rows has the current pass?
     */
    var rows = 0; private set

    /**
     * How many columns (pixels) are there in the current row
     */
    var cols = 0; private set
    var dY = 0
    var dX = 0
    var oY = 0
    // current step and offset (in pixels)
    var oX = 0
    private var oXsamples = 0
    // step in samples
    private var dXsamples = 0

    /**
     * current row number inside the "sub image"
     */
    // current row in the virtual subsampled image; this increments (by 1) from 0 to rows/dy 7 times
    var currRowSubimg: Int = -1; private set

    /**
     * current row number inside the "real image"
     */
    // in the real image, this will cycle from 0 to im.rows in different steps, 7 times
    var currRowReal: Int = -1; private set
    // not counting empty rows
    private var currRowSeq: Int = 0

    private var totalRows: Int = 0

    private var isEnded = false

    init {
        setPass(1)
        setRow(0)
    }

    /** this refers to the row currRowSubimg  */
    private fun setRow(n: Int) { // This should be called only intercally, in sequential order
        currRowSubimg = n
        currRowReal = n * dY + oY
        if (currRowReal < 0 || currRowReal >= imi.rows) throw PngjExceptionInternal("bad row - this should not happen")
    }

    /** Skips passes with no rows. Return false is no more rows  */
    fun nextRow(): Boolean {
        currRowSeq++
        if (rows == 0 || currRowSubimg >= rows - 1) { // next pass
            if (pass == 7) {
                isEnded = true
                return false
            }
            setPass(pass + 1)
            if (rows == 0) {
                currRowSeq--
                return nextRow()
            }
            setRow(0)
        } else {
            setRow(currRowSubimg + 1)
        }
        return true
    }

    private fun setPass(p: Int) {
        if (pass == p) return
        pass = p
        val pp = paramsForPass(p) // dx,dy,ox,oy
        dX = pp[0].toInt()
        dY = pp[1].toInt()
        oX = pp[2].toInt()
        oY = pp[3].toInt()
        rows = if (imi.rows > oY) (imi.rows + dY - 1 - oY) / dY else 0
        cols = if (imi.cols > oX) (imi.cols + dX - 1 - oX) / dX else 0
        if (cols == 0) rows = 0 // well, really...
        dXsamples = dX * imi.channels
        oXsamples = oX * imi.channels
    }

    // not including filter byte
    val bytesToRead: Int; get() = (imi.bitspPixel * cols + 7) / 8

    fun getTotalRows(): Int {
        if (totalRows == 0) { // lazy compute
            for (p in 1..7) {
                val pp = paramsForPass(p) // dx dy ox oy
                val rows = if (imi.rows > pp[3]) (imi.rows + pp[1] - 1 - pp[3]) / pp[1] else 0
                val cols = if (imi.cols > pp[2]) (imi.cols + pp[0] - 1 - pp[2]) / pp[0] else 0
                if (rows > 0 && cols > 0) totalRows += rows
            }
        }
        return totalRows
    }// without filter byte// dx dy ox oy// including the filter byte

    /**
     * total unfiltered bytes in the image, including the filter byte
     */
    val totalRawBytes: Long
        get() { // including the filter byte
            var bytes: Long = 0
            for (p in 1..7) {
                val pp = paramsForPass(p) // dx dy ox oy
                val rows = if (imi.rows > pp[3]) (imi.rows + pp[1] - 1 - pp[3]) / pp[1] else 0
                val cols = if (imi.cols > pp[2]) (imi.cols + pp[0] - 1 - pp[2]) / pp[0] else 0
                val bytesr: Int = (imi.bitspPixel * cols + 7) / 8 // without filter byte
                if (rows > 0 && cols > 0) bytes += rows * (1 + bytesr.toLong())
            }
            return bytes
        }

    companion object {
        fun paramsForPass(p: Int): ByteArray { // dx,dy,ox,oy
            return when (p) {
                1 -> byteArrayOf(8, 8, 0, 0)
                2 -> byteArrayOf(8, 8, 4, 0)
                3 -> byteArrayOf(4, 8, 0, 4)
                4 -> byteArrayOf(4, 4, 2, 0)
                5 -> byteArrayOf(2, 4, 0, 2)
                6 -> byteArrayOf(2, 2, 1, 0)
                7 -> byteArrayOf(1, 2, 0, 1)
                else -> throw PngjExceptionInternal("bad interlace pass$p")
            }
        }
    }
}