package jetbrains.datalore.visualization.plot.base.stat.regression

import jetbrains.datalore.visualization.plot.common.data.SeriesUtil
import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.stat.descriptive.moment.Mean
import org.apache.commons.math3.stat.regression.SimpleRegression

class LinearRegression
/**
 * @param xs              - observations
 * @param ys              - observations
 * @param confidenceLevel - 0.01..0.99, def: 0.95 (= 95% confidence)
 */
(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double) : RegressionEvaluator(xs, ys, confidenceLevel) {
    private val myRegression: SimpleRegression = SimpleRegression()

    private val myXBar: Double
    private val mySy: Double
    private val myTCritical: Double
    private val myXSumSquares: Double

    init {
        val meanX = Mean()

        val x_ = xs.iterator()
        val y_ = ys.iterator()
        while (x_.hasNext()) {
            val x = x_.next()
            val y = y_.next()
            if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(y)) {
                myRegression.addData(x!!, y!!)
                meanX.increment(x)
            }
        }

        myXBar = meanX.result

        val N = myRegression.n.toInt()

        // standard deviation of the residuals (aka. residual standard error)
        val SSE = myRegression.sumSquaredErrors
        mySy = Math.sqrt(SSE / (N - 2))

        // critical value
        val distribution = TDistribution((N - 2).toDouble())
        val alpha = 1 - confidenceLevel
        myTCritical = distribution.inverseCumulativeProbability(1.0 - alpha / 2.0)

        // sum of squared deviations from xbar
        myXSumSquares = myRegression.xSumSquares
    }

    override fun evalX(x: Double): EvalResult {
        val N = myRegression.n.toInt()

        // confidence interval for the conditional mean
        // https://www.ma.utexas.edu/users/mks/statmistakes/CIvsPI.html
        // https://onlinecourses.science.psu.edu/stat414/node/297

        // http://www2.stat.duke.edu/~tjl13/s101/slides/unit6lec3H.pdf
        // Stat symbols:
        // http://brownmath.com/swt/symbol.htm


        // x deviation squared
        val xd = x - myXBar
        val dxSquare = xd * xd

        // standard error (of estimate?)
        val SE = mySy * Math.sqrt(1.0 / N + dxSquare / myXSumSquares)
        // half-width of confidence interval for estimated mean y
        val halfConfidenceInterval = myTCritical * SE

        val yHat = myRegression.predict(x)
        return EvalResult(
                yHat,
                yHat - halfConfidenceInterval,
                yHat + halfConfidenceInterval,
                SE
        )
    }
}
