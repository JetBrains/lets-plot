/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil.isFinite

class YDotplotStat(
    binCount: Int,
    binWidth: Double?,
    private val xPosKind: BinStat.XPosKind,
    private val xPos: Double,
    private val method: DotplotStat.Method
) : BaseStat(DEF_MAPPING) {
    private val binOptions = BinStatUtil.BinOptions(binCount, binWidth)

    override fun consumes(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        return listOf(org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, org.jetbrains.letsPlot.core.plot.base.Aes.Y)) {
            return withEmptyStatValues()
        }

        val ys = data.getNumeric(TransformVar.Y)
        val xs = if (data.has(TransformVar.X)) {
            data.getNumeric(TransformVar.X)
        } else {
            List(ys.size) { 0.0 }
        }

        val statData = buildStat(data, xs, ys, statCtx.overallYRange())

        val builder = DataFrame.Builder()
        for ((variable, series) in statData) {
            builder.putNumeric(variable, series)
        }

        return builder.build()
    }

    private fun buildStat(
        data: DataFrame,
        xs: List<Double?>,
        ys: List<Double?>,
        rangeY: DoubleSpan?
    ) : MutableMap<DataFrame.Variable, List<Double>> {
        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statBinWidth = ArrayList<Double>()

        if (rangeY != null) {
            val groupedData = (xs zip ys)
                .filter { isFinite(it.first) }
                .groupBy({ it.first!! }, { it.second })

            for ((x, ysGroup) in groupedData) {
                val binsData = when (method) {
                    DotplotStat.Method.DOTDENSITY ->
                        BinStatUtil.computeDotdensityStatSeries(rangeY, ysGroup, binOptions)
                    DotplotStat.Method.HISTODOT ->
                        BinStatUtil.computeHistogramStatSeries(data, rangeY, ysGroup, xPosKind, xPos, binOptions)
                }
                statX += MutableList(binsData.x.size) { x }
                statY += binsData.x
                statCount += binsData.count
                statDensity += binsData.density
                statBinWidth += binsData.binWidth
            }
        }

        return mutableMapOf(
            Stats.X to statX,
            Stats.Y to statY,
            Stats.COUNT to statCount,
            Stats.DENSITY to statDensity,
            Stats.BIN_WIDTH to statBinWidth
        )
    }

    companion object {
        private val DEF_MAPPING: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, DataFrame.Variable> = mapOf(
            org.jetbrains.letsPlot.core.plot.base.Aes.X to Stats.X,
            org.jetbrains.letsPlot.core.plot.base.Aes.Y to Stats.Y,
            org.jetbrains.letsPlot.core.plot.base.Aes.STACKSIZE to Stats.COUNT,
            org.jetbrains.letsPlot.core.plot.base.Aes.BINWIDTH to Stats.BIN_WIDTH
        )
    }
}