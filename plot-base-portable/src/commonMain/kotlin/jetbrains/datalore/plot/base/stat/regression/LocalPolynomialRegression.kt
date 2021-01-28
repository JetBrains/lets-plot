/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.LoessInterpolator
import jetbrains.datalore.plot.base.stat.math3.PolynomialSplineFunction
import jetbrains.datalore.plot.base.stat.math3.TDistribution
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class LocalPolynomialRegression(
    xs: List<Double?>,
    ys: List<Double?>,
    confidenceLevel: Double,
    private val bandwidth: Double
) : RegressionEvaluator(xs, ys, confidenceLevel) {

    val canCompute: Boolean

    private val n: Int
    private val meanX: Double
    private val sumXX: Double
    private val sy: Double
    private val tcritical: Double
    private lateinit var polynomial: PolynomialSplineFunction

    init {
        val (xVals, yVals) = averageByX(xs, ys)

        n = xVals.size
        val degreesOfFreedom = n - 2.0
        canCompute = (xVals.size >= 3 && degreesOfFreedom > 0)

        meanX = xVals.average()
        sumXX = xVals.sumByDouble { (it - meanX).pow(2) }

        val meanY = yVals.average()
        val sumYY = yVals.sumByDouble { (it - meanY).pow(2) }
        val sumXY = xVals.zip(yVals).sumByDouble { (x, y) -> (x - meanX) * (y - meanY) }

        sy = run {
            val sse = max(0.0, sumYY - sumXY * sumXY / sumXX)
            sqrt(sse / (n - 2))
        }


        if (canCompute) {
            polynomial = getPoly(xVals, yVals)
        }

        tcritical = if (canCompute) {
            val alpha = 1.0 - confidenceLevel
            TDistribution(degreesOfFreedom).inverseCumulativeProbability(1.0 - alpha / 2.0)
        } else {
            Double.NaN
        }
    }

    override fun evalX(x: Double): EvalResult {

        val se = run {
            // x deviation squared
            val dxSquare = (x - meanX).pow(2)
            sy * sqrt(1.0 / n + dxSquare / sumXX)
        }

        // half-width of confidence interval for estimated mean y
        val halfConfidenceInterval = tcritical * se

        val yHat = polynomial.value(x)!!

        return EvalResult(
            yHat,
            yHat - halfConfidenceInterval,
            yHat + halfConfidenceInterval,
            se
        )
    }

    private fun getPoly(xVals: DoubleArray, yVals: DoubleArray): PolynomialSplineFunction {
        return LoessInterpolator(bandwidth, 4).interpolate(xVals, yVals)
    }
}