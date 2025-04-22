/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.test.Test
import kotlin.test.assertTrue

class SinaStatTest : YDensityStatTest() {
    @Test
    override fun emptyDataFrame() {
        testEmptyDataFrame(sinaStat())
    }

    @Test
    override fun oneElementDataFrame() {
        val df = dataFrame(mapOf(
            TransformVar.Y to listOf(3.14)
        ))
        compareWithYDensity(df)
    }

    // TODO: Uncomment when the problem with the test is fixed
    /*
    @Test
    fun regressionTest() {
        val df = dataFrame(mapOf(
            TransformVar.Y to listOf(3.0, 4.0, 2.0, 4.0, 4.0, 1.0, 2.0, 2.0, 2.0, 4.0, 3.0, 2.0, 4.0, 1.0, 3.0, 1.0, 3.0, 4.0)
        ))
        compareWithYDensity(df)
    }
    */

    private fun compareWithYDensity(df: DataFrame, sinaScale: SinaStat.Scale? = null, yDensityScale: YDensityStat.Scale? = null) {
        val sinaStat = sinaStat(sinaScale)
        val sinaStatDf = sinaStat.normalize(sinaStat.apply(df, statContext(df)))
        val yDensityStat = yDensityStat(yDensityScale)
        val yDensityStatDf = yDensityStat.normalize(yDensityStat.apply(df, statContext(df)))
        val xDistinctValues = sinaStatDf.getNumeric(Stats.X).map { it!! }.toSet()
        for (x in xDistinctValues) {
            val sinaXIndices = sinaStatDf.getNumeric(Stats.X).mapIndexed { i, v -> Pair(i, v) }.filter { it.second == x }.map(Pair<Int, Double?>::first)
            val yDensityXIndices = yDensityStatDf.getNumeric(Stats.X).mapIndexed { i, v -> Pair(i, v) }.filter { it.second == x }.map(Pair<Int, Double?>::first)
            compare(sinaStatDf.slice(sinaXIndices), yDensityStatDf.slice(yDensityXIndices), x)
        }
    }

    private fun compare(sinaDf: DataFrame, yDensityDf: DataFrame, x: Double) {
        val sinaY = sinaDf.getNumeric(Stats.Y).map { it!! }
        val sinaViolinWidth = sinaDf.getNumeric(Stats.VIOLIN_WIDTH).map { it!! }
        val yDensityY = yDensityDf.getNumeric(Stats.Y).map { it!! }
        val yDensityViolinWidth = yDensityDf.getNumeric(Stats.VIOLIN_WIDTH).map { it!! }
        val sortingIndices = yDensityY.mapIndexed { i, v -> Pair(i, v) }.sortedBy { it.second }.map(Pair<Int, Double?>::first)
        val sortedYDensityY = sortingIndices.map { yDensityY[it] }
        val sortedYDensityViolinWidth = sortingIndices.map { yDensityViolinWidth[it] }
        for (i in 0 until sinaDf.rowCount()) {
            val y = sinaY[i]
            val violinWidth = sinaViolinWidth[i]
            val minIndex = sortedYDensityY.indexOfLast { v -> v <= y }
            val maxIndex = sortedYDensityY.indexOfFirst { v -> v > y }
            val minViolinWidth = sortedYDensityViolinWidth[minIndex]
            val maxViolinWidth = sortedYDensityViolinWidth[maxIndex]
            assertTrue(
                minViolinWidth <= violinWidth,
                "For X = $x and Y = $y it should be: $minViolinWidth <= $violinWidth"
            )
            assertTrue(
                violinWidth <= maxViolinWidth,
                "For X = $x and Y = $y it should be: $violinWidth <= $maxViolinWidth"
            )
        }
    }

    private fun sinaStat(scale: SinaStat.Scale? = null): SinaStat {
        return SinaStat(
            scale = scale ?: SinaStat.DEF_SCALE,
            bandWidth = null,
            bandWidthMethod = DensityStat.DEF_BW,
            adjust = DensityStat.DEF_ADJUST,
            kernel = DensityStat.DEF_KERNEL,
            fullScanMax = DensityStat.DEF_FULL_SCAN_MAX,
            quantiles = YDensityStat.DEF_QUANTILES
        )
    }
}