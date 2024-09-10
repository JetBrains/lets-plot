/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

/**
 * Default stat for geom_histogram
 *
 * @param binCount Number of bins (overridden by binWidth)
 * @param binWidth Used to compute binCount such that bins covers the range of the data
 * @param xPosKind Specifies a way in which bin x-position is interpreted (center, boundary)
 * @param xPos Bin x-position.
 * @param threshold Threshold for bin trimming
 *
 * Computed values:
 *
 * count - number of points in bin
 * density - density of points in bin, scaled to integrate to 1
 * ncount - count, scaled to maximum of 1
 * ndensity - density, scaled to maximum of 1
 */
open class BinStat(
    binCount: Int,
    binWidth: Double?,
    private val xPosKind: XPosKind,
    private val xPos: Double,
    private val threshold: Double?,
) : BaseStat(DEF_MAPPING) {
    private val binOptions = BinStatUtil.BinOptions(binCount, binWidth)

    override fun consumes(): List<Aes<*>> {
        return listOf(Aes.X, Aes.WEIGHT)
    }

    override fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit): DataFrame {
        if (!hasRequiredValues(data, Aes.X)) {
            return withEmptyStatValues()
        }

        val statX = ArrayList<Double>()
        val statCount = ArrayList<Double>()
        val statDensity = ArrayList<Double>()
        val statSumProp = ArrayList<Double>()
        val statSumPct = ArrayList<Double>()

        val rangeX = statCtx.overallXRange()
        if (rangeX != null) { // null means all input values are null
            val binsData = BinStatUtil.computeHistogramStatSeries(
                data,
                rangeX,
                data.getNumeric(TransformVar.X),
                xPosKind,
                xPos,
                binOptions
            )
            statX.addAll(binsData.x)
            statCount.addAll(binsData.count)
            statDensity.addAll(binsData.density)
            statSumProp.addAll(binsData.sumProp)
            statSumPct.addAll(binsData.sumPct)
        }

        if (threshold != null) {
            val leftDropPart = statCount.withIndex().takeWhile { it.value <= threshold }.map { it.index }
            val rightDropPart = statCount.withIndex().reversed().takeWhile { it.value <= threshold }.map { it.index }

            val dropList = leftDropPart + rightDropPart

            dropList.forEach {
                statCount[it] = Double.NaN
                statDensity[it] = Double.NaN
                statSumProp[it] = Double.NaN
                statSumPct[it] = Double.NaN
            }

            // resolution hack - need at least two consecutive X values, or width of the bin will be incorrect
            when {
                statX.size - dropList.size > 1 -> dropList // already have at least two consecutive X values
                leftDropPart.isNotEmpty() -> leftDropPart.dropLast(1) + rightDropPart
                rightDropPart.isNotEmpty() -> leftDropPart + rightDropPart.dropLast(1) // dropLast b/c reversed
                else -> emptyList()
            }.forEach { statX[it] = Double.NaN }
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.COUNT, statCount)
            .putNumeric(Stats.DENSITY, statDensity)
            .putNumeric(Stats.SUMPROP, statSumProp)
            .putNumeric(Stats.SUMPCT, statSumPct)
            .build()
    }

    enum class XPosKind {
        NONE, CENTER, BOUNDARY
    }

    companion object {
        const val DEF_BIN_COUNT = 30

        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.COUNT
        )
    }
}
