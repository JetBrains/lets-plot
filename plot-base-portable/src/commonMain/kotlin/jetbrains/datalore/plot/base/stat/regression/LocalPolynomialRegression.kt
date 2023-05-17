/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.plot.base.stat.math3.LoessInterpolator
import jetbrains.datalore.plot.base.stat.math3.PolynomialSplineFunction
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class LocalPolynomialRegression(
    xs: List<Double?>,
    ys: List<Double?>,
    confidenceLevel: Double,
    private val bandwidth: Double
) : RegressionEvaluator(xs, ys, confidenceLevel) {

    private var canCompute: Boolean? = null

    override val canBeComputed: Boolean
        get() {
            if (canCompute == null) {
                val degreesOfFreedom = n - 2.0
                // See: LoessInterpolator.kt:168
                val bandwidthInPoints = (bandwidth * n).toInt()
                val bandwidthInPointsOk = bandwidthInPoints >= 2
                canCompute = (n >= 3 && degreesOfFreedom > 0 && bandwidthInPointsOk)
            }

            return canCompute!!
        }

    override val degreesOfFreedom: Double
        get() = n - 2.0

    private lateinit var polynomial: PolynomialSplineFunction

    init {
        val (xVals, yVals) = averageByX(xs, ys)

        if (canBeComputed) {
            polynomial = getPoly(xVals, yVals)
        }
    }

    override fun prepareData(xs: List<Double?>, ys: List<Double?>): Pair<DoubleArray, DoubleArray> {
        return averageByX(xs, ys)
    }

    override fun value(x: Double): Double {
        return polynomial.value(x)!!
    }

    override fun standardErrorOfEstimate(xVals: DoubleArray, yVals: DoubleArray): Double {
        val meanY = yVals.average()
        val sumYY = yVals.sumOf { (it - meanY).pow(2) }
        val sumXY = xVals.zip(yVals).sumOf { (x, y) -> (x - meanX) * (y - meanY) }
        val sse = max(0.0, sumYY - sumXY * sumXY / sumXX)
        return sqrt(sse / degreesOfFreedom)
    }

    private fun getPoly(xVals: DoubleArray, yVals: DoubleArray): PolynomialSplineFunction {
        return LoessInterpolator(bandwidth, 4).interpolate(xVals, yVals)
    }
}