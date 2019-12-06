/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.stat.DensityStatUtil
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction
import kotlin.math.pow
import kotlin.random.Random
import kotlin.test.Test

internal class LoessRegressionTest {

    @Test
    fun compareLoess() {

        val numPoints = 1000
        val numIntervals = numPoints - 1
        val confidenceLevel = 0.95

        var from = 0.000_000_001
        var to = 10_000.0

        val yRange = from.rangeTo(to)
        val (xs, ys) = RegressionTestUtil.data(numPoints, yRange)

        val rndOur = Random(0)
        val rndMath3 = Random(0)
        val genOur = { rndOur.nextDouble() }
        val genMath3 = { rndMath3.nextDouble() }
        val math3LoessRegression = LoessRegression(xs, ys, confidenceLevel, genMath3)
        val loessRegression = LocalPolynomialRegression(xs, ys, confidenceLevel, genOur)

        var i = 0.0
        while (i < numIntervals) {
            RegressionTestUtil.assertRegression(
                math3LoessRegression,
                loessRegression,
                i,
                epsilon = 0.000_000_000_1
            )
            i += 0.1
        }
    }


    class LoessRegression(
        xs: List<Double?>,
        ys: List<Double?>,
        confidenceLevel: Double,
        private val rnd: () -> Double = { Random.nextDouble() }
    ) : RegressionEvaluator(xs, ys, confidenceLevel) {


        private val myPolynomial: PolynomialSplineFunction
        private val mySamplePolynomials: Array<PolynomialSplineFunction?>
        private val myAlpha: Double = .5 - confidenceLevel / 2.0

        init {
            val mapX: MutableMap<Double, MutableList<Double>> = hashMapOf()
            xs.zip(ys).forEach { (x, y) -> mapX.getOrPut(x!!) { mutableListOf() }.add(y!!) }

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
                            distinct.size / 2,
                            rnd
                        )
                    )
            }
        }

        override fun evalX(x: Double): EvalResult {
            val yHat = myPolynomial.value(x)
            val sample = ArrayList<Double>()
            for (poly in mySamplePolynomials) {
                sample.add(
                    interpolateLinear(
                        poly!!,
                        x
                    )
                )
            }
            val yMin = RegressionUtil.percentile(sample, myAlpha)
            val yMax = RegressionUtil.percentile(sample, 1 - myAlpha)
            val SE = DensityStatUtil.stdDev(sample)
            return EvalResult(
                yHat,
                yMin,
                yMax,
                SE
            )
        }

        companion object {
            private const val DEF_SAMPLE_NUMBER = 150

            private fun getPoly(points: List<DoubleVector>): PolynomialSplineFunction {
                val listX = ArrayList<Double>()
                val listY = ArrayList<Double>()
                points
                    .sortedWith(Comparator { o1: DoubleVector, o2: DoubleVector -> o1.x.compareTo(other = o2.x) })
                    .forEach { listX.add(it.x); listY.add(it.y) }

                return LoessInterpolator(0.5, 4).interpolate(listX.toDoubleArray(), listY.toDoubleArray())
            }

            private fun interpolateLinear(function: PolynomialSplineFunction, x: Double): Double {
                val knots = function.knots
                val firstKnot = knots[0]
                val lastKnot = knots[knots.size - 1]

                if (x < firstKnot) {
                    return function.polynomials[0].value(x - knots[0])
                } else if (x > lastKnot) {
                    return function.polynomials[knots.size - 2].value(x - knots[knots.size - 2])
                }
                return function.value(x)
            }
        }
    }

}