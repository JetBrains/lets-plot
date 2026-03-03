/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.math3.LoessInterpolator
import org.jetbrains.letsPlot.core.plot.base.stat.math3.PolynomialSplineFunction

class LocalPolynomialRegression private constructor (
    xVals: DoubleArray,
    yVals: DoubleArray,
    model: (Double) -> Double,
    confidenceLevel: Double,
) : RegressionEvaluator(xVals, yVals, model, 1.0, confidenceLevel, emptyList()) {
    companion object {
        fun fit(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double, bandwidth: Double): LocalPolynomialRegression? {
            check(xs, ys, confidenceLevel)

            // Prepare data
            val (xVals, yVals) = averageByX(xs, ys)
            val n = xVals.size

            // Check computability
            // See: LoessInterpolator.kt:168
            if (n < 3) {
                return null
            }

            if (bandwidth * n < 2) {
                return null
            }

            // Prepare model
            val polynomial = getPolynomial(xVals, yVals, bandwidth)
            val model: (Double) -> Double = { x -> polynomial.value(x)!! }

            return LocalPolynomialRegression(
                xVals,
                yVals,
                model,
                confidenceLevel
            )
        }

        private fun getPolynomial(xVals: DoubleArray, yVals: DoubleArray, bandwidth: Double): PolynomialSplineFunction {
            return LoessInterpolator(bandwidth, 4).interpolate(xVals, yVals)
        }
    }
}