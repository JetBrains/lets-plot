/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.regression.FDistribution
import org.jetbrains.letsPlot.core.stat.tQuantile
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

abstract class RegressionEvaluator protected constructor(
    val n: Int,
    private val meanX: Double,
    private val sumXX: Double,
    private val model: (Double) -> Double,
    private val standardErrorOfEstimate: Double,
    private val tCritical: Double,
    val eq: List<Double>,
    val r2: Double,
    val aic: Double,
    val bic: Double,
    val fTest: FTestResult
) {
    val adjR2: Double
        get() {
            val predictorsCount = (eq.size - 1).coerceAtLeast(0)
            if (n <= predictorsCount + 1 || r2.isNaN()) {
                return Double.NaN
            }
            return 1.0 - (1.0 - r2) * ((n - 1.0) / (n - predictorsCount - 1.0))
        }

    fun value(x: Double): Double {
        return model(x)
    }

    fun evalX(x: Double): EvalResult {
        // confidence interval for the conditional mean
        // https://www.ma.utexas.edu/users/mks/statmistakes/CIvsPI.html
        // https://onlinecourses.science.psu.edu/stat414/node/297

        // https://www2.stat.duke.edu/~tjl13/s101/slides/unit6lec3H.pdf
        // Stat symbols:
        // https://brownmath.com/swt/symbol.htm

        // standard error of predicted means
        val se = run {
            val dxSquare = (x - meanX).pow(2)
            standardErrorOfEstimate * sqrt(1.0 / n + dxSquare / sumXX)
        }

        // half-width of confidence interval for estimated mean y
        val halfConfidenceInterval = tCritical * se
        val yHat = value(x)

        return EvalResult(
            yHat,
            yHat - halfConfidenceInterval,
            yHat + halfConfidenceInterval,
            se
        )
    }

    companion object {
        fun check(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double) {
            require(confidenceLevel in 0.01..0.99) { "Confidence level is out of range [0.01-0.99]. CL:$confidenceLevel" }
            require(xs.size == ys.size) { "X/Y must have same size. X:" + xs.size + " Y:" + ys.size }
        }

        fun calcStandardErrorOfEstimate(
            xVals: DoubleArray,
            yVals: DoubleArray,
            model: (Double) -> Double,
            degreesOfFreedom: Double
        ): Double {
            // https://en.wikipedia.org/wiki/Residual_sum_of_squares
            val sse = (xVals zip yVals).sumOf { (x, y) -> (y - model(x)).pow(2) }
            return sqrt(sse / degreesOfFreedom)
        }

        fun calcTCritical(degreesOfFreedom: Double, confidenceLevel: Double): Double {
            return if (degreesOfFreedom > 0) {
                val alpha = 1.0 - confidenceLevel
                tQuantile(degreesOfFreedom)(1.0 - alpha / 2.0)
            } else {
                Double.NaN
            }
        }

        fun calcRSquared(
            xVals: DoubleArray,
            yVals: DoubleArray,
            model: (Double) -> Double
        ): Double {
            val meanY = yVals.average()

            var ssTot = 0.0
            var ssRes = 0.0

            for (i in xVals.indices) {
                val y = yVals[i]
                val yHat = model(xVals[i])

                val diffRes = y - yHat
                ssRes += diffRes * diffRes

                val diffMean = y - meanY
                ssTot += diffMean * diffMean
            }

            return if (ssTot == 0.0) {
                0.0
            } else {
                1.0 - ssRes / ssTot
            }
        }

        fun calcRss(xVals: DoubleArray, yVals: DoubleArray, model: (Double) -> Double): Double {
            var rss = 0.0
            for (i in xVals.indices) {
                val e = yVals[i] - model(xVals[i])
                rss += e * e
            }
            return rss
        }

        fun calcAic(n: Int, rss: Double, k: Int): Double {
            if (n <= 0 || k <= 0 || !rss.isFinite()) return Double.NaN
            // Guard against log(0) in a perfect fit
            val rssSafe = maxOf(rss, 1e-12)
            return n * kotlin.math.ln(rssSafe / n) +
                    n * (1.0 + kotlin.math.ln(2.0 * PI)) +
                    2.0 * k
        }

        fun calcBic(n: Int, rss: Double, k: Int): Double {
            if (n <= 0 || k <= 0 || !rss.isFinite()) return Double.NaN
            // Guard against log(0) in a perfect fit
            val rssSafe = maxOf(rss, 1e-12)
            return n * kotlin.math.ln(rssSafe / n) +
                    n * (1.0 + kotlin.math.ln(2.0 * PI)) +
                    k * kotlin.math.ln(n.toDouble())
        }

        data class FTestResult(
            val fValue: Double,
            val pValue: Double,
            val df1: Double,
            val df2: Double
        )

        internal fun calcOverallModelFTest(
            nRaw: Int,
            eqSizeRaw: Int,   // includes intercept
            r2Raw: Double
        ): FTestResult {
            val n = nRaw.toDouble()
            val p = (eqSizeRaw - 1).toDouble()   // predictors without intercept

            val df1 = p
            val df2 = n - p - 1.0

            // Invalid setup
            if (!r2Raw.isFinite() || n <= 0.0 || eqSizeRaw <= 0 || df1 <= 0.0 || df2 <= 0.0) {
                return FTestResult(Double.NaN, Double.NaN, df1, df2)
            }

            // Clamp possible floating-point overshoots
            val r2 = r2Raw.coerceIn(0.0, 1.0)

            // Edge cases
            if (r2 == 0.0) {
                return FTestResult(0.0, 1.0, df1, df2)
            }
            if (r2 == 1.0) {
                return FTestResult(Double.POSITIVE_INFINITY, 0.0, df1, df2)
            }

            val numerator = r2 / df1
            val denominator = (1.0 - r2) / df2

            if (!numerator.isFinite() || !denominator.isFinite() || denominator <= 0.0) {
                return FTestResult(Double.NaN, Double.NaN, df1, df2)
            }

            val fValue = numerator / denominator

            if (!fValue.isFinite()) {
                return if (fValue == Double.POSITIVE_INFINITY) {
                    FTestResult(Double.POSITIVE_INFINITY, 0.0, df1, df2)
                } else {
                    FTestResult(Double.NaN, Double.NaN, df1, df2)
                }
            }

            val pValue = fTestPValueUpperTail(fValue, df1, df2)
            return FTestResult(fValue, pValue, df1, df2)
        }

        internal fun fTestPValueUpperTail(fValue: Double, df1: Double, df2: Double): Double {
            if (!fValue.isFinite() || df1 <= 0.0 || df2 <= 0.0) return Double.NaN

            if (fValue < 0.0) return Double.NaN
            if (fValue == Double.POSITIVE_INFINITY) return 0.0

            val cdf = FDistribution(df1, df2).cumulativeProbability(fValue)

            // Guard against tiny numerical drift outside [0, 1]
            return (1.0 - cdf).coerceIn(0.0, 1.0)
        }

    }
}
