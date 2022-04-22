/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.stat.math3.AbstractRealDistribution
import jetbrains.datalore.plot.base.stat.math3.NormalDistribution
import jetbrains.datalore.plot.base.stat.math3.TDistribution

class QQStat(
    private val distribution: Distribution
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
            Distribution.NORMAL -> NormalDistribution(0.0, 1.0)
            Distribution.T -> TDistribution(1.0)
        }
        val statX = t.map { dist.inverseCumulativeProbability(it) }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.Y, statY)
            .build()
    }

    enum class Distribution {
        NORMAL,
        T
    }

    companion object {
        val DEF_DISTRIBUTION = Distribution.NORMAL

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}