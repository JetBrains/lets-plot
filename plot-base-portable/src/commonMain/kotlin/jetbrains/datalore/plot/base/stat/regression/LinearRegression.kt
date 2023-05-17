/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class LinearRegression(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double) :
    RegressionEvaluator(xs, ys, confidenceLevel) {

    override val canBeComputed: Boolean
        get() = n > 1

    override val degreesOfFreedom: Double
        get() = n - 2.0

    private val slope: Double
    private val intercept: Double

    init {
        val (xVals, yVals) = prepareData(xs, ys)

        val meanY = yVals.average()
        val sumXY = xVals.zip(yVals).sumOf { (x, y) -> (x - meanX) * (y - meanY) }

        slope = sumXY / sumXX
        intercept = meanY - slope * meanX
    }

    override fun prepareData(xs: List<Double?>, ys: List<Double?>): Pair<DoubleArray, DoubleArray> {
        return allFinite(xs, ys)
    }

    override fun value(x: Double): Double = slope * x + intercept

    override fun standardErrorOfEstimate(xVals: DoubleArray, yVals: DoubleArray): Double {
        val meanY = yVals.average()
        val sumXY = xVals.zip(yVals).sumOf { (x, y) -> (x - meanX) * (y - meanY) }
        val sumYY = yVals.sumOf { (it - meanY).pow(2) }
        val sse = max(0.0, sumYY - sumXY * sumXY / sumXX) // https://en.wikipedia.org/wiki/Residual_sum_of_squares
        return sqrt(sse / degreesOfFreedom) // SE estimate
    }
}