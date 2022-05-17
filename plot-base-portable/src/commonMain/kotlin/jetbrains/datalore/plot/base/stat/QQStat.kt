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
import jetbrains.datalore.plot.common.data.SeriesUtil

class QQStat(
    private val distribution: Distribution,
    private val distributionParameters: List<Double>
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val ys = data.getNumeric(TransformVar.Y)
        val statData = if (data.has(TransformVar.X)) {
            val xs = data.getNumeric(TransformVar.X)
            val (statX, statY) = (xs zip ys).filter { (x, y) ->
                SeriesUtil.allFinite(x, y)
            }.unzip()
            mutableMapOf(
                Stats.X to statX.map { it!! }.sorted(),
                Stats.Y to statY.map { it!! }.sorted()
            )
        } else {
            buildStat(ys)
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statData[Stats.X]!!)
            .putNumeric(Stats.Y, statData[Stats.Y]!!)
            .build()
    }

    private fun buildStat(
        ys: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val statY = ys.filter { it?.isFinite() == true }.map { it!! }.sorted()

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
            Distribution.EXP -> {
                val lambda = distributionParameters.getOrNull(0) ?: 1.0
                GammaDistribution(1.0, lambda)
            }
            Distribution.CHI_SQUARED -> {
                val k = distributionParameters.getOrNull(0) ?: 1.0
                GammaDistribution(k / 2.0, 0.5)
            }
        }
        val statX = t.map { dist.inverseCumulativeProbability(it) }

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY
        )
    }

    enum class Distribution {
        NORMAL,
        UNIFORM,
        T,
        GAMMA,
        EXP,
        CHI_SQUARED
    }

    companion object {
        val DEF_DISTRIBUTION = Distribution.NORMAL

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}