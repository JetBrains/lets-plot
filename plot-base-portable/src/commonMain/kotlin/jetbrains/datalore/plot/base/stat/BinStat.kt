/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.abs

/**
 * Default stat for geom_histogram
 *
 * @param binCount Number of bins (overridden by binWidth)
 * @param binWidth Used to compute binCount such that bins covers the range of the data
 * @param xPosKind Specifies a way in which bin x-position is interpreted (center, boundary)
 * @param xPos Bin x-position.
 *
 * Computed values:
 *
 * count - number of points in bin
 * density - density of points in bin, scaled to integrate to 1
 * ncount - count, scaled to maximum of 1
 * ndensity - density, scaled to maximum of 1
 */
internal class BinStat(
    binCount: Int,
    binWidth: Double?,
    private val xPosKind: XPosKind,
    private val xPos: Double
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

        val rangeX = statCtx.overallXRange()
        if (rangeX != null) { // null means all input values are null
            val binsData = computeStatSeries(data, rangeX, data.getNumeric(TransformVar.X))
            statX.addAll(binsData.x)
            statCount.addAll(binsData.count)
            statDensity.addAll(binsData.density)
        }

        return DataFrame.Builder()
            .putNumeric(Stats.X, statX)
            .putNumeric(Stats.COUNT, statCount)
            .putNumeric(Stats.DENSITY, statDensity)
            .build()
    }

    private fun computeStatSeries(
        data: DataFrame,
        rangeX: ClosedRange<Double>,
        valuesX: List<Double?>
    ): BinStatUtil.BinsData {
        var startX: Double? = rangeX.lowerEndpoint()
        var spanX = rangeX.upperEndpoint() - startX!!

        // initial bin count/width
        var b: BinStatUtil.CountAndWidth = BinStatUtil.binCountAndWidth(spanX, binOptions)

        // adjusted bin count/width
        // extend the data range by 0.7 of binWidth on each ends (to allow limited horizontal adjustments)
        startX -= b.width * 0.7
        spanX += b.width * 1.4
        b = BinStatUtil.binCountAndWidth(spanX, binOptions)
        val binCount = b.count
        val binWidth = b.width

        // optional horizontal adjustment (+/-0.5 bin width max)
        if (xPosKind != XPosKind.NONE) {
            var minDelta = Double.MAX_VALUE
            val x = xPos

            for (i in 0 until binCount) {
                val binLeft = startX + i * binWidth
                val delta: Double
                if (xPosKind == XPosKind.CENTER) {
                    delta = x - (binLeft + binWidth / 2)
                } else {       // BOUNDARY
                    if (i == 0) {
                        minDelta = x - startX // init still
                    }
                    delta = x - (binLeft + binWidth)
                }

                if (abs(delta) < abs(minDelta)) {
                    minDelta = delta
                }
            }

            // max offset: +/-0.5 bin width
            val offset = minDelta % (binWidth / 2)
            startX += offset
        }

        // density plot area should be == 1
        val normalBinWidth = SeriesUtil.span(rangeX) / binCount
        val densityNormalizingFactor = if (normalBinWidth > 0)
            1.0 / normalBinWidth
        else
            1.0

        // compute bins

        val binsData = BinStatUtil.computeBins(
            valuesX,
            startX,
            binCount,
            binWidth,
            BinStatUtil.weightAtIndex(data),
            densityNormalizingFactor
        )
        checkState(
            binsData.x.size == binCount,
            "Internal: stat data size=" + binsData.x.size + " expected bin count=" + binCount
        )
        return binsData
    }

    enum class XPosKind {
        NONE, CENTER, BOUNDARY
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
            Aes.X to Stats.X,
            Aes.Y to Stats.COUNT
        )
    }
}
