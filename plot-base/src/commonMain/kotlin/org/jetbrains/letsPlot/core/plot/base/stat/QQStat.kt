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

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.SAMPLE)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.SAMPLE)) {
            return withEmptyStatValues()
        }

        val sampleSeries = data.getNumeric(TransformVar.SAMPLE)
        val (indices, statSample) = sampleSeries
            .withIndex()
            .filter { it.value?.isFinite() == true }
            .sortedBy { it.value }
            .map { it.index to it.value!! }
            .unzip()
        val t = (1..statSample.size).map { (it - 0.5) / statSample.size }
        val quantileFunction = QQStatUtil.getQuantileFunction(distribution, distributionParameters)
        val statTheoretical = t.map(quantileFunction)

        return DataFrame.Builder()
            .putNumeric(Stats.THEORETICAL, statTheoretical)
            .putNumeric(Stats.SAMPLE, statSample)
            .put(Stats.INDEX, indices)
            .build()
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

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.THEORETICAL,
            Aes.Y to Stats.SAMPLE
        )
    }
}