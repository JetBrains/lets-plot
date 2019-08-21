package jetbrains.datalore.visualization.plot.base.stat.regression

import jetbrains.datalore.visualization.plot.base.stat.regression.math3.TDistribution
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

class SimpleRegression(
    xs: List<Double?>,
    ys: List<Double?>,
    confidenceLevel: Double
) : RegressionEvaluator(xs, ys, confidenceLevel) {

    private val n: Int
    private val meanX: Double
    private val sumXX: Double
    private val k: Double
    private val b: Double
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
        val meanY = yVals.sum().div(n)
        sumXX = xVals.sumByDouble { (it - meanX).pow(2) }
        val sumYY = yVals.sumByDouble { (it - meanY).pow(2) }
        val sumXY = xVals.zip(yVals).sumByDouble { (x, y) -> (x - meanX) * (y - meanY) }
        val sse = max(0.0, sumYY - sumXY * sumXY / sumXX);
        sy = sqrt(sse / (n - 2))

        val variance = xVals.map { (it - meanX).pow(2) }.sum()
        val covariance = xVals.zip(yVals).map { (x, y) -> (x - meanX) * (y - meanY) }.sum()
        k = covariance / variance
        b = meanY - k * meanX

        val alpha = 1 - confidenceLevel
        tcritical = TDistribution(n - 2.0).inverseCumulativeProbability(1.0 - alpha / 2.0)
    }


    override fun evalX(x: Double): EvalResult {

        // confidence interval for the conditional mean
        // https://www.ma.utexas.edu/users/mks/statmistakes/CIvsPI.html
        // https://onlinecourses.science.psu.edu/stat414/node/297

        // http://www2.stat.duke.edu/~tjl13/s101/slides/unit6lec3H.pdf
        // Stat symbols:
        // http://brownmath.com/swt/symbol.htm


        // x deviation squared
        val xd = x - meanX
        val dxSquare = xd * xd

        // standard error (of estimate?)
        val se = sy * sqrt(1.0 / n + dxSquare / sumXX)
        // half-width of confidence interval for estimated mean y
        val halfConfidenceInterval = tcritical * se

        val yHat = k * x + b
        return EvalResult(
            yHat,
            yHat - halfConfidenceInterval,
            yHat + halfConfidenceInterval,
            se
        )
    }
}