/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import junit.framework.TestCase
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameAssert
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.Bin2dStat
import org.jetbrains.letsPlot.core.plot.base.stat.SimpleStatContext
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.junit.Test

class Bin2dStatTest {
    @Test
    fun twoPointsInOneBin() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(0.0, 1.0),
                TransformVar.Y.name to listOf(0.0, 0.0)
            )
        )

        val statDf = applyBin2dStat(df, 1, 1)

        // expecting count = [2]
        MatcherAssert.assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(2.0))

        // expecting density = [0.5]
        // (because expected bin size: 2x1, area=2.0 and density must integrate to 1.0)
        MatcherAssert.assertThat(statDf.getNumeric(Stats.DENSITY), Matchers.contains(0.5))
    }

    @Test
    fun twoPointsInTwoBins() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(0.0, 1.0),
                TransformVar.Y.name to listOf(0.0, 0.0)
            )
        )

        val statDf = applyBin2dStat(df, 2, 1)

        // expecting count = [1, 1]
        MatcherAssert.assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(1.0, 1.0))

        // width == 0.75 -> normalization factor = 1 / 0.75
        val normFactor = 1 / 0.75
        // If density sum / normFactor == 1 -> density sum == normFactor
        @Suppress("UNCHECKED_CAST")
        val densitySum = (statDf.getNumeric(Stats.DENSITY) as List<Double>).sum()
        TestCase.assertEquals(normFactor, densitySum)

        // expecting density - half in each bin
        MatcherAssert.assertThat(statDf.getNumeric(Stats.DENSITY), Matchers.contains(densitySum / 2, densitySum / 2))
    }

    @Test
    fun twoPointsInFourBins() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(0.0, 1.0),
                TransformVar.Y.name to listOf(0.0, 1.0)
            )
        )

        val statDf = applyBin2dStat(df, 2, 2)

        // expecting count = [1, 0, 0, 1]
        MatcherAssert.assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(1.0, 0.0, 0.0, 1.0))

        // width == 0.75
        // hight == 0.75 -> area == 0.5625
        val normFactor = 1 / 0.5625
        // If density sum / normFactor == 1 -> density sum == normFactor
        @Suppress("UNCHECKED_CAST")
        val densitySum = (statDf.getNumeric(Stats.DENSITY) as List<Double>).sum()
        TestCase.assertEquals(normFactor, densitySum)

        // expecting density - 1/2 in first and last bins
        MatcherAssert.assertThat(
            statDf.getNumeric(Stats.DENSITY),
            Matchers.contains(densitySum / 2, 0.0, 0.0, densitySum / 2)
        )
    }

    @Test
    fun twoPointsInFourBinsDropZeroes() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(0.0, 1.0),
                TransformVar.Y.name to listOf(0.0, 1.0)
            )
        )

        val statDf = applyBin2dStat(df, 2, 2, drop = true)

        // Only bins with count > 0
        // expecting count = [1, 1]
        MatcherAssert.assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(1.0, 1.0))

        // width == 0.75
        // hight == 0.75 -> area == 0.5625
        val normFactor = 1 / 0.5625
        // If density sum / normFactor == 1 -> density sum == normFactor
        @Suppress("UNCHECKED_CAST")
        val densitySum = (statDf.getNumeric(Stats.DENSITY) as List<Double>).sum()
        TestCase.assertEquals(normFactor, densitySum)

        // expecting density - 1/2 in first and last bins
        MatcherAssert.assertThat(
            statDf.getNumeric(Stats.DENSITY),
            Matchers.contains(densitySum / 2, densitySum / 2)
        )
    }

    @Test
    fun twoPointsInFourBinsWithWaight() {
        val df = DataFrameUtil.fromMap(
            mapOf(
                TransformVar.X.name to listOf(0.0, 1.0),
                TransformVar.Y.name to listOf(0.0, 1.0),
                TransformVar.WEIGHT.name to listOf(0.0, 2.0)
            )
        )

        val statDf = applyBin2dStat(df, 2, 2)

        // Only bins with count > 0
        // expecting count = [0,0,0,2]   // due to `weight`
        MatcherAssert.assertThat(statDf.getNumeric(Stats.COUNT), Matchers.contains(0.0, 0.0, 0.0, 2.0))

        // width == 0.75
        // hight == 0.75 -> area == 0.5625
        val normFactor = 1 / 0.5625
        // If density sum / normFactor == 1 -> density sum == normFactor
        @Suppress("UNCHECKED_CAST")
        val densitySum = (statDf.getNumeric(Stats.DENSITY) as List<Double>).sum()
        TestCase.assertEquals(normFactor, densitySum)

        // expecting density - all in the last bin
        MatcherAssert.assertThat(
            statDf.getNumeric(Stats.DENSITY),
            Matchers.contains(0.0, 0.0, 0.0, densitySum)
        )
    }

    companion object {
        private fun applyBin2dStat(df: DataFrame, binCountX: Int, binCountY: Int, drop: Boolean = false): DataFrame {
            val stat = Bin2dStat(
                binCountX,
                binCountY,
                null,
                null,
                drop = drop
            )
            val statCtx = SimpleStatContext(df, emptyList())
            val statDf = stat.apply(df, statCtx)

            val expectedSize = if (drop) {
                -1 // don't check
            } else {
                binCountX * binCountY
            }
            DataFrameAssert.assertHasVars(
                statDf,
                listOf(Stats.X, Stats.Y, Stats.COUNT, Stats.DENSITY),
                expectedSize
            )
            return statDf
        }

    }
}
