/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.math3.*

class QQStat(
    private val distribution: Distribution,
    private val distributionParameters: List<Double>
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }

        val xs = data.getNumeric(TransformVar.X)
        val statY = xs.filter { it?.isFinite() == true }.map { it!! }.sorted()

        val t = (1..statY.size).map { (it - 0.5) / statY.size }
        val dist: AbstractRealDistribution = when (distribution) {
            Distribution.NORMAL -> {
                val mean = distributionParameters.getOrNull(0) ?: 0.0
                val standardDeviation = distributionParameters.getOrNull(1) ?: 1.0
                NormalDistribution(mean, standardDeviation)
            }
            Distribution.UNIFORM -> {
                val a = distributionParameters.getOrNull(0) ?: 0.0
                val b = distributionParameters.getOrNull(1) ?: 1.0
                UniformDistribution(a, b)
            }
            Distribution.T -> TDistribution(distributionParameters.getOrNull(0) ?: 1.0)
            Distribution.GAMMA -> {
                val alpha = distributionParameters.getOrNull(0) ?: 1.0
                val beta = distributionParameters.getOrNull(1) ?: 1.0
                GammaDistribution(alpha, beta)
            }
        }
        val statX = t.map { dist.inverseCumulativeProbability(it) }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.Y, statY)
            .build()
    }

    enum class Distribution {
        NORMAL,
        UNIFORM,
        T,
        GAMMA
    }

    companion object {
        val DEF_DISTRIBUTION = Distribution.NORMAL

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}