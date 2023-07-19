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
package org.jetbrains.letsPlot.nat.encoding.png

/**
 * Internal PNG predictor filter type
 *
 * Negative values are pseudo types, actually global strategies for writing,
 * that (can) result on different real filters for different rows
 */
enum class FilterType(val value: Int) {
    /**
     * No filter.
     */
    FILTER_NONE(0),

    /**
     * SUB filter (uses same row)
     */
    FILTER_SUB(1),

    /**
     * UP filter (uses previous row)
     */
    FILTER_UP(2),

    /**
     * AVERAGE filter
     */
    FILTER_AVERAGE(3),

    /**
     * PAETH predictor
     */
    FILTER_PAETH(4),

    /**
     * Default strategy: select one of the standard filters depending on global
     * image parameters
     */
    FILTER_DEFAULT(-1),
    @Deprecated("use #FILTER_ADAPTIVE_FAST")
    FILTER_AGGRESSIVE(-2),
    @Deprecated("use #FILTER_ADAPTIVE_MEDIUM or #FILTER_ADAPTIVE_FULL")
    FILTER_VERYAGGRESSIVE(-4),

    /**
     * Adaptative strategy, sampling each row, or almost
     */
    FILTER_ADAPTIVE_FULL(-4),

    /**
     * Adaptive strategy, skippping some rows
     */
    FILTER_ADAPTIVE_MEDIUM(-3),  // samples about 1/4 row

    /**
     * Adaptative strategy, skipping many rows - more speed
     */
    FILTER_ADAPTIVE_FAST(-2),  // samples each 8 or 16 rows

    /**
     * Experimental
     */
    FILTER_SUPER_ADAPTIVE(-10),  //

    /**
     * Preserves the filter passed in original row.
     */
    FILTER_PRESERVE(-40),

    /**
     * Uses all fiters, one for lines, cyciclally. Only for tests.
     */
    FILTER_CYCLIC(-50),

    /**
     * Not specified, placeholder for unknown or NA filters.
     */
    FILTER_UNKNOWN(-100);

    companion object {
        private val byVal: MutableMap<Int, FilterType> = mutableMapOf()

        init {
            for (ft in values()) {
                byVal[ft.value] = ft
            }
        }

        fun getByVal(i: Int): FilterType {
            return byVal[i] ?: error("Invalid index: $i")
        }

        /** only considers standard  */
        fun isValidStandard(i: Int): Boolean {
            return i in 0..4
        }

        fun isValidStandard(fy: FilterType?): Boolean {
            return fy != null && isValidStandard(fy.value)
        }

        fun isAdaptive(fy: FilterType): Boolean {
            return fy.value <= -2 && fy.value >= -4
        }

        /**
         * Returns all "standard" filters
         */
        val allStandard: Array<FilterType>
            get() = arrayOf(FILTER_NONE, FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH)
        val allStandardNoneLast: Array<FilterType>
            get() = arrayOf(FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH, FILTER_NONE)
        val allStandardExceptNone: Array<FilterType>
            get() = arrayOf(FILTER_SUB, FILTER_UP, FILTER_AVERAGE, FILTER_PAETH)
        val allStandardForFirstRow: Array<FilterType>
            get() = arrayOf(FILTER_SUB, FILTER_NONE)
    }
}