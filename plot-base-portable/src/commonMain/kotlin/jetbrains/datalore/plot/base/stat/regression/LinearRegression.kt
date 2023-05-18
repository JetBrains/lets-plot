/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class LinearRegression private constructor (
    n: Int,
    meanX: Double,
    sumXX: Double,
    model: (Double) -> Double,
    standardErrorOfEstimate: Double,
    tCritical: Double
) : RegressionEvaluator(n, meanX, sumXX, model, standardErrorOfEstimate, tCritical) {
    companion object {
        fun fit(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double): LinearRegression? {
            check(xs, ys, confidenceLevel)

            // Prepare data
            val (xVals, yVals) = allFinite(xs, ys)
            val n = xVals.size
            val degreesOfFreedom = n - 2.0

            // Check computability
            if (n <= 1) {
                return null
            }

            // Calculate standard stats
            val meanX = xVals.average()
            val sumXX = xVals.sumOf { (it - meanX).pow(2) }

            // Prepare model
            val meanY = yVals.average()
            val sumXY = xVals.zip(yVals).sumOf { (x, y) -> (x - meanX) * (y - meanY) }
            val slope = sumXY / sumXX
            val intercept = meanY - slope * meanX
            val model: (Double) -> Double = { x -> slope * x + intercept }

            // Calculate standard error of estimate
            // https://en.wikipedia.org/wiki/Residual_sum_of_squares
            val sumYY = yVals.sumOf { (it - meanY).pow(2) }
            val sse = max(0.0, sumYY - sumXY * sumXY / sumXX)
            val standardErrorOfEstimate = sqrt(sse / degreesOfFreedom)

            return LinearRegression(
                n,
                meanX,
                sumXX,
                model,
                standardErrorOfEstimate,
                tCritical(degreesOfFreedom, confidenceLevel)
            )
        }
    }
}