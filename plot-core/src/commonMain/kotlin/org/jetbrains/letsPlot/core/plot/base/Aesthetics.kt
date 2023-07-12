/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.interval.DoubleSpan

interface Aesthetics {
    val isEmpty: Boolean

    fun dataPointAt(index: Int): DataPointAesthetics

    fun dataPointCount(): Int

    fun dataPoints(): Iterable<DataPointAesthetics>

    /**
     * Numeric aes only (x,y)
     *
     * @return The range of mapped data
     */
    fun range(aes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>): DoubleSpan?

    fun resolution(aes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>, naValue: Double): Double

    fun numericValues(aes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>): Iterable<Double?>

    fun groups(): Iterable<Int>
}
