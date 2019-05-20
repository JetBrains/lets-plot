package jetbrains.datalore.visualization.plot.gog.server.core.data.stat.regression

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.data.stat.DensityStatUtil
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction

class LoessRegression(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double) : RegressionEvaluator(xs, ys, confidenceLevel) {

    private val myPolynomial: PolynomialSplineFunction
    private val mySamplePolynomials: Array<PolynomialSplineFunction?>
    private val myAlpha: Double = .5 - confidenceLevel / 2.0

    init {
        val points = ArrayList<DoubleVector>()
        for (i in xs.indices) {
            points.add(DoubleVector(xs[i]!!, ys[i]!!))
        }

        myPolynomial = getPoly(points)

        mySamplePolynomials = arrayOfNulls(DEF_SAMPLE_NUMBER)
        for (i in 0 until DEF_SAMPLE_NUMBER) {
            mySamplePolynomials[i] = getPoly(RegressionUtil.sampling(points, points.size / 4))
        }
    }

    override fun evalX(x: Double): EvalResult {
        val yHat = myPolynomial.value(x)
        val sample = ArrayList<Double>()
        for (poly in mySamplePolynomials) {
            sample.add(interpolateLinear(poly!!, x))
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
        private const val DEF_SAMPLE_NUMBER = 50

        private fun getPoly(points: ArrayList<DoubleVector>): PolynomialSplineFunction {
            points.sortWith(Comparator { o1: DoubleVector, o2: DoubleVector -> o1.x.compareTo(o2.x) })

            val listX = DoubleArray(points.size)
            val listY = DoubleArray(points.size)
            for (i in points.indices) {
                listX[i] = points[i].x
                listY[i] = points[i].y
            }

            return LoessInterpolator().interpolate(listX, listY)
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
