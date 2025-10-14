/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameAssert
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.test.Test

class BinStatTest {

    private fun applyBinStat(df: DataFrame, binCount: Int, breaks: List<Double> = emptyList()): DataFrame {
        val stat = BinStat(
            binCount,
            null,
            BinStat.XPosKind.NONE,
            0.0,
            breaks,
            null
        )
        val statDf = stat.apply(df, SimpleStatContext(df))
        DataFrameAssert.assertHasVars(statDf, listOf(Stats.X, Stats.COUNT, Stats.DENSITY, Stats.SUMPROP, Stats.SUMPCT), binCount)
        return statDf
    }

    private fun getBinWidth(df: DataFrame, binCount: Int): Double {
        val binOptions = BinStatUtil.BinOptions(binCount, null)
        val statCtx = SimpleStatContext(df)
        val rangeX = statCtx.overallXRange()
        if (rangeX == null) return 1.0
        val (_, binWidth, _) = BinStatUtil.getBinningParameters(rangeX, BinStat.XPosKind.NONE, 0.0, binOptions)
        return binWidth
    }

    @Test
    fun twoPointsInOneBin() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(1.0, 2.0)
            )
        )

        val statDf = applyBinStat(df, 1)
        val binWidth = getBinWidth(df, 1)

        // expecting count = [2]
        assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(2.0))

        // expecting density = [1 / width]
        assertThat(statDf.getNumeric(Stats.DENSITY), Matchers.contains(1.0 / binWidth))

        // expecting sumprop = [1]
        assertThat(statDf.getNumeric(Stats.SUMPROP), Matchers.contains(1.0))

        // expecting sumpct = [100]
        assertThat(statDf.getNumeric(Stats.SUMPCT), Matchers.contains(100.0))
    }

    @Test
    fun twoPointsInTwoBins() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(1.0, 2.0)
            )
        )

        val statDf = applyBinStat(df, 2)
        val binWidth = getBinWidth(df, 2)

        // expecting count = [1, 1]
        assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(1.0, 1.0))

        // expecting density sum is equal to 1 / width
        val area = binWidth * statDf.getNumeric(Stats.DENSITY).filterNotNull().sum()
        assertThat(area, Matchers.closeTo(1.0, 1e-12))

        // expecting sumprop sum is equal to 1
        val sumPropTotal = statDf.getNumeric(Stats.SUMPROP).filterNotNull().sum()
        assertThat(sumPropTotal, Matchers.closeTo(1.0, 1e-12))

        // expecting sumpct sum is equal to 100
        val sumPctTotal = statDf.getNumeric(Stats.SUMPCT).filterNotNull().sum()
        assertThat(sumPctTotal, Matchers.closeTo(100.0, 1e-12))
    }

    @Test
    fun twoPointsInFourBins() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(1.0, 2.0)
            )
        )

        val statDf = applyBinStat(df, 4)
        val binWidth = getBinWidth(df, 4)

        // expecting count = [1,0,0,1]
        assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(1.0, 0.0, 0.0, 1.0))

        // expecting density sum is equal to 1 / width
        val area = binWidth * statDf.getNumeric(Stats.DENSITY).filterNotNull().sum()
        assertThat(area, Matchers.closeTo(1.0, 1e-12))

        // expecting sumprop sum is equal to 1
        val sumPropTotal = statDf.getNumeric(Stats.SUMPROP).filterNotNull().sum()
        assertThat(sumPropTotal, Matchers.closeTo(1.0, 1e-12))

        // expecting sumpct sum is equal to 100
        val sumPctTotal = statDf.getNumeric(Stats.SUMPCT).filterNotNull().sum()
        assertThat(sumPctTotal, Matchers.closeTo(100.0, 1e-12))
    }

    @Test
    fun fourPointsInUnequalBins() {
        val breaks = listOf(0.0, 2.0, 6.0, 8.0)
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(1.0, 3.0, 5.0, 7.0)
            )
        )

        val statDf = applyBinStat(df, breaks.size - 1, breaks = breaks)

        // expecting count = [1,2,1]
        assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(1.0, 2.0, 1.0))

        // expecting area under density = 1
        val area = (listOf(2.0, 4.0, 2.0) zip statDf.getNumeric(Stats.DENSITY).filterNotNull()).sumOf { it.first * it.second }
        assertThat(area, Matchers.closeTo(1.0, 1e-12))

        // expecting sumprop sum is equal to 1
        val sumPropTotal = statDf.getNumeric(Stats.SUMPROP).filterNotNull().sum()
        assertThat(sumPropTotal, Matchers.closeTo(1.0, 1e-12))

        // expecting sumpct sum is equal to 100
        val sumPctTotal = statDf.getNumeric(Stats.SUMPCT).filterNotNull().sum()
        assertThat(sumPctTotal, Matchers.closeTo(100.0, 1e-12))
    }
}