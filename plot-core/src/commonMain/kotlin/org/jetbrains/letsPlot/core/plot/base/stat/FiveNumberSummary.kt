/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Ordering

/**
 * For a set of data, the minimum, first quartile, median, third quartile, and maximum.
 * Note: A boxplot is a visual display of the five-number summary.
 */
internal class FiveNumberSummary {

    val min: Double
    val max: Double
    val median: Double
    val firstQuartile: Double
    // 25 %
    val thirdQuartile: Double    // 75 %

    constructor(data: List<Double>) {
        val sorted = Ordering.natural<Double>().sortedCopy(data)
        min = AggregateFunctions.min(sorted)
        max = AggregateFunctions.max(sorted)
        median = AggregateFunctions.median(sorted)
        firstQuartile = AggregateFunctions.quantile(sorted, 0.25)
        thirdQuartile = AggregateFunctions.quantile(sorted, 0.75)
    }

    constructor(min: Double, max: Double, median: Double, firstQuartile: Double, thirdQuartile: Double) {
        this.min = min
        this.max = max
        this.median = median
        this.firstQuartile = firstQuartile
        this.thirdQuartile = thirdQuartile
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as FiveNumberSummary?
        return that!!.min.compareTo(min) == 0 &&
                that.max.compareTo(max) == 0 &&
                that.median.compareTo(median) == 0 &&
                that.firstQuartile.compareTo(firstQuartile) == 0 &&
                that.thirdQuartile.compareTo(thirdQuartile) == 0
    }

    override fun hashCode(): Int {
        return arrayOf(min, max, median, firstQuartile, thirdQuartile).hashCode()
    }
}
