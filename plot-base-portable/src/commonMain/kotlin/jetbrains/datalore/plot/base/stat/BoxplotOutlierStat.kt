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
import kotlin.math.sqrt

class BoxplotOutlierStat(
    private val whiskerIQRRatio: Double,    // ggplot: 'coef'
    private val computeWidth: Boolean       // ggplot: 'varWidth'
) : BaseStat(DEF_MAPPING) {
    // Note: outliers will need 'width' value, for the 'dodge' positioning to work correctly for all data-points.

    override fun hasDefaultMapping(aes: Aes<*>): Boolean {
        return super.hasDefaultMapping(aes) ||
                aes == Aes.WIDTH && computeWidth
    }

    override fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable {
        return if (aes == Aes.WIDTH) {
            Stats.WIDTH
        } else {
            super.getDefaultMapping(aes)
        }
    }

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
        val statCount = statData.remove(Stats.COUNT)

        if (computeWidth) {
            // 'width' is in range 0..1
            val maxCountPerBin = statCount?.maxOrNull()?.toInt() ?: 0
            val norm = sqrt(maxCountPerBin.toDouble())
            val statWidth = statCount!!.map { count -> sqrt(count) / norm }
            statData[Stats.WIDTH] = statWidth
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
            val statCount = ArrayList<Double>()

            for ((x, bin) in binnedData) {
                val count = bin.size.toDouble()
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
                    statCount.add(count)
                }

                // If there are no outliers, add a fake one to correct splitting for additional grouping
                if (outliers.isEmpty() && count > 0) {
                    statX.add(x)
                    statY.add(Double.NaN)
                    statCount.add(count)
                }
            }

            return mutableMapOf(
                Stats.X to statX,
                Stats.Y to statY,
                Stats.COUNT to statCount
            )
        }
    }
}