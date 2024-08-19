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

    private fun applyBinStat(df: DataFrame, binCount: Int): DataFrame {
        val stat = BinStat(
            binCount,
            null,
            BinStat.XPosKind.NONE,
            0.0,
            null
        )
        val statDf = stat.apply(df, SimpleStatContext(df))
        DataFrameAssert.assertHasVars(statDf, listOf(Stats.X, Stats.COUNT, Stats.DENSITY), binCount)
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
    }
}