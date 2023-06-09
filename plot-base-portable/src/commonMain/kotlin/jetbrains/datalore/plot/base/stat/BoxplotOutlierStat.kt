/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.common.data.SeriesUtil

class BoxplotOutlierStat(
    private val whiskerIQRRatio: Double,    // ggplot: 'coef'
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
            Aes.Y to Stats.Y
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

            val binnedData: MutableMap<Double, MutableList<Double>> = HashMap()
            for ((x, y) in xyPairs) {
                binnedData.getOrPut(x) { ArrayList() }.add(y)
            }

            val statX = ArrayList<Double>()
            val statY = ArrayList<Double>()

            for ((x, bin) in binnedData) {
                val summary = FiveNumberSummary(bin)
                val lowerHinge = summary.firstQuartile
                val upperHinge = summary.thirdQuartile
                val IQR = upperHinge - lowerHinge
                val lowerFence = lowerHinge - IQR * whiskerIQRRatio
                val upperFence = upperHinge + IQR * whiskerIQRRatio
                val outliers = bin.filter { y -> y < lowerFence || y > upperFence }
                for (y in outliers) {
                    statX.add(x)
                    statY.add(y)
                }

                // If there are no outliers, add a fake one to correct splitting for additional grouping
                if (outliers.isEmpty() && bin.size > 0) {
                    statX.add(x)
                    statY.add(Double.NaN)
                }
            }

            return mutableMapOf(
                Stats.X to statX,
                Stats.Y to statY
            )
        }
    }
}