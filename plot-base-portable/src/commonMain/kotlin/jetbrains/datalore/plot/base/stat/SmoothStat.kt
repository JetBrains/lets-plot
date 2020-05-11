/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.regression.LinearRegression
import jetbrains.datalore.plot.base.stat.regression.LocalPolynomialRegression
import jetbrains.datalore.plot.base.stat.regression.PolynomialRegression
import jetbrains.datalore.plot.common.data.SeriesUtil

/**
 * See doc for stat_smooth / geom_smooth
 *
 *
 *
 *
 * Defaults:
 *
 *
 * geom = "smooth"
 * position = "identity"
 *
 *
 * Other params:
 *
 *
 * method - smoothing method: lm, glm, gam, loess, rlm
 * (For datasets with n < 1000 default is loess. For datasets with 1000 or more observations defaults to gam)
 * formula - formula to use in smoothing function
 * ( eg. y ~ x, y ~ poly(x, 2), y ~ log(x))
 * se (TRUE ) - display confidence interval around smooth?
 * n (80) - number of points to evaluate smoother at
 *
 *
 * span (0.75) - controls the amount of smoothing for the default loess smoother.
 * fullrange (FALSE) - should the fit span the full range of the plot, or just the data
 * level (0.95) - level of confidence interval to use
 * method.args - ist of additional arguments passed on to the modelling function defined by method
 *
 *
 *
 *
 *
 *
 * Adds columns:
 *
 *
 * y    - predicted value
 * ymin - lower pointwise confidence interval around the mean
 * ymax - upper pointwise confidence interval around the mean
 * se   - standard error
 */
class SmoothStat internal constructor() : BaseStat(DEF_MAPPING) {
    var smootherPointCount = DEF_EVAL_POINT_COUNT

    // checkArgument(smoothingMethod == Method.LM or Method.LOESS, "Linear and loess models are supported only, use: method='lm' or 'loess'");
    var smoothingMethod = DEF_SMOOTHING_METHOD
    var confidenceLevel = DEF_CONFIDENCE_LEVEL
    var isDisplayConfidenceInterval = DEF_DISPLAY_CONFIDENCE_INTERVAL
    var span = DEF_SPAN
    var deg: Int = DEF_DEG // default degree for polynomial regression

    override fun hasDefaultMapping(aes: Aes<*>): Boolean {
        return super.hasDefaultMapping(aes) ||
                aes == Aes.YMIN && isDisplayConfidenceInterval ||
                aes == Aes.YMAX && isDisplayConfidenceInterval
    }

    override fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable {
        if (aes == Aes.YMIN) {
            return Stats.Y_MIN
        }
        return if (aes == Aes.YMAX) {
            Stats.Y_MAX
        } else super.getDefaultMapping(aes)
    }

    enum class Method {
        LM, // linear model
        GLM,
        GAM,
        LOESS,
        RLM
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )  // also conditional Y_MIN / Y_MAX
        private const val DEF_EVAL_POINT_COUNT = 80
        private val DEF_SMOOTHING_METHOD = Method.LM
        private const val DEF_CONFIDENCE_LEVEL = 0.95    // 95 %
        private const val DEF_DISPLAY_CONFIDENCE_INTERVAL = true
        private const val DEF_SPAN = 0.5
        private const val DEF_DEG = 1
    }


    override fun consumes(): List<Aes<*>> {
        return listOf<Aes<*>>(Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val valuesY = data.getNumeric(TransformVar.Y)
        if (valuesY.size < 3) {  // at least 3 data points required
            return withEmptyStatValues()
        }

        val valuesX: List<Double?>
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
     * Loess: DONE, SE used bootstrap method, but too many strikes. Refer to www.netlib.org/a/cloess.ps Page 45
     * Generalized Linear Model: https://spark.apache.org/docs/latest/ml-classification-regression.html#generalized-linear-regression
     * Robust Linear Model: Unfortunately no Java Library
     * Generalized Additive Model: Unknown
     */

    private fun applySmoothing(valuesX: List<Double?>, valuesY: List<Double?>): Map<DataFrame.Variable, List<Double>> {
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

        val regression = when (smoothingMethod) {
            Method.LM -> {
                Preconditions.checkArgument(
                    deg >= 1,
                    "Degree of polynomial regression must be at least 1"
                )
                if (deg == 1) {
                    LinearRegression(valuesX, valuesY, confidenceLevel)
                } else {
                    if (PolynomialRegression.canBeComputed(valuesX, valuesY, deg)) {
                        PolynomialRegression(valuesX, valuesY, confidenceLevel, deg)
                    } else {
                        return result   // empty stat data
                    }
                }
            }
            Method.LOESS -> LocalPolynomialRegression(valuesX, valuesY, confidenceLevel, span)
            else -> throw IllegalArgumentException(
                "Unsupported smoother method: $smoothingMethod (only 'lm' and 'loess' methods are currently available)"
            )
        }

        val rangeX = SeriesUtil.range(valuesX) ?: return result

        val startX = rangeX.lowerEndpoint()
        val spanX = rangeX.upperEndpoint() - startX
        val stepX = spanX / (smootherPointCount - 1)

        for (i in 0 until smootherPointCount) {
            val x = startX + i * stepX
            val eval = regression.evalX(x.coerceIn(rangeX.lowerEndpoint(), rangeX.upperEndpoint()))
            statX.add(x)
            statY.add(eval.y)
            statMinY.add(eval.ymin)
            statMaxY.add(eval.ymax)
            statSE.add(eval.se)
        }
        return result
    }
}


