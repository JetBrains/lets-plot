/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar

class DotplotStat(
    binCount: Int,
    binWidth: Double?,
    private val xPosKind: BinStat.XPosKind,
    private val xPos: Double,
    private val method: Method
) : BaseStat(DEF_MAPPING) {
    private val binOptions = BinStatUtil.BinOptions(binCount, binWidth)

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }
        val statX = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statBinWidth = ArrayList<Double>()

        val rangeX = statCtx.overallXRange()
        if (rangeX != null) { // null means all input values are null
            val binsData = when (method) {
                Method.DOTDENSITY -> BinStatUtil.computeDotdensityStatSeries(rangeX, data.getNumeric(TransformVar.X), binOptions)
                Method.HISTODOT -> BinStatUtil.computeHistogramStatSeries(data, rangeX, data.getNumeric(TransformVar.X), xPosKind, xPos, binOptions)
            }
            statX.addAll(binsData.x)
            statCount.addAll(binsData.count)
            statDensity.addAll(binsData.density)
            statBinWidth.addAll(binsData.binWidth)
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.COUNT, statCount)
            .putNumeric(Stats.DENSITY, statDensity)
            .putNumeric(Stats.BIN_WIDTH, statBinWidth)
            .build()
    }

    enum class Method {
        HISTODOT, DOTDENSITY;

        companion object {

            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Method>()

            fun safeValueOf(v: String): Method {
                return ENUM_INFO.safeValueOf(v) ?:
                throw IllegalArgumentException(
                    "Unsupported method: '$v'\n" +
                    "Use one of: histodot, dotdensity."
                )
            }
        }
    }

    companion object {
        val DEF_METHOD = Method.DOTDENSITY

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.STACKSIZE to Stats.COUNT,
            Aes.BINWIDTH to Stats.BIN_WIDTH
        )
    }
}