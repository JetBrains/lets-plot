/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.enums.EnumInfoFactory
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar

class QQStat(
    private val distribution: Distribution,
    private val distributionParameters: List<Double>
) : BaseStat(DEF_MAPPING) {

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val statData = buildStat(data.getNumeric(TransformVar.Y))

        return DataFrame.Builder()
            .putNumeric(Stats.X, statData.getValue(Stats.X))
            .putNumeric(Stats.Y, statData.getValue(Stats.Y))
            .build()
    }

    private fun buildStat(
        ys: List<Double?>
    ): MutableMap<DataFrame.Variable, List<Double>> {
        val statY = ys.filter { it?.isFinite() == true }.map { it!! }.sorted()
        val t = (1..statY.size).map { (it - 0.5) / statY.size }
        val dist = QQStatUtil.getDistribution(distribution, distributionParameters)
        val statX = t.map { dist.inverseCumulativeProbability(it) }

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY
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

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y
        )
    }
}