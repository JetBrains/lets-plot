/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.BinStat.Companion.DEF_BIN_COUNT
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

    @Test
    fun checkComputeHistogramStatSeries() {
        val valuesX = listOf(-0.5, 0.0, 0.0, 1.5)
        val data = DataFrame.Builder()
            .putNumeric(TransformVar.X, valuesX)
            .build()
        val statData = BinStatUtil.computeHistogramStatSeries(
            data,
            DoubleSpan(valuesX.min(), valuesX.max()),
            valuesX,
            BinStat.XPosKind.CENTER,
            0.0,
            BinStatUtil.BinOptions(DEF_BIN_COUNT, 0.5)
        )
        assertContentEquals(listOf(-0.5, 0.0, 0.5, 1.0, 1.5, 2.0), statData.x)
        assertContentEquals(listOf(1.0, 2.0, 0.0, 0.0, 1.0, 0.0), statData.count)
        assertContentEquals(listOf(0.5, 1.0, 0.0, 0.0, 0.5, 0.0), statData.density)
        assertContentEquals(listOf(0.25, 0.5, 0.0, 0.0, 0.25, 0.0), statData.sumProp)
        assertContentEquals(listOf(25.0, 50.0, 0.0, 0.0, 25.0, 0.0), statData.sumPct)
    }

    @Test
    fun checkHistogramNormalizedVariables() {
        val tolerance = 1e-13

        val checks = listOf(
            listOf(0.0),
            listOf(0.0, 1.0, 1.0),
            listOf(-10.0, 0.0, 1.0, 1.0, 3.0),
            listOf(0.0, 0.05, 0.051, 0.1),
        )

        for (valuesX in checks) {
            val binOptions = BinStatUtil.BinOptions(DEF_BIN_COUNT, null)
            val rangeX = DoubleSpan(valuesX.min(), valuesX.max())
            val xPosKind = BinStat.XPosKind.NONE
            val xPos = 0.0
            val (_, binWidth, _) = BinStatUtil.getBinningParameters(rangeX, xPosKind, xPos, binOptions)
            val data = DataFrame.Builder()
                .putNumeric(TransformVar.X, valuesX)
                .build()
            val statData = BinStatUtil.computeHistogramStatSeries(data, rangeX, valuesX, xPosKind, xPos, binOptions)
            val widthFactor = if (binWidth > 0) binWidth else 1.0
            val area = widthFactor * statData.density.sum()
            assertEquals(1.0, area, tolerance)
            val sumPropTotal = statData.sumProp.sum()
            assertEquals(1.0, sumPropTotal, tolerance)
            val sumPctTotal = statData.sumPct.sum()
            assertEquals(100.0, sumPctTotal, tolerance)
        }
    }
}