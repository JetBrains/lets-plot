package jetbrains.datalore.visualization.plot.gog.server.core.data.stat

import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.StatContext
import jetbrains.datalore.visualization.plot.gog.core.data.TransformVar
import jetbrains.datalore.visualization.plot.gog.core.data.stat.SmoothStatShell
import jetbrains.datalore.visualization.plot.gog.core.data.stat.Stats
import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.regression.LinearRegression
import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.regression.LoessRegression
import jetbrains.datalore.visualization.plot.gog.server.core.data.stat.regression.RegressionEvaluator
import java.util.*

class SmoothStat internal constructor() : SmoothStatShell() {

    override fun apply(data: DataFrame, statCtx: StatContext): DataFrame {
        if (!data.has(TransformVar.Y)) {
            return withEmptyStatValues()
        }
        val valuesY = data.getNumeric(TransformVar.Y)
        if (valuesY.size < 3) {  // at least 3 data points required
            return withEmptyStatValues()
        }

        val valuesX: List<Double>
        if (data.has(TransformVar.X)) {
            valuesX = data.getNumeric(TransformVar.X)
        } else {
            valuesX = ArrayList()
            for (i in valuesY.indices) {
                valuesX.add(i.toDouble())
            }
        }

        SeriesUtil.range(valuesX) ?: return withEmptyStatValues()

        // do stat for each group separately

        val statX: List<Double>
        val statY: List<Double>
        val statMinY: List<Double>
        val statMaxY: List<Double>
        val statSE: List<Double>

        val statValues = applySmoothing(valuesX, valuesY)

        statX = statValues[Stats.X]!!
        statY = statValues[Stats.Y]!!
        statMinY = statValues[Stats.Y_MIN]!!
        statMaxY = statValues[Stats.Y_MAX]!!
        statSE = statValues[Stats.SE]!!

        val statData = DataFrame.Builder()
                .putNumeric(Stats.X, statX)
                .putNumeric(Stats.Y, statY)

        if (isDisplayConfidenceInterval) {
            statData.putNumeric(Stats.Y_MIN, statMinY)
                    .putNumeric(Stats.Y_MAX, statMaxY)
                    .putNumeric(Stats.SE, statSE)
        }

        return statData.build()
    }

    /* About five methods
   * Linear Regression: DONE
   * Loess: DONE, SE used bootstrap method, but too many strikes. Refer to http://www.netlib.org/a/cloess.ps Page 45
   * Generalized Linear Model: https://spark.apache.org/docs/latest/ml-classification-regression.html#generalized-linear-regression
   * Robust Linear Model: Unfortunately no Java Library
   * Generalized Additive Model: Unknown
   * */

    private fun applySmoothing(valuesX: List<Double>, valuesY: List<Double>): Map<DataFrame.Variable, List<Double>> {
        val regression: RegressionEvaluator
        when (smoothingMethod) {
            Method.LM -> regression = LinearRegression(valuesX, valuesY, confidenceLevel)
            Method.LOESS -> regression = LoessRegression(valuesX, valuesY, confidenceLevel)
            else -> throw IllegalArgumentException(
                    "Unsupported smoother method: $smoothingMethod (only 'lm' and 'loess' methods are currently available)"
            )
        }
        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statMinY = ArrayList<Double>()
        val statMaxY = ArrayList<Double>()
        val statSE = ArrayList<Double>()

        val result = HashMap<DataFrame.Variable, List<Double>>()
        result[Stats.X] = statX
        result[Stats.Y] = statY
        result[Stats.Y_MIN] = statMinY
        result[Stats.Y_MAX] = statMaxY
        result[Stats.SE] = statSE

        val rangeX = SeriesUtil.range(valuesX) ?: return result

        val startX = rangeX.lowerEndpoint()
        val spanX = rangeX.upperEndpoint() - startX
        val stepX = spanX / (smootherPointCount - 1)

        for (i in 0 until smootherPointCount) {
            val x = startX + i * stepX
            val eval = regression.evalX(x)
            statX.add(x)
            statY.add(eval.y)
            statMinY.add(eval.ymin)
            statMaxY.add(eval.ymax)
            statSE.add(eval.se)
        }
        return result
    }
}
