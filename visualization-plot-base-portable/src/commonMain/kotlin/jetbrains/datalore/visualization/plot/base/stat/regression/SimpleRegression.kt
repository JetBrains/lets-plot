package jetbrains.datalore.visualization.plot.base.stat.regression

import jetbrains.datalore.visualization.plot.base.stat.math3.TDistribution
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class SimpleRegression(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double)
    : RegressionEvaluator(xs, ys, confidenceLevel) {

    private val n: Int
    private val meanX: Double
    private val sumXX: Double
    private val beta1: Double
    private val beta0: Double
    private val sy: Double
    private val tcritical: Double

    init {
        val (xVals, yVals) = xs.asSequence().zip(ys.asSequence())
            .filter { (x, y) -> SeriesUtil.allFinite(x, y) }
            .fold(Pair(mutableListOf<Double>(), mutableListOf<Double>())) { points, (x, y) ->
                points.apply {
                    first.add(x!!)
                    second.add(y!!)
                }
            }

        n = xVals.size
        meanX = xVals.sum().div(n)
        sumXX = xVals.sumByDouble { (it - meanX).pow(2) }

        val meanY = yVals.sum().div(n)
        sy = run {
            val sumYY = yVals.sumByDouble { (it - meanY).pow(2) }
            val sumXY = xVals.zip(yVals).sumByDouble { (x, y) -> (x - meanX) * (y - meanY) }
            val sse = max(0.0, sumYY - sumXY * sumXY / sumXX);
            sqrt(sse / (n - 2))
        }

        beta1 = run {
            val variance = xVals.map { (it - meanX).pow(2) }.sum()
            val covariance = xVals.zip(yVals).map { (x, y) -> (x - meanX) * (y - meanY) }.sum()
            covariance / variance
        }

        beta0 = meanY - beta1 * meanX

        tcritical = run {
            val alpha = 1.0 - confidenceLevel
            TDistribution(n - 2.0).inverseCumulativeProbability(1.0 - alpha / 2.0)
        }
    }

    override fun evalX(x: Double): EvalResult {

        // confidence interval for the conditional mean
        // https://www.ma.utexas.edu/users/mks/statmistakes/CIvsPI.html
        // https://onlinecourses.science.psu.edu/stat414/node/297

        // http://www2.stat.duke.edu/~tjl13/s101/slides/unit6lec3H.pdf
        // Stat symbols:
        // http://brownmath.com/swt/symbol.htm


        // standard error (of estimate?)
        val se = run {
            // x deviation squared
            val dxSquare = (x - meanX).pow(2)
            sy * sqrt(1.0 / n + dxSquare / sumXX)
        }

        // half-width of confidence interval for estimated mean y
        val halfConfidenceInterval = tcritical * se

        val yHat = beta1 * x + beta0
        return EvalResult(
            yHat,
            yHat - halfConfidenceInterval,
            yHat + halfConfidenceInterval,
            se
        )
    }
}