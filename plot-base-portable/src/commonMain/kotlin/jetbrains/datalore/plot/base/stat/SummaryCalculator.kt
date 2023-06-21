/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.gcommon.collect.Ordering
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class SummaryCalculator(values: List<Double>) {
    private val sortedValues: List<Double> = Ordering.natural<Double>().sortedCopy(values)

    val nan = Double.NaN
    val count by lazy { sortedValues.size.toDouble() }
    val sum by lazy { sortedValues.sum() }
    val mean by lazy {
        if (sortedValues.isEmpty()) {
            Double.NaN
        } else if (sortedValues.size == 1) {
            sortedValues.first()
        } else {
            sum / sortedValues.size
        }
    }
    val median by lazy { quantile(0.5) }
    val min by lazy {
        if (sortedValues.isEmpty()) {
            Double.NaN
        } else {
            sortedValues.first()
        }
    }
    val max by lazy {
        if (sortedValues.isEmpty()) {
            Double.NaN
        } else {
            sortedValues.last()
        }
    }
    val q1 by lazy { quantile(0.25) }
    val q3 by lazy { quantile(0.75) }

    fun quantile(p: Double): Double {
        if (sortedValues.isEmpty()) {
            return Double.NaN
        }
        if (sortedValues.size == 1) {
            return sortedValues.first()
        }
        val place = p * (sortedValues.size - 1)
        val i = round(place)
        return if (place == i) {
            sortedValues[place.toInt()]
        } else {
            (sortedValues[ceil(place).toInt()] + sortedValues[floor(place).toInt()]) / 2.0
        }
    }
}