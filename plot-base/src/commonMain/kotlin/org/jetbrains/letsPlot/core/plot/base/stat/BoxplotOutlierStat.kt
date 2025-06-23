/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.StatContext
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar

class BoxplotOutlierStat(
    private val whiskerIQRRatio: Double    // ggplot: 'coef'
) : BaseStat(DEF_MAPPING) {

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

        val statData = buildStat(xs, ys, whiskerIQRRatio)

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
            Aes.YMAX to Stats.Y_MAX,
            Aes.LOWER to Stats.LOWER,
            Aes.MIDDLE to Stats.MIDDLE,
            Aes.UPPER to Stats.UPPER
        )

        private fun buildStat(
            xs: List<Double?>,
            ys: List<Double?>,
            whiskerIQRRatio: Double
        ): MutableMap<DataFrame.Variable, List<Double>> {
            val xyPairs = SeriesUtil.filterFinite(xs, ys)
                .let { (xs, ys) -> xs zip ys }
            if (xyPairs.isEmpty()) {
                return mutableMapOf()
            }

            val binnedData: MutableMap<Double, MutableList<Double>> = LinkedHashMap()
            for ((x, y) in xyPairs) {
                binnedData.getOrPut(x) { ArrayList() }.add(y)
            }

            val statX = ArrayList<Double>()
            val statY = ArrayList<Double>()

            val statMiddle = ArrayList<Double>()
            val statLower = ArrayList<Double>()
            val statUpper = ArrayList<Double>()
            val statMin = ArrayList<Double>()
            val statMax = ArrayList<Double>()

            for ((x, bin) in binnedData) {
                val summary = FiveNumberSummary(bin)
                val middle = summary.median
                val lowerHinge = summary.firstQuartile
                val upperHinge = summary.thirdQuartile
                val IQR = upperHinge - lowerHinge
                val lowerFence = lowerHinge - IQR * whiskerIQRRatio
                val upperFence = upperHinge + IQR * whiskerIQRRatio

                var lowerWhisker = lowerFence
                var upperWhisker = upperFence
                if (SeriesUtil.allFinite(lowerFence, upperFence)) {
                    val boxed = bin.filter { y -> y in lowerFence..upperFence }
                    val range = DoubleSpan.encloseAllQ(boxed)
                    if (range != null) {
                        lowerWhisker = range.lowerEnd
                        upperWhisker = range.upperEnd
                    }
                }

                val outliers = bin.filter { y -> y < lowerFence || y > upperFence }
                val binOutliers = if (outliers.isEmpty() && bin.size > 0) {
                    // If there are no outliers, add a fake one to correct splitting for additional grouping
                    listOf(Double.NaN)
                } else {
                    outliers
                }

                for (y in binOutliers) {
                    statX.add(x)
                    statY.add(y)

                    statMiddle.add(middle)
                    statLower.add(lowerHinge)
                    statUpper.add(upperHinge)
                    statMin.add(lowerWhisker)
                    statMax.add(upperWhisker)
                }
            }

            return mutableMapOf(
                Stats.X to statX,
                Stats.Y to statY,
                Stats.MIDDLE to statMiddle,
                Stats.LOWER to statLower,
                Stats.UPPER to statUpper,
                Stats.Y_MIN to statMin,
                Stats.Y_MAX to statMax,
            )
        }
    }
}