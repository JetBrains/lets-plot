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
            false
        )
        val statDf = stat.apply(df, SimpleStatContext(df))
        DataFrameAssert.assertHasVars(statDf, listOf(Stats.X, Stats.COUNT, Stats.DENSITY), binCount)
        return statDf
    }

    @Test
    fun twoPointsInOneBin() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(1.0, 2.0)
            )
        )

        val statDf = applyBinStat(df, 1)

        // expecting count = [2]
        assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(2.0))

        // expecting density = [1]
        assertThat(statDf.getNumeric(Stats.DENSITY), Matchers.contains(1.0))
    }

    @Test
    fun twoPointsInTwoBins() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(1.0, 2.0)
            )
        )

        val statDf = applyBinStat(df, 2)

        // expecting count = [1, 1]
        assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(1.0, 1.0))

        // expecting density = [1, 1]   (width = 0.5 -> 0.5 + 0.5 = 1)
        assertThat(statDf.getNumeric(Stats.DENSITY), Matchers.contains(1.0, 1.0))
    }

    @Test
    fun twoPointsInFourBins() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(1.0, 2.0)
            )
        )

        val statDf = applyBinStat(df, 4)

        // expecting count = [1,0,0,1]
        assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(1.0, 0.0, 0.0, 1.0))

        // expecting density = [2, 0, 0, 2]  (width = 0.25 -> 2 * 0.25 + 0 + 0 + 2 * 0.25 = 1)
        assertThat(statDf.getNumeric(Stats.DENSITY), Matchers.contains(2.0, 0.0, 0.0, 2.0))
    }
}