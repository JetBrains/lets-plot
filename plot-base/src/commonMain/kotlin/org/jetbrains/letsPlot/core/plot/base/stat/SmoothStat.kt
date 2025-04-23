/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.regression.LinearRegression
import org.jetbrains.letsPlot.core.plot.base.stat.regression.LocalPolynomialRegression
import org.jetbrains.letsPlot.core.plot.base.stat.regression.PolynomialRegression
import org.jetbrains.letsPlot.core.plot.base.util.SamplingUtil
import kotlin.random.Random


/**
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
 * Adds columns:
 *
 * y    - predicted value
 * ymin - lower pointwise confidence interval around the mean
 * ymax - upper pointwise confidence interval around the mean
 * se   - standard error
 */
class SmoothStat constructor(
    private val smootherPointCount: Int,
    private val smoothingMethod: Method,
    private val confidenceLevel: Double,
    private val displayConfidenceInterval: Boolean,
    private val span: Double,
    private val polynomialDegree: Int,
    private val loessCriticalSize: Int,
    private val samplingSeed: Long
) : BaseStat(DEF_MAPPING) {

    override fun hasDefaultMapping(aes: Aes<*>): Boolean {
        return super.hasDefaultMapping(aes) ||
                aes == Aes.YMIN && displayConfidenceInterval ||
                aes == Aes.YMAX && displayConfidenceInterval
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
        const val DEF_EVAL_POINT_COUNT = 80
        val DEF_SMOOTHING_METHOD = Method.LM
        const val DEF_CONFIDENCE_LEVEL = 0.95    // 95 %
        const val DEF_DISPLAY_CONFIDENCE_INTERVAL = true
        const val DEF_SPAN = 0.5
        const val DEF_DEG = 1
        const val DEF_LOESS_CRITICAL_SIZE = 1_000
        const val DEF_SAMPLING_SEED = 37L
    }


    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    private fun needSampling(rowCount: Int): Boolean {
        if (smoothingMethod != Method.LOESS) {
            return false
        }

        if (rowCount <= loessCriticalSize) {
            return false
        }

        return true
    }

    private fun applySampling(data: DataFrame, messageConsumer: (s: String) -> Unit): DataFrame {
        val msg = "LOESS drew a random sample with max_n=$loessCriticalSize, seed=$samplingSeed"
        messageConsumer(msg)

        return SamplingUtil.sampleWithoutReplacement(loessCriticalSize, Random(samplingSeed), data)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        @Suppress("NAME_SHADOWING")
        var data = data

        if (needSampling(data.rowCount())) {
            data = applySampling(data, messageConsumer)
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
            for (i in valuesY.indices) {   // ToDo: what indices to do with smoothing?
                valuesX.add(i.toDouble())
            }
        }

        DoubleSpan.encloseAllQ(valuesX) ?: return withEmptyStatValues()

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

        if (displayConfidenceInterval) {
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
   * */

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
                require(polynomialDegree >= 1) { "Degree of polynomial regression must be at least 1" }
                if (polynomialDegree == 1) {
                    LinearRegression.fit(valuesX, valuesY, confidenceLevel)
                } else {
                    PolynomialRegression.fit(valuesX, valuesY, confidenceLevel, polynomialDegree)
                }
            }

            Method.LOESS -> {
                LocalPolynomialRegression.fit(valuesX, valuesY, confidenceLevel, span)
            }

            else -> throw IllegalArgumentException(
                "Unsupported smoother method: $smoothingMethod (only 'lm' and 'loess' methods are currently available)"
            )
        } ?: return result

        val rangeX = DoubleSpan.encloseAllQ(valuesX) ?: return result

        val startX = rangeX.lowerEnd
        val spanX = rangeX.upperEnd - startX
        val stepX = spanX / (smootherPointCount - 1)

        for (i in 0 until smootherPointCount) {
            val x = startX + i * stepX
            val eval = regression.evalX(x.coerceIn(rangeX.lowerEnd, rangeX.upperEnd))
            statX.add(x)
            statY.add(eval.y)
            statMinY.add(eval.ymin)
            statMaxY.add(eval.ymax)
            statSE.add(eval.se)
        }
        return result
    }
}


