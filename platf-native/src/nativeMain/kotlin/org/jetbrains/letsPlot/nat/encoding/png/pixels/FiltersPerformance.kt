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

@file:Suppress("unused")
package org.jetbrains.letsPlot.nat.encoding.png.pixels

import org.jetbrains.letsPlot.nat.encoding.png.arraycopy
import org.jetbrains.letsPlot.nat.encoding.png.fill
import org.jetbrains.letsPlot.nat.encoding.png.*
import kotlin.math.ln
import kotlin.math.pow

/** for use in adaptative strategy  */
internal class FiltersPerformance(imgInfo: ImageInfo) {
    private val iminfo: ImageInfo
    private var memoryA = 0.7 // empirical (not very critical: 0.72)
    private var lastrow = -1
    private val absum = DoubleArray(5) // depending on the strategy not all values might be computed for all
    private val entropy = DoubleArray(5)
    private val cost = DoubleArray(5)
    private val histog = IntArray(256) // temporary, not normalized
    private var lastprefered = -1
    private var initdone = false
    private var preferenceForNone = 1.0 // higher gives more preference to NONE
    private val filterWeights = doubleArrayOf(-1.0, -1.0, -1.0, -1.0, -1.0)

    init {
        iminfo = imgInfo
    }

    private fun init() {
        if (filterWeights[0] < 0) { // has not been set from outside
            org.jetbrains.letsPlot.nat.encoding.png.arraycopy(FILTER_WEIGHTS_DEFAULT, 0, filterWeights, 0, 5)
            var wNone = filterWeights[0]
            if (iminfo.bitDepth == 16) wNone = 1.2 else if (iminfo.alpha) wNone =
                0.8 else if (iminfo.indexed || iminfo.bitDepth < 8) wNone = 0.4 // we prefer NONE strongly
            wNone /= preferenceForNone
            filterWeights[0] = wNone
        }
        org.jetbrains.letsPlot.nat.encoding.png.fill(cost, 1.0)
        initdone = true
    }

    fun updateFromFiltered(ftype: FilterType, rowff: ByteArray?, rown: Int) {
        updateFromRawOrFiltered(ftype, rowff, null, null, rown)
    }

    /** alternative: computes statistic without filtering  */
    fun updateFromRaw(ftype: FilterType, rowb: ByteArray?, rowbprev: ByteArray?, rown: Int) {
        updateFromRawOrFiltered(ftype, null, rowb, rowbprev, rown)
    }

    private fun updateFromRawOrFiltered(
        ftype: FilterType,
        rowff: ByteArray?,
        rowb: ByteArray?,
        rowbprev: ByteArray?,
        rown: Int
    ) {
        if (!initdone) init()
        if (rown != lastrow) {
            org.jetbrains.letsPlot.nat.encoding.png.fill(absum, Double.NaN)
            org.jetbrains.letsPlot.nat.encoding.png.fill(entropy, Double.NaN)
        }
        lastrow = rown
        if (rowff != null) computeHistogram(rowff) else computeHistogramForFilter(ftype, rowb, rowbprev)
        if (ftype === FilterType.FILTER_NONE) entropy[ftype.value] =
            computeEntropyFromHistogram() else absum[ftype.value] = computeAbsFromHistogram()
    }// lower wins

    /* WARNING: this is not idempotent, call it just once per cycle (sigh) */
    val preferred: FilterType
        get() {
            var fi = 0
            var vali = Double.MAX_VALUE
            var `val`: Double  // lower wins
            for (i in 0..4) {
                `val` = if (!absum[i].isNaN()) {
                    absum[i]
                } else if (!entropy[i].isNaN()) {
                    (2.0.pow(entropy[i]) - 1.0) * 0.5
                } else continue
                `val` *= filterWeights[i]
                `val` = cost[i] * memoryA + (1 - memoryA) * `val`
                cost[i] = `val`
                if (`val` < vali) {
                    vali = `val`
                    fi = i
                }
            }
            lastprefered = fi
            return FilterType.getByVal(lastprefered)
        }

