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

class PolynomialRegression(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double, private val deg: Int) :
    RegressionEvaluator(xs, ys, confidenceLevel) {

    override val canBeComputed: Boolean
        get() = n > deg

    override val degreesOfFreedom: Double
        get() = n - deg - 1.0

    private val p: PolynomialFunction

    init {
        require(deg >= 2) { "Degree of polynomial must be at least 2" }

        val (xVals, yVals) = averageByX(xs, ys)

        require(n > deg) { "The number of valid data points must be greater than deg" }

        p = calcPolynomial(deg, xVals, yVals)
    }

    override fun prepareData(xs: List<Double?>, ys: List<Double?>): Pair<DoubleArray, DoubleArray> {
        return averageByX(xs, ys)
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

    override fun value(x: Double): Double {
        return p.value(x)
    }

    override fun standardErrorOfEstimate(xVals: DoubleArray, yVals: DoubleArray): Double {
        val sse = xVals.zip(yVals).sumOf { (x, y) -> (y - p.value(x)).pow(2) }
        return sqrt(sse / degreesOfFreedom)
    }
}