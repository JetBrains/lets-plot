/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class QQStat(
    private val distribution: Distribution,
    private val distributionParameters: List<Double>
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        return listOf(org.jetbrains.letsPlot.core.plot.base.Aes.SAMPLE)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, org.jetbrains.letsPlot.core.plot.base.Aes.SAMPLE)) {
            return withEmptyStatValues()
        }

        val statData = buildStat(data.getNumeric(TransformVar.SAMPLE))

        return DataFrame.Builder()
            .putNumeric(Stats.THEORETICAL, statData.getValue(Stats.THEORETICAL))
            .putNumeric(Stats.SAMPLE, statData.getValue(Stats.SAMPLE))
            .build()
    }

    private fun buildStat(
        sampleSeries: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val statSample = sampleSeries.filter { it?.isFinite() ?: false }.map { it!! }.sorted()
        val t = (1..statSample.size).map { (it - 0.5) / statSample.size }
        val dist = QQStatUtil.getDistribution(distribution, distributionParameters)
        val statTheoretical = t.map { dist.inverseCumulativeProbability(it) }

        return mutableMapOf(
            Stats.THEORETICAL to statTheoretical,
            Stats.SAMPLE to statSample
        )
    }

    enum class Distribution {
        NORM, UNIFORM, T, GAMMA, EXP, CHI2;

        companion object {

            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Distribution>()

            fun safeValueOf(v: String): Distribution {
                return ENUM_INFO.safeValueOf(v) ?:
                throw IllegalArgumentException(
                    "Unsupported distribution: '$v'\n" +
                    "Use one of: norm, uniform, t, gamma, exp, chi2."
                )
            }
        }
    }

    companion object {
        val DEF_DISTRIBUTION = Distribution.NORM
        val DEF_DISTRIBUTION_PARAMETERS = emptyList<Double>()

        private val DEF_MAPPING: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DataFrame.Variable> = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X to Stats.THEORETICAL,
            org.jetbrains.letsPlot.core.plot.base.Aes.Y to Stats.SAMPLE
        )
    }
}