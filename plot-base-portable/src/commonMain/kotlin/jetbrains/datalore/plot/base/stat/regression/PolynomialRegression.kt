/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.ForsythePolynomialGenerator
import jetbrains.datalore.plot.base.stat.math3.PolynomialFunction
import jetbrains.datalore.plot.base.stat.math3.TDistribution
import jetbrains.datalore.plot.base.stat.math3.times
import kotlin.math.pow
import kotlin.math.sqrt

class PolynomialRegression(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double, private val deg: Int) :
    RegressionEvaluator(xs, ys, confidenceLevel) {

    override val canBeComputed: Boolean
        get() = n > deg

    private val p: PolynomialFunction
    private val n: Int
    private val meanX: Double
    private val sumXX: Double
    private val sy: Double
    private val tcritical: Double

    init {
        require(deg >= 2) { "Degree of polynomial must be at least 2" }

        val (xVals, yVals) = averageByX(xs, ys)
        n = xVals.size

        require(n > deg) { "The number of valid data points must be greater than deg" }

        p = calcPolynomial(deg, xVals, yVals)

        meanX = xVals.average()
        sumXX = xVals.sumOf { (it - meanX).pow(2) }
        val df = n - deg - 1.0

        sy = run { // Standard error of estimate
            val sse = xVals.zip(yVals).sumOf { (x, y) -> (y - p.value(x)).pow(2) }
            sqrt(sse / (df))
        }

        tcritical = run {
            val alpha = 1.0 - confidenceLevel
            TDistribution(df).inverseCumulativeProbability(1.0 - alpha / 2.0)
        }
    }

    private fun calcPolynomial(deg: Int, xVals: DoubleArray, yVals: DoubleArray): PolynomialFunction {
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
        for (i in 0 until xVals.size) {
            val x = xVals[i]
            val y = yVals[i]
            val pval = p.value(x)

            ww += pval * pval
            w += y * pval
        }

        return w / ww
    }

    override fun getEvalX(x: Double): EvalResult {

        val se = run { // standard error of predicted means
            // x deviation squared
            val dxSquare = (x - meanX).pow(2)
            sy * sqrt(1.0 / n + dxSquare / sumXX)
        }

        // half-width of confidence interval for estimated mean y
        val halfConfidenceInterval = tcritical * se

        val yHat = p.value(x)

        return EvalResult(
            yHat,
            yHat - halfConfidenceInterval,
            yHat + halfConfidenceInterval,
            se
        )
    }
}