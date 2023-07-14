/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.math3.TDistribution
import kotlin.math.pow
import kotlin.math.sqrt

abstract class RegressionEvaluator protected constructor(
    private val n: Int,
    private val meanX: Double,
    private val sumXX: Double,
    private val model: (Double) -> Double,
    private val standardErrorOfEstimate: Double,
    private val tCritical: Double
) {
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
                TDistribution(degreesOfFreedom).inverseCumulativeProbability(1.0 - alpha / 2.0)
            } else {
                Double.NaN
            }
        }
    }
}
