/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.math3.ForsythePolynomialGenerator
import org.jetbrains.letsPlot.core.plot.base.stat.math3.PolynomialFunction
import org.jetbrains.letsPlot.core.plot.base.stat.math3.times

class PolynomialRegression private constructor (
    n: Int,
    meanX: Double,
    sumXX: Double,
    model: (Double) -> Double,
    standardErrorOfEstimate: Double,
    tCritical: Double
) : RegressionEvaluator(n, meanX, sumXX, model, standardErrorOfEstimate, tCritical) {
    companion object {
        fun fit(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double, deg: Int): PolynomialRegression? {
            check(xs, ys, confidenceLevel)
            require(deg >= 2) { "Degree of polynomial must be at least 2" }

            // Prepare data
            val (xVals, yVals) = averageByX(xs, ys)
            val n = xVals.size
            val degreesOfFreedom = n - deg - 1.0

            // Check computability
            if (n <= deg) {
                return null
            }

            // Calculate standard stats
            val meanX = xVals.average()
            val sumXX = sumOfSquaredDeviations(xVals, meanX)

            // Prepare model
            val polynomial = calculatePolynomial(deg, xVals, yVals)
            val model: (Double) -> Double = { x -> polynomial.value(x) }

            return PolynomialRegression(
                n,
                meanX,
                sumXX,
                model,
                calcStandardErrorOfEstimate(xVals, yVals, model, degreesOfFreedom),
                calcTCritical(degreesOfFreedom, confidenceLevel)
            )
        }

        private fun calculatePolynomial(deg: Int, xVals: DoubleArray, yVals: DoubleArray): PolynomialFunction {
            val fpg = ForsythePolynomialGenerator(xVals)
            var res = PolynomialFunction(doubleArrayOf(0.0))

            for (i in 0..deg) {
                val p = fpg.getPolynomial(i)
                val s = coefficient(p, xVals, yVals)
                res += s * p
            }

            return res
        }

        private fun coefficient(p: PolynomialFunction, xVals: DoubleArray, yVals: DoubleArray): Double {
            var ww = 0.0
            var w = 0.0
            for (i in xVals.indices) {
                val x = xVals[i]
                val y = yVals[i]
                val pval = p.value(x)

                ww += pval * pval
                w += y * pval
            }

            return w / ww
        }
    }
}