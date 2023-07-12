/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.math3.LoessInterpolator
import org.jetbrains.letsPlot.core.plot.base.stat.math3.PolynomialSplineFunction

class LocalPolynomialRegression private constructor (
    n: Int,
    meanX: Double,
    sumXX: Double,
    model: (Double) -> Double,
    standardErrorOfEstimate: Double,
    tCritical: Double
) : RegressionEvaluator(n, meanX, sumXX, model, standardErrorOfEstimate, tCritical) {
    companion object {
        fun fit(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double, bandwidth: Double): LocalPolynomialRegression? {
            check(xs, ys, confidenceLevel)

            // Prepare data
            val (xVals, yVals) = averageByX(xs, ys)
            val n = xVals.size
            val degreesOfFreedom = n - 2.0

            // Check computability
            if (!canBeComputed(n, degreesOfFreedom, bandwidth)) {
                return null
            }

            // Calculate standard stats
            val meanX = xVals.average()
            val sumXX = sumOfSquaredDeviations(xVals, meanX)

            // Prepare model
            val polynomial = getPolynomial(xVals, yVals, bandwidth)
            val model: (Double) -> Double = { x -> polynomial.value(x)!! }

            return LocalPolynomialRegression(
                n,
                meanX,
                sumXX,
                model,
                calcStandardErrorOfEstimate(xVals, yVals, model, degreesOfFreedom),
                calcTCritical(degreesOfFreedom, confidenceLevel)
            )
        }

        private fun canBeComputed(n: Int, degreesOfFreedom: Double, bandwidth: Double): Boolean {
            // See: LoessInterpolator.kt:168
            val bandwidthInPoints = (bandwidth * n).toInt()
            val bandwidthInPointsOk = bandwidthInPoints >= 2
            return n >= 3 && degreesOfFreedom > 0 && bandwidthInPointsOk
        }

        private fun getPolynomial(xVals: DoubleArray, yVals: DoubleArray, bandwidth: Double): PolynomialSplineFunction {
            return LoessInterpolator(bandwidth, 4).interpolate(xVals, yVals)
        }
    }
}