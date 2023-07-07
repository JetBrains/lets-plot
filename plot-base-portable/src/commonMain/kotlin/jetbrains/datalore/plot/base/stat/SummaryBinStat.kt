/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar

class SummaryBinStat(
    binCount: Int,
    binWidth: Double?,
    private val xPosKind: BinStat.XPosKind,
    private val xPos: Double,
    private val yAggFunction: (List<Double>) -> Double,
    private val yMinAggFunction: (List<Double>) -> Double,
    private val yMaxAggFunction: (List<Double>) -> Double,
    private val lowerQuantile: Double,
    private val middleQuantile: Double,
    private val upperQuantile: Double
) : BaseStat(DEF_MAPPING) {
    private val binOptions = BinStatUtil.BinOptions(binCount, binWidth)

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.Y)) {
            return withEmptyStatValues()
        }

        val ys = data.getNumeric(TransformVar.Y)
        val xs = if (data.has(TransformVar.X)) {
            data.getNumeric(TransformVar.X)
        } else {
            List(ys.size) { 0.0 }
        }

        val paramAggFunctions = mapOf(
            Stats.Y to yAggFunction,
            Stats.Y_MIN to yMinAggFunction,
            Stats.Y_MAX to yMaxAggFunction,
        )
        val aesAggFunctions = statCtx.mappedStatVariables()
            .associateWith { AggregateFunctions.byStatVar(it, lowerQuantile, middleQuantile, upperQuantile) }
        val rangeX = statCtx.overallXRange() ?: return withEmptyStatValues()

        val statData = BinStatUtil.computeSummaryStatSeries(xs, ys, paramAggFunctions + aesAggFunctions, rangeX, xPosKind, xPos, binOptions)
        if (statData.isEmpty()) {
            return withEmptyStatValues()
        }

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }
        return builder.build()
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.YMIN to Stats.Y_MIN,
            Aes.YMAX to Stats.Y_MAX
        )
    }
}