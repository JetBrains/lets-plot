/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import demoAndTestShared.assertEquals
import org.jetbrains.letsPlot.commons.intern.indicesOf
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SinaStatTest : BaseStatTest() {
    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(sinaStat())
    }

    @Test
    fun oneElementDataFrame() {
        val df = dataFrame(mapOf(
            TransformVar.Y to listOf(3.14)
        ))
        compareWithYDensity(df)
    }

    @Test
    fun twoElementsInDataFrame() {
        val df = dataFrame(mapOf(
            TransformVar.Y to listOf(2.71, 3.14)
        ))
        compareWithYDensity(df)
    }

    @Test
    fun withNanValues() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(null, 4.0, 3.0, 3.0, 1.0, 1.0, 2.0, 2.0),
            TransformVar.Y to listOf(3.0, null, 2.0, 3.0, 0.0, 1.0, 1.0, 2.0)
        ))
        compareWithYDensity(df)
    }

    @Test
    fun changeScales() {
        val df = dataFrame(mapOf(
            TransformVar.X to listOf(0.0, 0.0, 0.0, 0.0, 1.0, 1.0),
            TransformVar.Y to listOf(0.0, 1.0, 2.0, 3.0, 0.0, 1.0)
        ))
        for (scale in BaseYDensityStat.Scale.entries) {
            compareWithYDensity(df, scale, scale)
        }
    }

    @Test
    fun randomDataFrameTest() {
        val rand = Random(42)
        (0 until 5).forEach { _ ->
            val size = rand.nextInt(2, 100)
            val df = dataFrame(mapOf(
                TransformVar.Y to List(size) { rand.nextDouble() }
            ))
            compareWithYDensity(df)
        }
    }

    @Test
    fun regressionTest() {
        val df = dataFrame(mapOf(
            TransformVar.Y to listOf(3.0, 4.0, 2.0, 4.0, 4.0, 1.0, 2.0, 2.0, 2.0, 4.0, 3.0, 2.0, 4.0, 1.0, 3.0, 1.0, 3.0, 4.0)
        ))
        compareWithYDensity(df)
    }

    private fun compareWithYDensity(df: DataFrame, sinaScale: BaseYDensityStat.Scale? = null, yDensityScale: BaseYDensityStat.Scale? = null) {
        val sinaStat = sinaStat(sinaScale)
        val sinaStatDf = sinaStat.normalize(sinaStat.apply(df, statContext(df)))
        val yDensityStat = yDensityStat(yDensityScale)
        val yDensityStatDf = yDensityStat.normalize(yDensityStat.apply(df, statContext(df)))
        val xDistinctValues = sinaStatDf.getNumeric(Stats.X).map(::assertNotNull).toSet()
        for (x in xDistinctValues) {
            val sinaIndices = sinaStatDf.getNumeric(Stats.X).indicesOf { it == x }
            val yDensityIndices = yDensityStatDf.getNumeric(Stats.X).indicesOf { it == x }
            compareGroup(sinaStatDf.slice(sinaIndices), yDensityStatDf.slice(yDensityIndices), x)
        }
    }

    private fun compareGroup(sinaStatDf: DataFrame, yDensityStatDf: DataFrame, x: Double) {
        val sinaY = sinaStatDf.getNumeric(Stats.Y).map(::assertNotNull)
        val sinaViolinWidth = sinaStatDf.getNumeric(Stats.VIOLIN_WIDTH).map(::assertNotNull)
        val sinaQuantile = sinaStatDf.getNumeric(Stats.QUANTILE).map(::assertNotNull)
        val yDensityY = yDensityStatDf.getNumeric(Stats.Y).map(::assertNotNull)
        val yDensityViolinWidth = yDensityStatDf.getNumeric(Stats.VIOLIN_WIDTH).map(::assertNotNull)
        val yDensityQuantile = yDensityStatDf.getNumeric(Stats.QUANTILE).map(::assertNotNull)
        var startIndex = 0
        val lastIndex = yDensityY.size - 1
        for (i in 0 until sinaStatDf.rowCount()) {
            val y = sinaY[i]
            val violinWidth = sinaViolinWidth[i]
            val minIndex = (startIndex..lastIndex).lastOrNull { j -> yDensityY[j] <= y } ?: lastIndex
            val maxIndex = (minIndex..lastIndex).firstOrNull { j -> y < yDensityY[j] } ?: lastIndex
            startIndex = minIndex
            val violinWidthForMinIndex = yDensityViolinWidth[minIndex]
            val violinWidthForMaxIndex = yDensityViolinWidth[maxIndex]
            val minViolinWidth = min(violinWidthForMinIndex, violinWidthForMaxIndex)
            val maxViolinWidth = max(violinWidthForMinIndex, violinWidthForMaxIndex)
            assertTrue(
                violinWidth - minViolinWidth > -EPSILON,
                "ViolinWidth: For X = $x and Y = $y it should be: $minViolinWidth <= $violinWidth"
            )
            assertTrue(
                maxViolinWidth - violinWidth > -EPSILON,
                "ViolinWidth: For X = $x and Y = $y it should be: $violinWidth <= $maxViolinWidth"
            )
            assertEquals(
                yDensityQuantile[minIndex],
                sinaQuantile[i],
                EPSILON,
                "Quantile: For X = $x and Y = $y it should be: ${sinaQuantile[i]} == ${yDensityQuantile[minIndex]}"
            )
        }
    }

    private fun sinaStat(scale: BaseYDensityStat.Scale? = null): SinaStat {
        return SinaStat(
            scale = scale ?: BaseYDensityStat.DEF_SCALE,
            trim = BaseYDensityStat.DEF_TRIM,
            tailsCutoff = BaseYDensityStat.DEF_TAILS_CUTOFF,
            bandWidth = null,
            bandWidthMethod = DensityStat.DEF_BW,
            adjust = DensityStat.DEF_ADJUST,
            kernel = DensityStat.DEF_KERNEL,
            n = DensityStat.DEF_N,
            fullScanMax = DensityStat.DEF_FULL_SCAN_MAX,
            quantiles = BaseYDensityStat.DEF_QUANTILES
        )
    }

    private fun yDensityStat(scale: BaseYDensityStat.Scale? = null): YDensityStat {
        return YDensityStat(
            scale = scale ?: BaseYDensityStat.DEF_SCALE,
            trim = BaseYDensityStat.DEF_TRIM,
            tailsCutoff = BaseYDensityStat.DEF_TAILS_CUTOFF,
            bandWidth = null,
            bandWidthMethod = DensityStat.DEF_BW,
            adjust = DensityStat.DEF_ADJUST,
            kernel = DensityStat.DEF_KERNEL,
            n = DensityStat.DEF_N,
            fullScanMax = DensityStat.DEF_FULL_SCAN_MAX,
            quantiles = BaseYDensityStat.DEF_QUANTILES
        )
    }

    companion object {
        const val EPSILON = 1e-12
    }
}