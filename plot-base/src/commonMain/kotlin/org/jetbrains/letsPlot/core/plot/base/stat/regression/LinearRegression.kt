/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.regression.RsquaredCI.ciRsquaredLikeConfintrFromR2

class LinearRegression private constructor (
    n: Int,
    meanX: Double,
    sumXX: Double,
    model: (Double) -> Double,
    standardErrorOfEstimate: Double,
    tCritical: Double,
    eq: List<Double>,
    r2: Double,
    aic: Double,
    bic: Double,
    fTest: RegressionEvaluator.Companion.FTestResult,
    r2ConfInt: R2ConfIntResult

) : RegressionEvaluator(n, meanX, sumXX, model, standardErrorOfEstimate, tCritical, eq, r2, aic, bic, fTest, r2ConfInt) {
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

            val k = 3 // number of predictors (slope, intercept, sigma^2)
            val rss = calcRss(xVals, yVals, model)
            val r2 = calcRSquared(xVals, yVals, model)

            return LinearRegression(
                n,
                meanX,
                sumXX,
                model,
                calcStandardErrorOfEstimate(xVals, yVals, model, degreesOfFreedom),
                calcTCritical(degreesOfFreedom, confidenceLevel),
                listOf(intercept, slope),
                r2,
                calcAic(n, rss, k),
                calcBic(n, rss, k),
                calcOverallModelFTest(n, 2, r2),
                ciRsquaredLikeConfintrFromR2(n, 2, r2, confidenceLevel)
            )
        }
    }
}