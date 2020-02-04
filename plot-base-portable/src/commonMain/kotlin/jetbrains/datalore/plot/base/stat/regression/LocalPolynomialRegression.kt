/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.stat.DensityStatUtil
import jetbrains.datalore.plot.base.stat.math3.LoessInterpolator
import jetbrains.datalore.plot.base.stat.math3.PolynomialSplineFunction
import jetbrains.datalore.plot.common.data.SeriesUtil

class LocalPolynomialRegression(
    xs: List<Double?>,
    ys: List<Double?>,
    confidenceLevel: Double,
    private val myBandwidth: Double
)
    : RegressionEvaluator(xs, ys, confidenceLevel) {

    private val myPolynomial: PolynomialSplineFunction
    private val mySamplePolynomials: Array<PolynomialSplineFunction?>
    private val myAlpha: Double = .5 - confidenceLevel / 2.0

    init {
        val mapX: MutableMap<Double, MutableList<Double>> = hashMapOf()

        xs.zip(ys)
            .filter { (x, y) -> SeriesUtil.allFinite(x, y) }
            .forEach { (x, y) -> mapX.getOrPut(x!!) { mutableListOf() }.add(y!!) }

        val distinct = mapX
            .map { (x, ys) -> x to ys.average() }
            .map { (x, y) -> DoubleVector(x, y) }

        myPolynomial = getPoly(distinct)

        mySamplePolynomials = arrayOfNulls(DEF_SAMPLE_NUMBER)
        for (i in 0 until DEF_SAMPLE_NUMBER) {
            mySamplePolynomials[i] =
                getPoly(
                    RegressionUtil.sampling(
                        distinct,
                        distinct.size / 2
                    )
                )
        }
    }

    override fun evalX(x: Double): EvalResult {
        val yHat = myPolynomial.value(x)!!
        val sample = mySamplePolynomials.map { interpolateLinear( it!!, x) }
        val yMin = RegressionUtil.percentile(sample, myAlpha)
        val yMax = RegressionUtil.percentile(sample, 1 - myAlpha)
        val se = DensityStatUtil.stdDev(sample)
        return EvalResult(
            yHat,
            yMin,
            yMax,
            se
        )
    }

    private fun getPoly(points: List<DoubleVector>): PolynomialSplineFunction {
        val listX = ArrayList<Double>()
        val listY = ArrayList<Double>()
        points
            .sortedBy( DoubleVector::x )
            .forEach { listX.add(it.x); listY.add(it.y) }

        return LoessInterpolator(myBandwidth, 4).interpolate(listX.toDoubleArray(), listY.toDoubleArray())
    }

    companion object {
        private const val DEF_SAMPLE_NUMBER = 50

        private fun interpolateLinear(function: PolynomialSplineFunction, x: Double): Double {
            val knots = function.knots

            if (x < knots.first()) {
                return function.polynomials[0]!!.value(x - knots.first())
            } else if (x > knots.last()) {
                return function.polynomials[knots.size - 2]!!.value(x - knots[knots.size - 2])
            }
            return function.value(x)!!
        }
    }

}