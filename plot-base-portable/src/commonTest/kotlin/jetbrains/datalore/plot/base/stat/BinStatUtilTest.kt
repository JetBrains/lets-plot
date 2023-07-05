/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import kotlin.test.*

class BinStatUtilTest {
    @Test
    fun checkBinCountAndWidthWithZeroDataRange() {
        val binOptions = BinStatUtil.binCountAndWidth(0.0, BinStatUtil.BinOptions(5, null))
        assertEquals(0.0, binOptions.width)
        assertEquals(5, binOptions.count)
    }

    @Test
    fun checkBinCountAndWidthWithoutBinWidth() {
        val binOptions = BinStatUtil.binCountAndWidth(3.5, BinStatUtil.BinOptions(7, null))
        assertEquals(0.5, binOptions.width)
        assertEquals(7, binOptions.count)
    }

    @Test
    fun checkBinCountAndWidthWithZeroBinCount() {
        val binOptions = BinStatUtil.binCountAndWidth(3.5, BinStatUtil.BinOptions(0, null))
        assertEquals(3.5, binOptions.width)
        assertEquals(1, binOptions.count)
    }

    @Test
    fun checkBinCountAndWidthWithTooBigBinCount() {
        val binOptions = BinStatUtil.binCountAndWidth(50.0, BinStatUtil.BinOptions(501, null))
        assertEquals(0.1, binOptions.width)
        assertEquals(500, binOptions.count)
    }

    @Test
    fun checkBinCountAndWidthWithConflictingBinWidth() {
        val binOptions = BinStatUtil.binCountAndWidth(3.5, BinStatUtil.BinOptions(5, 0.5))
        assertEquals(0.5, binOptions.width)
        assertEquals(7, binOptions.count)
    }

    @Test
    fun checkBinCountAndWidthWithTooSmallBinWidth() {
        val binOptions = BinStatUtil.binCountAndWidth(50.0, BinStatUtil.BinOptions(5, 0.05))
        assertEquals(0.05, binOptions.width)
        assertEquals(500, binOptions.count)
    }

    @Test
    fun checkGetBinningParametersWithNoneXPosKind() {
        val (binCount, binWidth, startX) = BinStatUtil.getBinningParameters(
            DoubleSpan(-5.0, 5.0),
            BinStat.XPosKind.NONE,
            3.5,
            BinStatUtil.BinOptions(2, null)
        )
        assertEquals(2, binCount)
        assertEquals(8.5, binWidth)
        assertEquals(-8.5, startX)
    }

    @Test
    fun checkGetBinningParametersWithCenterXPosKind() {
        val (binCount, binWidth, startX) = BinStatUtil.getBinningParameters(
            DoubleSpan(-5.0, 5.0),
            BinStat.XPosKind.CENTER,
            3.5,
            BinStatUtil.BinOptions(2, null)
        )
        assertEquals(2, binCount)
        assertEquals(8.5, binWidth)
        assertEquals(-9.25, startX)
    }

    @Test
    fun checkGetBinningParametersWithBoundaryXPosKind() {
        val (binCount, binWidth, startX) = BinStatUtil.getBinningParameters(
            DoubleSpan(-5.0, 5.0),
            BinStat.XPosKind.BOUNDARY,
            3.5,
            BinStatUtil.BinOptions(2, null)
        )
        assertEquals(2, binCount)
        assertEquals(8.5, binWidth)
        assertEquals(-5.0, startX)
    }

    @Test
    fun checkComputeSummaryStatSeries() {
        val statData = BinStatUtil.computeSummaryStatSeries(
            listOf(-5.0, -2.5, 0.0, 2.5, 5.0),
            listOf(-1.0, 1.0, 1.0, 2.0, 3.0),
            mapOf(
                Stats.Y to AggregateFunctions::mean,
                Stats.Y_MIN to AggregateFunctions::min,
                Stats.Y_MAX to AggregateFunctions::max,
                Stats.COUNT to AggregateFunctions::count,
                Stats.SUM to AggregateFunctions::sum,
            ),
            DoubleSpan(-5.0, 5.0),
            BinStat.XPosKind.NONE,
            0.0,
            BinStatUtil.BinOptions(2, null)
        )
        assertContentEquals(listOf(-4.25, 4.25), statData[Stats.X])
        assertContentEquals(listOf(0.0, 2.0), statData[Stats.Y])
        assertContentEquals(listOf(-1.0, 1.0), statData[Stats.Y_MIN])
        assertContentEquals(listOf(1.0, 3.0), statData[Stats.Y_MAX])
        assertContentEquals(listOf(2.0, 3.0), statData[Stats.COUNT])
    }
}