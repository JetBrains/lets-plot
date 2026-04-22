/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable.Source.STAT
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.SmoothStat.Method
import org.jetbrains.letsPlot.core.plot.base.stat.regression.LinearRegression
import org.jetbrains.letsPlot.core.plot.base.stat.regression.LocalPolynomialRegression
import org.jetbrains.letsPlot.core.plot.base.stat.regression.PolynomialRegression
import org.jetbrains.letsPlot.core.plot.base.util.SamplingUtil
import kotlin.random.Random

// TODO: fix duplication SmoothStat
class SmoothStatSummary(
    private val smoothingMethod: Method,
    private val confidenceLevel: Double,
    private val span: Double,
    private val polynomialDegree: Int,
    private val loessCriticalSize: Int,
    private val samplingSeed: Long
) : BaseStat(DEF_MAPPING) {

    private var myVariables: List<DataFrame.Variable>? = null

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
        )
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

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        @Suppress("NAME_SHADOWING")
        var data = data

        if (needSampling(data.rowCount())) {
            data = SamplingUtil.sampleWithoutReplacement(loessCriticalSize, Random(samplingSeed), data)
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

        val fTest = regression.fTest
        val r2ConfInt = regression.r2ConfInt

        val dfb = DataFrame.Builder()
            .put(Stats.X, listOf(0.0))
            .put(Stats.Y, listOf(0.0))
            .put(Stats.R2, listOf(regression.r2))
            .put(Stats.R2_ADJ, listOf(regression.adjustedR2))
            .put(Stats.N, listOf(regression.n))
            .put(Stats.METHOD, listOf(smoothingMethodLabel(smoothingMethod)))
            .put(Stats.AIC, listOf(regression.aic))
            .put(Stats.BIC, listOf(regression.bic))
            .put(Stats.F, listOf(fTest.fValue))
            .put(Stats.DF1, listOf(fTest.df1))
            .put(Stats.DF2, listOf(fTest.df2))
            .put(Stats.P, listOf(fTest.pValue))
            .put(Stats.CI_LEVEL, listOf(r2ConfInt.level))
            .put(Stats.CI_LOW, listOf(r2ConfInt.low))
            .put(Stats.CI_HIGH, listOf(r2ConfInt.high))

        val vars = myVariables ?: initVariables(regression.eq.size)
        regression.eq.forEachIndexed { index, coef ->
            dfb.put(vars[index], listOf(coef)) }

        return dfb.build()
    }

    private fun initVariables(size: Int): List<DataFrame.Variable> {
        require(myVariables == null)

        myVariables = (0 until size).map {
            val varName = "smooth_eq_coef_$it"
            DataFrame.Variable("..$varName..", STAT, varName)
        }

        return myVariables!!
    }

    private fun smoothingMethodLabel(method: Method): String {
        return when (method) {
            Method.LM -> "lm"
            Method.LOESS -> "loess"
            Method.GLM -> "glm"
            Method.GAM -> "gam"
            Method.RLM -> "rlm"
        }
    }
}
