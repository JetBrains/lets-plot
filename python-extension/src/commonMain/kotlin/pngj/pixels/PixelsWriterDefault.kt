/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

@file:Suppress("unused")
package org.jetbrains.letsPlot.util.pngj.pixels

import org.jetbrains.letsPlot.util.pngj.FilterType
import org.jetbrains.letsPlot.util.pngj.ImageInfo
import org.jetbrains.letsPlot.util.pngj.PngjOutputException
import org.jetbrains.letsPlot.util.pngj.fill
import kotlin.math.round

/**
 * Default implementation of PixelsWriter, with fixed filters and also adaptive
 * strategies.
 */
internal class PixelsWriterDefault(imgInfo: ImageInfo) : PixelsWriter(imgInfo) {
    /** current raw row  */
    private var rowb: ByteArray = arrayOf<Byte>().toByteArray()

    override fun getCurentRowb(): ByteArray {
        if (!initdone) init()
        return rowb
    }

    /** previous raw row  */
    private var rowbprev: ByteArray? = null

    /** buffer for filtered row  */
    private var rowbfilter: ByteArray? = null

    /** evaluates different filters, for adaptive strategy  */
    private var filtersPerformance: FiltersPerformance

    /** currently concrete selected filter type  */
    private var curfilterType: FilterType? = null

    /** parameters for adaptive strategy  */
    // set in initParams, does not change
    private var adaptMaxSkip = 0
    // set in initParams, does not change
    private var adaptSkipIncreaseSinceRow = 0
    // set in initParams, does not change
    private var adaptSkipIncreaseFactor = 0.0
    private var adaptNextRow = 0

    init {
        filtersPerformance = FiltersPerformance(imgInfo)
    }

    override fun initParams() {
        super.initParams()
        if (rowb.size < buflen) rowb = ByteArray(buflen)
        if (rowbfilter == null || rowbfilter!!.size < buflen) rowbfilter = ByteArray(buflen)
        if (rowbprev == null || rowbprev!!.size < buflen) rowbprev = ByteArray(buflen) else fill(rowbprev!!, 0.toByte())

        // if adaptative but too few rows or columns, use default
        if (imgInfo.cols < 3 && !FilterType.isValidStandard(filterType)) filterType = FilterType.FILTER_DEFAULT
        if (imgInfo.rows < 3 && !FilterType.isValidStandard(filterType)) filterType = FilterType.FILTER_DEFAULT
        if (imgInfo.totalPixels <= 1024 && !FilterType.isValidStandard(filterType)) filterType = defaultFilter
        if (FilterType.isAdaptive(filterType)) {
            // adaptCurSkip = 0;
            adaptNextRow = 0
            if (filterType === FilterType.FILTER_ADAPTIVE_FAST) {
                adaptMaxSkip = 200
                adaptSkipIncreaseSinceRow = 3
                adaptSkipIncreaseFactor = 1 / 4.0 // skip ~ row/3
            } else if (filterType === FilterType.FILTER_ADAPTIVE_MEDIUM) {
                adaptMaxSkip = 8
                adaptSkipIncreaseSinceRow = 32
                adaptSkipIncreaseFactor = 1 / 80.0
            } else if (filterType === FilterType.FILTER_ADAPTIVE_FULL) {
                adaptMaxSkip = 0
                adaptSkipIncreaseSinceRow = 128
                adaptSkipIncreaseFactor = 1 / 120.0
            } else throw PngjOutputException("bad filter $filterType")
        }
    }

    override fun filterAndWrite(rowb: ByteArray) {
        require(rowb.contentEquals(this.rowb)) { "??" } // we rely on this

        decideCurFilterType()
        val filtered: ByteArray = filterRowWithFilterType(curfilterType!!, rowb, rowbprev!!, rowbfilter!!)
        sendToCompressedStream(filtered)
        // swap rowb <-> rowbprev
        val aux = this.rowb
        this.rowb = rowbprev!!
        rowbprev = aux
    }

    private fun decideCurFilterType() {
        // decide the real filter and store in curfilterType
        if (FilterType.isValidStandard(filterType)) {
            curfilterType = filterType
        } else if (filterType === FilterType.FILTER_PRESERVE) {
            curfilterType = FilterType.getByVal(rowb[0].toInt())
        } else if (filterType === FilterType.FILTER_CYCLIC) {
            curfilterType = FilterType.getByVal(currentRow % 5)
        } else if (filterType === FilterType.FILTER_DEFAULT) {
            filterType = defaultFilter
            curfilterType = filterType // this could be done once
        } else if (FilterType.isAdaptive(filterType)) { // adaptive
            if (currentRow == adaptNextRow) {
                for (ftype in FilterType.allStandard) filtersPerformance.updateFromRaw(
                    ftype,
                    rowb,
                    rowbprev,
                    currentRow
                )
                curfilterType = filtersPerformance.preferred
                var skip =
                    if (currentRow >= adaptSkipIncreaseSinceRow) round((currentRow - adaptSkipIncreaseSinceRow) * adaptSkipIncreaseFactor)
                        .toInt() else 0
                if (skip > adaptMaxSkip) skip = adaptMaxSkip
                if (currentRow == 0) skip = 0
                adaptNextRow = currentRow + 1 + skip
            }
        } else {
            throw PngjOutputException("not implemented filter: $filterType")
        }
        if (currentRow == 0 && curfilterType !== FilterType.FILTER_NONE && curfilterType !== FilterType.FILTER_SUB) curfilterType =
            FilterType.FILTER_SUB // first row should always be none or sub
    }

    /**
     * Only for adaptive strategies. See
     * [FiltersPerformance.setPreferenceForNone]
     */
    fun setPreferenceForNone(preferenceForNone: Double) {
        filtersPerformance.setPreferenceForNone(preferenceForNone)
    }

    /**
     * Only for adaptive strategies. See
     * [FiltersPerformance.tuneMemory]
     */
    fun tuneMemory(m: Double) {
        filtersPerformance.tuneMemory(m)
    }

    /**
     * Only for adaptive strategies. See
     * [FiltersPerformance.setFilterWeights]
     */
    fun setFilterWeights(weights: DoubleArray) {
        filtersPerformance.setFilterWeights(weights)
    }
}