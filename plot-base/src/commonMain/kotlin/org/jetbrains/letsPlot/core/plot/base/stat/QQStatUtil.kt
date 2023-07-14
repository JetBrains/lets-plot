/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.stat.math3.*
import kotlin.math.*

object QQStatUtil {
    fun getDistribution(
        distribution: QQStat.Distribution,
        distributionParameters: List<Double>
    ): AbstractRealDistribution {
        return when (distribution) {
            QQStat.Distribution.NORM -> {
                val mean = distributionParameters.getOrNull(0) ?: DEF_NORMAL_MEAN
                val standardDeviation = distributionParameters.getOrNull(1) ?: DEF_NORMAL_STD
                NormalDistribution(mean, standardDeviation)
            }
            QQStat.Distribution.UNIFORM -> {
                val a = distributionParameters.getOrNull(0) ?: DEF_UNIFORM_A
                val b = distributionParameters.getOrNull(1) ?: DEF_UNIFORM_B
                UniformDistribution(a, b)
            }
            QQStat.Distribution.T -> TDistribution(
                distributionParameters.getOrNull(0) ?: DEF_T_DEGREES_OF_FREEDOM
            )
            QQStat.Distribution.GAMMA -> {
                val alpha = distributionParameters.getOrNull(0) ?: DEF_GAMMA_ALPHA
                val beta = distributionParameters.getOrNull(1) ?: DEF_GAMMA_BETA
                GammaDistribution(alpha, beta)
            }
            QQStat.Distribution.EXP -> {
                val lambda = distributionParameters.getOrNull(0) ?: DEF_EXP_LAMBDA
                GammaDistribution(1.0, lambda)
            }
            QQStat.Distribution.CHI2 -> {
                val k = distributionParameters.getOrNull(0) ?: DEF_CHI2_K
                GammaDistribution(k / 2.0, 0.5)
            }
        }
    }

    // Use "R-8" quantile estimation type from here: https://en.wikipedia.org/wiki/Quantile#Estimating_quantiles_from_a_sample
    fun getEstimatedQuantile(
        sortedSeries: List<Double>,
        p: Double
    ): Double {
        require(sortedSeries.any()) { "$sortedSeries should not be empty" }
        require(p in 0.0..1.0) { "$p should be in [0, 1]" }
        val n = sortedSeries.size
        if (n == 1) return sortedSeries.first()
        if (p < 1.0 / n) return sortedSeries[0] + (n + 1.0) / (3.0 * n) * (sortedSeries[1] - sortedSeries[0])
        if (p > (n - 1.0) / n) return sortedSeries[n - 2] + (2.0 * n - 1.0) / (3.0 * n) * (sortedSeries[n - 1] - sortedSeries[n - 2])
        val h = (n + 1.0 / 3.0) * p - 2.0 / 3.0
        val i = floor(h).toInt()
        val j = ceil(h).toInt()
        return sortedSeries[i] + (h - i) * (sortedSeries[j] - sortedSeries[i])
    }

    fun lineByPoints(
        xCoord: Pair<Double, Double>,
        yCoord: Pair<Double, Double>
    ): (Double) -> Double {
        require(xCoord.second != xCoord.first) { "Should be ${xCoord.first} != ${xCoord.second}" }

        val slope = (yCoord.second - yCoord.first) / (xCoord.second - xCoord.first)
        val intercept = yCoord.first - slope * xCoord.first

        return { x -> slope * x + intercept }
    }

    private const val DEF_NORMAL_MEAN = 0.0
    private const val DEF_NORMAL_STD = 1.0
    private const val DEF_UNIFORM_A = 0.0
    private const val DEF_UNIFORM_B = 1.0
    private const val DEF_T_DEGREES_OF_FREEDOM = 1.0
    private const val DEF_GAMMA_ALPHA = 1.0
    private const val DEF_GAMMA_BETA = 1.0
    private const val DEF_EXP_LAMBDA = 1.0
    private const val DEF_CHI2_K = 1.0
}