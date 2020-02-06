/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.stat.math3.LoessInterpolator
import jetbrains.datalore.plot.base.stat.math3.PolynomialSplineFunction
import jetbrains.datalore.plot.base.stat.math3.TDistribution
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class LocalPolynomialRegression(
    xs: List<Double?>,
    ys: List<Double?>,
    confidenceLevel: Double,
    private val myBandwidth: Double
) : RegressionEvaluator(xs, ys, confidenceLevel) {

    private val n: Int
    private val meanX: Double
    private val sumXX: Double
    private val sy: Double
    private val tcritical: Double
    private val myPolynomial: PolynomialSplineFunction

    init {
        val mapX: MutableMap<Double, MutableList<Double>> = hashMapOf()

        xs.zip(ys)
            .filter { (x, y) -> SeriesUtil.allFinite(x, y) }
            .forEach { (x, y) -> mapX.getOrPut(x!!) { mutableListOf() }.add(y!!) }

        val distinct = mapX
            .map { (x, ys) -> x to ys.average() }
            .map { (x, y) -> DoubleVector(x, y) }

        n = distinct.size
        meanX = distinct.map { it.x }.sum().div(n)
        sumXX = distinct.map { (it.x - meanX).pow(2) }.sum()
        val meanY = distinct.map { it.y }.sum().div(n)

        sy = run {
            val sumYY = distinct.map { (it.y - meanY).pow(2) }.sum()
            val sumXY = distinct.sumByDouble { (it.x - meanX) * (it.y - meanY) }
            val sse = max(0.0, sumYY - sumXY * sumXY / sumXX);
            sqrt(sse / (n - 2))
        }

        myPolynomial = getPoly(distinct)

        tcritical = run {
            val alpha = 1.0 - confidenceLevel
            TDistribution(n - 2.0).inverseCumulativeProbability(1.0 - alpha / 2.0)
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

        val yHat = myPolynomial.value(x)!!

        return EvalResult(
            yHat,
            yHat - halfConfidenceInterval,
            yHat + halfConfidenceInterval,
            se
        )
    }

    private fun getPoly(points: List<DoubleVector>): PolynomialSplineFunction {
        val listX = ArrayList<Double>()
        val listY = ArrayList<Double>()
        points
            .sortedBy(DoubleVector::x)
            .forEach { listX.add(it.x); listY.add(it.y) }

        return LoessInterpolator(myBandwidth, 4).interpolate(listX.toDoubleArray(), listY.toDoubleArray())
    }
}