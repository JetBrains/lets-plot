/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.TDistribution
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class LinearRegression(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double) :
    RegressionEvaluator(xs, ys, confidenceLevel) {

    override val canBeComputed: Boolean
        get() = n > 1

    private val n: Int
    private val meanX: Double
    private val sumXX: Double
    private val beta1: Double
    private val beta0: Double
    private val sy: Double // Standard error of estimate
    private val tcritical: Double

    init {
        val (xVals, yVals) = allFinite(xs, ys)
        n = xVals.size
        meanX = xVals.average()
        sumXX = xVals.sumOf { (it - meanX).pow(2) }

        val meanY = yVals.average()
        val sumYY = yVals.sumOf { (it - meanY).pow(2) }
        val sumXY = xVals.zip(yVals).sumOf { (x, y) -> (x - meanX) * (y - meanY) }

        beta1 = sumXY / sumXX
        beta0 = meanY - beta1 * meanX

        sy = run { // Standard error of estimate
            val sse = max(0.0, sumYY - sumXY * sumXY / sumXX) // https://en.wikipedia.org/wiki/Residual_sum_of_squares
            sqrt(sse / (n - 2)) // SE estimate
        }

        tcritical = run {
            val alpha = 1.0 - confidenceLevel
            TDistribution(n - 2.0).inverseCumulativeProbability(1.0 - alpha / 2.0)
        }
    }

    private fun value(x: Double): Double = beta1 * x + beta0

    override fun getEvalX(x: Double): EvalResult {

        // confidence interval for the conditional mean
        // https://www.ma.utexas.edu/users/mks/statmistakes/CIvsPI.html
        // https://onlinecourses.science.psu.edu/stat414/node/297

        // https://www2.stat.duke.edu/~tjl13/s101/slides/unit6lec3H.pdf
        // Stat symbols:
        // https://brownmath.com/swt/symbol.htm


        // standard error (of estimate?)
        val se = run {// standard error of predicted means
            // x deviation squared
            val dxSquare = (x - meanX).pow(2)
            sy * sqrt(1.0 / n + dxSquare / sumXX)
        }

        // half-width of confidence interval for estimated mean y
        val halfConfidenceInterval = tcritical * se
        val yHat = value(x)

        return EvalResult(
            yHat,
            yHat - halfConfidenceInterval,
            yHat + halfConfidenceInterval,
            se
        )
    }
}