    fun computeHistogramForFilter(filterType: FilterType, rowb: ByteArray?, rowbprev: ByteArray?) {
        org.jetbrains.letsPlot.nat.encoding.png.fill(histog, 0)
        var i: Int
        var j: Int
        val imax: Int = iminfo.bytesPerRow
        when (filterType) {
            FilterType.FILTER_NONE -> {
                i = 1
                while (i <= imax) {
                    histog[rowb!![i].toInt() and 0xFF]++
                    i++
                }
            }

            FilterType.FILTER_PAETH -> {
                i = 1
                while (i <= imax) {
                    histog[PngHelperInternal.filterRowPaeth(rowb!![i].toInt(), 0, rowbprev!![i].toInt() and 0xFF, 0)]++
                    i++
                }
                j = 1
                i = iminfo.bytesPixel + 1
                while (i <= imax) {
                    histog[PngHelperInternal.filterRowPaeth(
                        rowb!![i].toInt(), rowb[j].toInt() and 0xFF, rowbprev!![i].toInt() and 0xFF,
                        rowbprev[j].toInt() and 0xFF
                    )]++
                    i++
                    j++
                }
            }

            FilterType.FILTER_SUB -> {
                i = 1
                while (i <= iminfo.bytesPixel) {
                    histog[rowb!![i].toInt() and 0xFF]++
                    i++
                }
                j = 1
                i = iminfo.bytesPixel + 1
                while (i <= imax) {
                    histog[rowb!![i] - rowb[j] and 0xFF]++
                    i++
                    j++
                }
            }

            FilterType.FILTER_UP -> {
                i = 1
                while (i <= iminfo.bytesPerRow) {
                    histog[rowb!![i] - rowbprev!![i] and 0xFF]++
                    i++
                }
            }

            FilterType.FILTER_AVERAGE -> {
                i = 1
                while (i <= iminfo.bytesPixel) {
                    histog[(rowb!![i].toInt() and 0xFF) - (rowbprev!![i].toInt() and 0xFF) / 2 and 0xFF]++
                    i++
                }
                j = 1
                i = iminfo.bytesPixel + 1
                while (i <= imax) {
                    histog[(rowb!![i].toInt() and 0xFF) - ((rowbprev!![i].toInt() and 0xFF) + (rowb[j].toInt() and 0xFF)) / 2 and 0xFF]++
                    i++
                    j++
                }
            }

            else -> throw PngjExceptionInternal("Bad filter:$filterType")
        }
    }

    fun computeHistogram(rowff: ByteArray) {
        org.jetbrains.letsPlot.nat.encoding.png.fill(histog, 0)
        for (i in 1 until iminfo.bytesPerRow) histog[rowff[i].toInt() and 0xFF]++
    }

    fun computeAbsFromHistogram(): Double {
        var s = 0
        for (i in 1..127) s += histog[i] * i
        var i = 128
        var j = 128
        while (j > 0) {
            s += histog[i] * j
            i++
            j--
        }
        return s / iminfo.bytesPerRow.toDouble()
    }

    fun computeEntropyFromHistogram(): Double {
        val s: Double = 1.0 / iminfo.bytesPerRow
        val ls: Double = ln(s)
        var h = 0.0
        for (x in histog) {
            if (x > 0) h += (ln(x.toDouble()) + ls) * x
        }
        h *= s * LOG2NI
        if (h < 0.0) h = 0.0
        return h
    }

    /**
     * If larger than 1.0, NONE will be more prefered. This must be called
     * before init
     *
     * @param preferenceForNone
     * around 1.0 (default: 1.0)
     */
    fun setPreferenceForNone(preferenceForNone: Double) {
        this.preferenceForNone = preferenceForNone
    }

    /**
     * Values greater than 1.0 (towards infinite) increase the memory towards 1.
     * Values smaller than 1.0 (towards zero) decreases the memory .
     *
     */
    fun tuneMemory(m: Double) {
        memoryA = if (m == 0.0) 0.0 else memoryA.pow(1.0 / m)
    }

    /**
     * To set manually the filter weights. This is not recommended, unless you
     * know what you are doing. Setting this ignores preferenceForNone and omits
     * some heuristics
     *
     * @param weights
     * Five doubles around 1.0, one for each filter type. Lower is
     * preferered
     */
    fun setFilterWeights(weights: DoubleArray) {
        org.jetbrains.letsPlot.nat.encoding.png.arraycopy(weights, 0, filterWeights, 0, 5)
    }

    companion object {
        // this values are empirical (montecarlo), for RGB8 images with entropy estimator for NONE and memory=0.7
        //DONT MODIFY THIS
        // lower is better!
        val FILTER_WEIGHTS_DEFAULT = doubleArrayOf(0.73, 1.03, 0.97, 1.11, 1.22)
        private val LOG2NI: Double = -1.0 / ln(2.0)
    }
}