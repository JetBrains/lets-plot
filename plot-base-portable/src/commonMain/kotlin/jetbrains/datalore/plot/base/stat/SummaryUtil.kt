/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

object SummaryUtil {
    fun nan(sortedValues: List<Double>): Double = Double.NaN

    fun count(sortedValues: List<Double>): Double = sortedValues.size.toDouble()

    fun sum(sortedValues: List<Double>): Double = sortedValues.sum()

    fun mean(sortedValues: List<Double>): Double {
        return when (sortedValues.size) {
            0 -> Double.NaN
            1 -> sortedValues.first()
            else -> sum(sortedValues) / sortedValues.size
        }
    }

    fun median(sortedValues: List<Double>): Double = quantile(sortedValues, 0.5)

    fun min(sortedValues: List<Double>): Double = sortedValues.firstOrNull() ?: Double.NaN

    fun max(sortedValues: List<Double>): Double = sortedValues.lastOrNull() ?: Double.NaN

    fun q1(sortedValues: List<Double>): Double = quantile(sortedValues, 0.25)

    fun q3(sortedValues: List<Double>): Double = quantile(sortedValues, 0.75)

    fun quantile(sortedValues: List<Double>, p: Double): Double {
        return when (sortedValues.size) {
            0 -> Double.NaN
            1 -> sortedValues.first()
            else -> {
                val place = p * (sortedValues.size - 1)
                val i = round(place)
                if (place == i) {
                    sortedValues[place.toInt()]
                } else {
                    (sortedValues[ceil(place).toInt()] + sortedValues[floor(place).toInt()]) / 2.0
                }
            }
        }
    }
}