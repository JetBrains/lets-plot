/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.gcommon.collect.Ordering
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

object SummaryStatUtil {
    fun getStandardAggFun(aggFunName: AggFun): (SummaryCalculator) -> Double {
        return when (aggFunName) {
            AggFun.NAN -> { calc -> calc.nan() }
            AggFun.COUNT -> { calc -> calc.count() }
            AggFun.SUM -> { calc -> calc.sum() }
            AggFun.MEAN -> { calc -> calc.mean() }
            AggFun.MEDIAN -> { calc -> calc.median() }
            AggFun.MIN -> { calc -> calc.min() }
            AggFun.MAX -> { calc -> calc.max() }
            AggFun.Q1 -> { calc -> calc.q1() }
            AggFun.Q3 -> { calc -> calc.q3() }
        }
    }

    fun getQuantileAggFun(p: Double): (SummaryCalculator) -> Double {
        return { calc -> calc.quantile(p) }
    }

    class SummaryCalculator(values: List<Double>) {
        private val sortedValues: List<Double> = Ordering.natural<Double>().sortedCopy(values)

        private var count: Double? = null
        private var sum: Double? = null
        private var mean: Double? = null
        private var median: Double? = null
        private var min: Double? = null
        private var max: Double? = null
        private var q1: Double? = null
        private var q3: Double? = null

        fun nan(): Double {
            return Double.NaN
        }

        fun count(): Double {
            if (count == null) {
                count = sortedValues.size.toDouble()
            }
            return count!!
        }

        fun sum(): Double {
            if (sum == null) {
                sum = sortedValues.sum()
            }
            return sum!!
        }

        fun mean(): Double {
            if (mean == null) {
                mean = if (sortedValues.isEmpty()) {
                    Double.NaN
                } else if (sortedValues.size == 1) {
                    sortedValues.first()
                } else {
                    sum() / sortedValues.size
                }
            }
            return mean!!
        }

        fun median(): Double {
            if (median == null) {
                median = quantile(0.5)
            }
            return median!!
        }

        fun min(): Double {
            if (min == null) {
                min = if (sortedValues.isEmpty()) {
                    Double.NaN
                } else {
                    sortedValues.first()
                }
            }
            return min!!
        }

        fun max(): Double {
            if (max == null) {
                max = if (sortedValues.isEmpty()) {
                    Double.NaN
                } else {
                    sortedValues.last()
                }
            }
            return max!!
        }

        fun q1(): Double {
            if (q1 == null) {
                q1 = quantile(0.25)
            }
            return q1!!
        }

        fun q3(): Double {
            if (q3 == null) {
                q3 = quantile(0.75)
            }
            return q3!!
        }

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

    enum class AggFun {
        NAN,
        COUNT,
        SUM,
        MEAN,
        MEDIAN,
        MIN,
        MAX,
        Q1,
        Q3,
    }
}