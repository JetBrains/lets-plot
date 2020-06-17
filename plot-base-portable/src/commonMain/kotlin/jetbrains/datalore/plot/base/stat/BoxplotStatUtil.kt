/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.gcommon.collect.Iterables.filter
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.max

internal object BoxplotStatUtil {
    /**
     * @return max num of observations in this group for all X
     * ToDo: implement 'weight'
     * ToDo: the 'group' is alvays 0. Do we need it?
     */
    fun buildStat(xs: List<Double?>, ys: List<Double?>, whiskerIQRRatio: Double, group: Int, collector: MutableMap<DataFrame.Variable, MutableList<Double>>): Int {
        if (collector.isEmpty()) {
            collector[Stats.X] = ArrayList()
            collector[Stats.Y] = ArrayList()
            collector[Stats.MIDDLE] = ArrayList()
            collector[Stats.LOWER] = ArrayList()
            collector[Stats.UPPER] = ArrayList()
            collector[Stats.Y_MIN] = ArrayList()
            collector[Stats.Y_MAX] = ArrayList()
            collector[Stats.WIDTH] = ArrayList()  // will contain 'counts' for each X
            collector[Stats.GROUP] = ArrayList()
        }

        val binnedData: MutableMap<Double, MutableList<Double>> = HashMap()
        val ys_ = ys.iterator()
        for (x in xs) {
            val y = ys_.next()
            if (SeriesUtil.isFinite(x) && SeriesUtil.isFinite(y)) {
                if (!binnedData.containsKey(x)) {
                    binnedData[x!!] = ArrayList()
                }
                binnedData[x]!!.add(y!!)
            }
        }

        if (binnedData.isEmpty()) {
            return 0
        }

        val statX = ArrayList<Double>()
        val statY = ArrayList<Double>()
        val statMiddle = ArrayList<Double>()
        val statLower = ArrayList<Double>()
        val statUpper = ArrayList<Double>()
        val statMin = ArrayList<Double>()
        val statMax = ArrayList<Double>()

        val statGroup = ArrayList<Double>()
        val statCount = ArrayList<Double>()

        var maxCount = 0
        for (x in binnedData.keys) {
            val bin = binnedData.get(x)!!

            val summary = FiveNumberSummary(bin)
            val middle = summary.median
            val lowerHinge = summary.firstQuartile
            val upperHinge = summary.thirdQuartile
            val IQR = upperHinge - lowerHinge
            val lowerFence = lowerHinge - IQR * whiskerIQRRatio
            val upperFence = upperHinge + IQR * whiskerIQRRatio

            var lowerWhisker = lowerFence
            var upperWhisker = upperFence
            if (SeriesUtil.isFinite(lowerFence) && SeriesUtil.isFinite(upperFence)) {
                val range = SeriesUtil.range(filter(bin) { y -> y >= lowerFence && y <= upperFence })
                if (range != null) {
                    lowerWhisker = range.lowerEndpoint()
                    upperWhisker = range.upperEndpoint()
                }
            }

            // add outliers first
            val outliers = filter(bin) { y: Double -> y < lowerFence || y > upperFence }
            var outlierCount = 0
            for (y in outliers) {
                outlierCount++
                // add 'outlier' data-point
                statX.add(x)
                statY.add(y)
                // no 'box' data
                statMiddle.add(Double.NaN)
                statLower.add(Double.NaN)
                statUpper.add(Double.NaN)
                statMin.add(Double.NaN)
                statMax.add(Double.NaN)
            }

            // add 'box' data-point
            statX.add(x)
            statY.add(Double.NaN)  // no Y for 'box' data-point
            statMiddle.add(middle)
            statLower.add(lowerHinge)
            statUpper.add(upperHinge)
            statMin.add(lowerWhisker)
            statMax.add(upperWhisker)

            // add data which is common for all data-points in bin
            // note: outliers also need 'width' for 'dodge' positioning to work correctly for all data-points
            val count = bin.size
            maxCount = max(maxCount, count)
            for (i in 0 until outlierCount + 1) {
                statCount.add(count.toDouble())
                statGroup.add(group.toDouble())
            }
        }

        collector[Stats.X]!!.addAll(statX)
        collector[Stats.Y]!!.addAll(statY)
        collector[Stats.MIDDLE]!!.addAll(statMiddle)
        collector[Stats.LOWER]!!.addAll(statLower)
        collector[Stats.UPPER]!!.addAll(statUpper)
        collector[Stats.Y_MIN]!!.addAll(statMin)
        collector[Stats.Y_MAX]!!.addAll(statMax)
        collector[Stats.WIDTH]!!.addAll(statCount)
        collector[Stats.GROUP]!!.addAll(statGroup)

        return maxCount
    }
}
