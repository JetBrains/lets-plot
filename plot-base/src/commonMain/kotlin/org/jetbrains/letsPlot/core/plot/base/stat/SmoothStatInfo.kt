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
 * Adds columns:
 *
 * r2       -
 * formula  -
 */
// TODO: fix duplication SmoothStat
class SmoothStatInfo constructor(
    private val smootherPointCount: Int,
    private val smoothingMethod: Method,
    private val confidenceLevel: Double,
    private val displayConfidenceInterval: Boolean,
    private val span: Double,
    private val polynomialDegree: Int,
    private val loessCriticalSize: Int,
    private val samplingSeed: Long
) : BaseStat(DEF_MAPPING) {

    enum class Method {
        LM, // linear model
        GLM,
        GAM,
        LOESS,
        RLM
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.LABEL to Stats.R2
        )
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
            data = applySampling(data) {}
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
        } ?: return DataFrame.Builder.emptyFrame()

        return DataFrame.Builder()
            .put(Stats.R2, listOf(regression.r2))
            .put(Stats.EQ, listOf(regression.eq))
            .build()
    }
}


