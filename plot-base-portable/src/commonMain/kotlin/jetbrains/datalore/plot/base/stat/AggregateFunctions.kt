/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.DataFrame
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

object AggregateFunctions {
    fun count(values: List<Double>): Double = values.size.toDouble()

    fun sum(values: List<Double>): Double {
        return when (values.size) {
            0 -> Double.NaN
            else -> values.sum()
        }
    }

    fun mean(values: List<Double>): Double {
        return when (values.size) {
            0 -> Double.NaN
            else -> sum(values) / count(values)
        }
    }

    fun median(sortedValues: List<Double>): Double = quantile(sortedValues, 0.5)

    fun min(sortedValues: List<Double>): Double = sortedValues.firstOrNull() ?: Double.NaN

    fun max(sortedValues: List<Double>): Double = sortedValues.lastOrNull() ?: Double.NaN

    fun quantile(sortedValues: List<Double>, p: Double): Double {
        if (sortedValues.isEmpty()) {
            return Double.NaN
        }
        val place = p * (sortedValues.size - 1)
        return when (round(place)) {
            place -> sortedValues[place.toInt()]
            else -> (sortedValues[ceil(place).toInt()] + sortedValues[floor(place).toInt()]) / 2.0
        }
    }

    fun byStat(statVar: DataFrame.Variable, sortedQuantiles: List<Double>): (List<Double>) -> Double {
        return when (statVar) {
            Stats.COUNT -> this::count
            Stats.SUM -> this::sum
            Stats.MEAN -> this::mean
            Stats.MEDIAN -> this::median
            Stats.Y_MIN -> this::min
            Stats.Y_MAX -> this::max
            Stats.LOWER_QUANTILE -> { values -> quantile(values, sortedQuantiles[0]) }
            Stats.MIDDLE_QUANTILE -> { values -> quantile(values, sortedQuantiles[1]) }
            Stats.UPPER_QUANTILE -> { values -> quantile(values, sortedQuantiles[2]) }
            else -> throw IllegalStateException(
                "Unsupported stat variable: '${statVar.name}'\n" +
                "Use one of: ..count.., ..sum.., ..mean.., ..median.., ..ymin.., ..ymax.., ..lq.., ..mq.., ..uq.."
            )
        }
    }
}