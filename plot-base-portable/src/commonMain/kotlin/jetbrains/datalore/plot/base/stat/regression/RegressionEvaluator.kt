/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.TDistribution
import kotlin.math.pow
import kotlin.math.sqrt

abstract class RegressionEvaluator protected constructor(
    xs: List<Double?>,
    ys: List<Double?>,
    private val confidenceLevel: Double
) {
    abstract val canBeComputed: Boolean
    abstract val degreesOfFreedom: Double

    protected val n: Int
    protected val meanX: Double
    protected val sumXX: Double

    private val standardErrorOfEstimate: Double
    private val tCritical: Double
        get() = if (canBeComputed && degreesOfFreedom > 0) {
            val alpha = 1.0 - confidenceLevel
            TDistribution(degreesOfFreedom).inverseCumulativeProbability(1.0 - alpha / 2.0)
        } else {
            Double.NaN
        }

    init {
        require(confidenceLevel in 0.01..0.99) { "Confidence level is out of range [0.01-0.99]. CL:$confidenceLevel" }
        require(xs.size == ys.size) { "X/Y must have same size. X:" + xs.size + " Y:" + ys.size }

        val data by lazy { prepareData(xs, ys) }
        val (xVals, yVals) = data
        n = xVals.size
        meanX = xVals.average()
        sumXX = xVals.sumOf { (it - meanX).pow(2) }

        val sy by lazy { standardErrorOfEstimate(xVals, yVals) }
        standardErrorOfEstimate = sy
    }

    protected abstract fun prepareData(xs: List<Double?>, ys: List<Double?>): Pair<DoubleArray, DoubleArray>

    fun evalX(x: Double): EvalResult {
        require(canBeComputed) { "Regression cannot be computed" }

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

    protected abstract fun value(x: Double): Double

    protected abstract fun standardErrorOfEstimate(xVals: DoubleArray, yVals: DoubleArray): Double
}
