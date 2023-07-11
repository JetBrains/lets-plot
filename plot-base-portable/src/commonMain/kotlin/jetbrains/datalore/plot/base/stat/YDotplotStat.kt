/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil.isFinite

class YDotplotStat(
    binCount: Int,
    binWidth: Double?,
    private val xPosKind: BinStat.XPosKind,
    private val xPos: Double,
    private val method: DotplotStat.Method
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
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.Y,
            Aes.STACKSIZE to Stats.COUNT,
            Aes.BINWIDTH to Stats.BIN_WIDTH
        )
    }
}