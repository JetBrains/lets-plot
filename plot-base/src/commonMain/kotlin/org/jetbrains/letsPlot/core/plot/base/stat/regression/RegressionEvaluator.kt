/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.math3.Beta
import org.jetbrains.letsPlot.core.stat.tQuantile
import kotlin.math.*

abstract class RegressionEvaluator protected constructor(
    private val xVals: DoubleArray,
    private val yVals: DoubleArray,
    private val model: (Double) -> Double,
    private val degreesOfFreedom: Double,
    private val confidenceLevel: Double,
    val eq: List<Double>
) {
    internal val n by lazy { xVals.size }
    internal val r2 by lazy { calcRSquared(xVals, yVals, model) }
    internal val adjustedR2 by lazy { calcAdjustedRSquared(xVals.size, eq.size, r2) }
    internal val rss by lazy { calcRss(xVals, yVals, model) }
    internal val aic by lazy { calcAic(xVals.size, rss, eq.size) }
    internal val bic by lazy { calcBic(xVals.size, rss, eq.size) }
    internal val fTest by lazy { calcOverallModelFTest(xVals.size, eq.size, r2) }
    internal val r2ConfInt by lazy { calcR2ConfInt(xVals.size, eq.size, r2, confidenceLevel) }

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

        // Calculate standard stats
        val meanX = xVals.average()
        val sumXX = sumOfSquaredDeviations(xVals, meanX)
        val standardErrorOfEstimate = calcStandardErrorOfEstimate(xVals, yVals, model, degreesOfFreedom)
        val tCritical = calcTCritical(degreesOfFreedom, confidenceLevel)

        // standard error of predicted means
        val se = run {
            val dxSquare = (x - meanX).pow(2)
            standardErrorOfEstimate * sqrt(1.0 / xVals.size + dxSquare / sumXX)
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

        private fun calcStandardErrorOfEstimate(
            xVals: DoubleArray,
            yVals: DoubleArray,
            model: (Double) -> Double,
            degreesOfFreedom: Double
        ): Double {
            // https://en.wikipedia.org/wiki/Residual_sum_of_squares
            val sse = (xVals zip yVals).sumOf { (x, y) -> (y - model(x)).pow(2) }
            return sqrt(sse / degreesOfFreedom)
        }

        private fun calcTCritical(degreesOfFreedom: Double, confidenceLevel: Double): Double {
            return if (degreesOfFreedom > 0) {
                val alpha = 1.0 - confidenceLevel
                tQuantile(degreesOfFreedom)(1.0 - alpha / 2.0)
            } else {
                Double.NaN
            }
        }

        internal data class FTestResult(
            val fValue: Double,
            val pValue: Double,
            val df1: Double,
            val df2: Double
        )

        internal data class R2ConfIntResult(
            val level: Double,
            val low: Double,
            val high: Double
        )

        private data class NcpConfIntResult(
            val estimate: Double,
            val low: Double,
            val high: Double
        )

        private fun calcR2ConfInt(
            n: Int,
            eqSize: Int,
            r2: Double,
            confidenceLevel: Double
        ): R2ConfIntResult {
            if (n <= 0 || eqSize <= 0 || !r2.isFinite()) {
                return R2ConfIntResult(confidenceLevel, Double.NaN, Double.NaN)
            }

            val df1 = (eqSize - 1).toDouble()
            val df2 = n - eqSize.toDouble()

            if (df1 <= 0.0 || df2 <= 0.0) {
                return R2ConfIntResult(confidenceLevel, Double.NaN, Double.NaN)
            }

            val fStat = when (val r2c = r2.coerceIn(0.0, 1.0)) {
                0.0 -> 0.0
                1.0 -> Double.POSITIVE_INFINITY
                else -> (r2c / (1.0 - r2c)) * (df2 / df1)
            }

            if (fStat == Double.POSITIVE_INFINITY) {
                return R2ConfIntResult(confidenceLevel, 1.0, 1.0)
            }

            return ciRSquaredLikeConfIntR(
                fStat = fStat,
                df1 = df1,
                df2 = df2,
                confidenceLevel = confidenceLevel
            )
        }

        private fun calcRSquared(
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

        private fun calcAdjustedRSquared(n: Int, nCoef: Int, r2: Double): Double {
            val predictorsCount = (nCoef - 1).coerceAtLeast(0)
            if (n <= predictorsCount + 1 || r2.isNaN()) {
                return Double.NaN
            }
            return 1.0 - (1.0 - r2) * ((n - 1.0) / (n - predictorsCount - 1.0))
        }

        private fun calcRss(xVals: DoubleArray, yVals: DoubleArray, model: (Double) -> Double): Double {
            var rss = 0.0
            for (i in xVals.indices) {
                val e = yVals[i] - model(xVals[i])
                rss += e * e
            }
            return rss
        }

        private fun calcAic(n: Int, rss: Double, predictorsCount: Int): Double {
            val k = predictorsCount + 1
            if (n <= 0 || k <= 0 || !rss.isFinite()) return Double.NaN
            // Guard against log(0) in a perfect fit
            val rssSafe = maxOf(rss, 1e-12)
            return n * ln(rssSafe / n) +
                    n * (1.0 + ln(2.0 * PI)) +
                    2.0 * k
        }

        private fun calcBic(n: Int, rss: Double, predictorsCount: Int): Double {
            val k = predictorsCount + 1
            if (n <= 0 || k <= 0 || !rss.isFinite()) return Double.NaN
            // Guard against log(0) in a perfect fit
            val rssSafe = maxOf(rss, 1e-12)
            return n * ln(rssSafe / n) +
                    n * (1.0 + ln(2.0 * PI)) +
                    k * ln(n.toDouble())
        }


        private fun calcOverallModelFTest(
            nRaw: Int,
            eqSizeRaw: Int,
            r2Raw: Double
        ): FTestResult {
            val n = nRaw.toDouble()
            val p = (eqSizeRaw - 1).toDouble()

            val df1 = p
            val df2 = n - p - 1.0

            if (!r2Raw.isFinite() || n <= 0.0 || eqSizeRaw <= 0 || df1 <= 0.0 || df2 <= 0.0) {
                return FTestResult(Double.NaN, Double.NaN, df1, df2)
            }

            val r2 = r2Raw.coerceIn(0.0, 1.0)

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

        private fun fDistributionCdf(x: Double, df1: Double, df2: Double): Double {
            if (x <= 0.0) return 0.0
            if (x.isNaN()) return Double.NaN
            if (df1 <= 0.0) return Double.NaN
            if (df2 <= 0.0) return Double.NaN

            val z = (df1 * x) / (df1 * x + df2)

            return Beta.regularizedBeta(z, df1 / 2.0, df2 / 2.0)
        }

        private fun fToNcp(f: Double, df1: Double, df2: Double): Double {
            if (!f.isFinite() || f < 0.0 || df1 <= 0.0 || df2 <= 0.0) return Double.NaN
            return df1 * f * (df1 + df2 + 1.0) / df2
        }

        private fun ciFNoncentrality(
            fStat: Double,
            df1: Double,
            df2: Double,
            probsLow: Double,
            probsHigh: Double,
            absTol: Double = 1e-10
        ): NcpConfIntResult {
            if (!fStat.isFinite() || fStat < 0.0 || df1 <= 0.0 || df2 <= 0.0) {
                return NcpConfIntResult(Double.NaN, Double.NaN, Double.NaN)
            }
            if (probsLow !in 0.0..1.0 || probsHigh !in 0.0..1.0 || probsLow > probsHigh) {
                return NcpConfIntResult(Double.NaN, Double.NaN, Double.NaN)
            }

            val estimate = fToNcp(fStat, df1, df2)
            if (!estimate.isFinite()) {
                return NcpConfIntResult(Double.NaN, Double.NaN, Double.NaN)
            }

            val targetLower = 1.0 - probsLow
            val targetUpper = 1.0 - probsHigh

            val low = if (probsLow == 0.0) {
                0.0
            } else {
                val fn: (Double) -> Double = { ncp ->
                    nonCentralFDistributionCDF(fStat, df1, df2, ncp) - targetLower
                }

                bisectionRootOrNull(fn, 0.0, estimate.coerceAtLeast(0.0), absTol) ?: 0.0
            }

            val high = if (probsHigh == 1.0) {
                Double.POSITIVE_INFINITY
            } else {
                val fn: (Double) -> Double = { ncp ->
                    nonCentralFDistributionCDF(fStat, df1, df2, ncp) - targetUpper
                }

                var upper = maxOf(4.0 * estimate, fStat * df1 * 4.0, df1 * 100.0)

                var root = bisectionRootOrNull(fn, estimate.coerceAtLeast(0.0), upper, absTol)
                var tries = 0
                while (root == null && tries < 20 && upper.isFinite()) {
                    upper *= 2.0
                    root = bisectionRootOrNull(fn, estimate.coerceAtLeast(0.0), upper, absTol)
                    tries++
                }
                root ?: Double.POSITIVE_INFINITY
            }

            return NcpConfIntResult(estimate, low, high)
        }

        private fun bisectionRootOrNull(
            f: (Double) -> Double,
            a0: Double,
            b0: Double,
            absTol: Double,
            maxIter: Int = 200
        ): Double? {
            var a = a0
            var b = b0
            if (!a.isFinite() || !b.isFinite() || a > b) return null

            var fa = f(a)
            val fb = f(b)
            if (!fa.isFinite() || !fb.isFinite()) return null

            if (fa == 0.0) return a
            if (fb == 0.0) return b

            if (fa * fb > 0.0) return null

            repeat(maxIter) {
                val m = 0.5 * (a + b)
                val fm = f(m)
                if (!fm.isFinite()) return null

                if (fm == 0.0) return m
                if ((b - a) <= absTol * (1.0 + kotlin.math.abs(a) + kotlin.math.abs(b))) {
                    return 0.5 * (a + b)
                }

                if (fa * fm <= 0.0) {
                    b = m
                } else {
                    a = m
                    fa = fm
                }
            }

            return 0.5 * (a + b)
        }

        // CDF of the non-central F distribution via Poisson mixture of central F distributions.
        private fun nonCentralFDistributionCDF(
            x: Double,
            df1: Double,
            df2: Double,
            ncp: Double,
            eps: Double = 1e-12,
            maxTerms: Int = 100000
        ): Double {
            if (x.isNaN() || df1 <= 0.0 || df2 <= 0.0 || ncp < 0.0) return Double.NaN
            if (x <= 0.0) return 0.0
            if (!x.isFinite()) return 1.0

            val z = (df1 * x) / (df1 * x + df2)
            if (!z.isFinite()) return Double.NaN
            if (z <= 0.0) return 0.0
            if (z >= 1.0) return 1.0

            val a0 = df1 / 2.0
            val b = df2 / 2.0
            val mu = ncp / 2.0

            if (mu == 0.0) {
                return Beta.regularizedBeta(z, a0, b).coerceIn(0.0, 1.0)
            }

            var w = exp(-mu)
            if (w == 0.0) {
                return cumulativeProbabilityCenterSummation(x, df1, df2, ncp, eps, maxTerms)
            }

            var sum = 0.0
            var weightSum = 0.0
            var j = 0

            while (j < maxTerms) {
                val a = a0 + j
                val termCdf = Beta.regularizedBeta(z, a, b)
                val term = w * termCdf

                sum += term
                weightSum += w

                if (w < eps && (1.0 - weightSum) < 10 * eps) {
                    break
                }

                j += 1
                w *= mu / j.toDouble()

                if (!w.isFinite()) return Double.NaN
                if (w == 0.0 && (1.0 - weightSum) < 1e-8) break
            }

            return sum.coerceIn(0.0, 1.0)
        }

        private fun cumulativeProbabilityCenterSummation(
            x: Double,
            df1: Double,
            df2: Double,
            ncp: Double,
            eps: Double,
            maxTerms: Int
        ): Double {
            val z = (df1 * x) / (df1 * x + df2)
            val a0 = df1 / 2.0
            val b = df2 / 2.0
            val mu = ncp / 2.0

            val m = kotlin.math.floor(mu).toInt().coerceAtLeast(0)

            var logFact = 0.0
            for (k in 2..m) logFact += ln(k.toDouble())
            val wM = exp(-mu + if (m == 0) 0.0 else m * ln(mu) - logFact)

            if (!wM.isFinite() || wM == 0.0) {
                return Double.NaN
            }

            var sum = 0.0
            var weightAccum = 0.0

            run {
                val cdfM = Beta.regularizedBeta(z, a0 + m, b)
                sum += wM * cdfM
                weightAccum += wM
            }

            var wUp = wM
            var j = m
            var upSteps = 0
            while (upSteps < maxTerms) {
                j += 1
                wUp *= mu / j.toDouble()
                if (!wUp.isFinite() || wUp <= 0.0) break

                val cdf = Beta.regularizedBeta(z, a0 + j, b)
                sum += wUp * cdf
                weightAccum += wUp

                upSteps++
                if (wUp < eps) break
            }

            var wDown = wM
            j = m
            var downSteps = 0
            while (j > 0 && downSteps < maxTerms) {
                wDown *= j.toDouble() / mu
                j -= 1
                if (!wDown.isFinite() || wDown <= 0.0) break

                val cdf = Beta.regularizedBeta(z, a0 + j, b)
                sum += wDown * cdf
                weightAccum += wDown

                downSteps++
                if (wDown < eps) break
            }

            return sum.coerceIn(0.0, 1.0)
        }

        private fun ncpToR2(ncp: Double, df1: Double, df2: Double): Double {
            if (ncp.isNaN() || ncp < 0.0 || df1 <= 0.0 || df2 <= 0.0) return Double.NaN
            if (ncp == Double.POSITIVE_INFINITY) return 1.0
            return (ncp / (ncp + df1 + df2 + 1.0)).coerceIn(0.0, 1.0)
        }

        private fun ciRSquaredLikeConfIntR(
            fStat: Double,
            df1: Double,
            df2: Double,
            confidenceLevel: Double
        ): R2ConfIntResult {
            if (!fStat.isFinite() || fStat < 0.0 || df1 <= 0.0 || df2 <= 0.0 || confidenceLevel <= 0.0 || confidenceLevel >= 1.0) {
                return R2ConfIntResult(confidenceLevel, Double.NaN, Double.NaN)
            }

            val alpha = 1.0 - confidenceLevel
            val probsLow = alpha / 2.0
            val probsHigh = 1.0 - alpha / 2.0

            val ncpCi = ciFNoncentrality(
                fStat = fStat,
                df1 = df1,
                df2 = df2,
                probsLow = probsLow,
                probsHigh = probsHigh
            )

            val low = ncpToR2(ncpCi.low, df1, df2)
            val high = ncpToR2(ncpCi.high, df1, df2)

            return R2ConfIntResult(confidenceLevel, low, high)
        }

        private fun fTestPValueUpperTail(fValue: Double, df1: Double, df2: Double): Double {
            if (!fValue.isFinite() || df1 <= 0.0 || df2 <= 0.0) return Double.NaN

            if (fValue < 0.0) return Double.NaN
            if (fValue == Double.POSITIVE_INFINITY) return 0.0

            val cdf = fDistributionCdf(fValue, df1, df2)

            return (1.0 - cdf).coerceIn(0.0, 1.0)
        }
    }
}
