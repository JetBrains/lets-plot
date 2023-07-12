/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

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
            val sumXX = sumOfSquaredDeviations(xVals, meanX)

            // Prepare model
            val meanY = yVals.average()
            val sumXY = sumOfDeviationProducts(xVals, yVals, meanX, meanY)
            val slope = sumXY / sumXX
            val intercept = meanY - slope * meanX
            val model: (Double) -> Double = { x -> slope * x + intercept }

            return LinearRegression(
                n,
                meanX,
                sumXX,
                model,
                calcStandardErrorOfEstimate(xVals, yVals, model, degreesOfFreedom),
                calcTCritical(degreesOfFreedom, confidenceLevel)
            )
        }
    }
}