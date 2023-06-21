/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

object SummaryStatUtil {
    val nan: (List<Double>) -> Double = { Double.NaN }

    fun count(sortedValues: List<Double>): Double {
        return sortedValues.size.toDouble()
    }

    fun sum(sortedValues: List<Double>): Double {
        return sortedValues.sum()
    }

    fun mean(sortedValues: List<Double>): Double {
        return if (sortedValues.isEmpty()) {
            Double.NaN
        } else if (sortedValues.size == 1) {
            sortedValues.first()
        } else {
            sum(sortedValues) / sortedValues.size
        }
    }

    fun median(sortedValues: List<Double>): Double {
        return quantile(0.5)(sortedValues)
    }

    fun min(sortedValues: List<Double>): Double {
        return if (sortedValues.isEmpty()) {
            Double.NaN
        } else {
            sortedValues.first()
        }
    }

    fun max(sortedValues: List<Double>): Double {
        return if (sortedValues.isEmpty()) {
            Double.NaN
        } else {
            sortedValues.last()
        }
    }

    fun q1(sortedValues: List<Double>): Double {
        return quantile(0.25)(sortedValues)
    }

    fun q3(sortedValues: List<Double>): Double {
        return quantile(0.75)(sortedValues)
    }

    fun quantile(p: Double): (List<Double>) -> Double {
        return { sortedValues ->
            if (sortedValues.isEmpty()) {
                Double.NaN
            } else if (sortedValues.size == 1) {
                sortedValues.first()
            } else {
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