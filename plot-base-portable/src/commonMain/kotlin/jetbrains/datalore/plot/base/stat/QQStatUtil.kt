/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.stat.math3.*

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