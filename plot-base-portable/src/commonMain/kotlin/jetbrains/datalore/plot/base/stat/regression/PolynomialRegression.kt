/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.ForsythePolynomialGenerator
import jetbrains.datalore.plot.base.stat.math3.PolynomialFunction
import jetbrains.datalore.plot.base.stat.math3.times
import kotlin.math.pow
import kotlin.math.sqrt

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
            val sumXX = xVals.sumOf { (it - meanX).pow(2) }

            // Prepare model
            val polynomial = calculatePolynomial(deg, xVals, yVals)
            val model: (Double) -> Double = { x -> polynomial.value(x) }

            // Calculate standard error of estimate
            // https://en.wikipedia.org/wiki/Residual_sum_of_squares
            val sse = (xVals zip yVals).sumOf { (x, y) -> (y - model(x)).pow(2) }
            val standardErrorOfEstimate = sqrt(sse / degreesOfFreedom)

            return PolynomialRegression(
                n,
                meanX,
                sumXX,
                model,
                standardErrorOfEstimate,
                tCritical(degreesOfFreedom, confidenceLevel)
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