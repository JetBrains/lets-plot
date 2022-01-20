/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.gcommon.collect.ClosedRange

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
    fun range(aes: Aes<Double>): ClosedRange<Double>?

    fun resolution(aes: Aes<Double>, naValue: Double): Double

    fun numericValues(aes: Aes<Double>): Iterable<Double?>

    fun groups(): Iterable<Int>
}
