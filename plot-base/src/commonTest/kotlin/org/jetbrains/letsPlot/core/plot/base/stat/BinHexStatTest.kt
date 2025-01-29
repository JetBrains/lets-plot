/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.math.sqrt
import kotlin.test.Test

class BinHexStatTest : BaseStatTest() {
    @Test
    fun basicTest() {
        val expectedY0 = -10.0 * HEIGHT_MULTIPLIER
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEIGHT_MULTIPLIER }
            ),
            expected = mapOf(
                Stats.X to listOf(-10.0, 10.0, 0.0, 20.0),
                Stats.Y to listOf(expectedY0, expectedY0, expectedY0 + 20.0, expectedY0 + 20.0),
                Stats.COUNT to listOf(1.0, 1.0, 2.0, 1.0),
                Stats.WIDTH to List(4) { 20.0 },
                Stats.HEIGHT to List(4) { 20 * STAT_HEIGHT_MULTIPLIER }
            ),
            binWidthX = 20.0,
            binWidthY = 20.0
        )
    }

    @Test
    fun emptyDataFrame() {
        testEmptyDataFrame(BinHexStat())
    }

    @Test
    fun oneElementDataFrame() {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(0.0),
                TransformVar.Y to listOf(0.0)
            ),
            expected = mapOf(
                Stats.X to listOf(0.0),
                Stats.Y to listOf(0.0),
                Stats.COUNT to listOf(1.0),
                Stats.WIDTH to listOf(1.0),
                Stats.HEIGHT to listOf(STAT_HEIGHT_MULTIPLIER)
            ),
            binWidthX = 1.0,
            binWidthY = 1.0
        )
    }

    @Test
    fun withNanValues() {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(0.0, null, 1.0),
                TransformVar.Y to listOf(0.0, 1.0, null)
            ),
            expected = mapOf(
                Stats.X to listOf(0.0),
                Stats.Y to listOf(0.0),
                Stats.COUNT to listOf(1.0),
                Stats.WIDTH to listOf(1.0),
                Stats.HEIGHT to listOf(STAT_HEIGHT_MULTIPLIER)
            ),
            binWidthX = 1.0,
            binWidthY = 1.0
        )
    }

    @Test
    fun aesWeight() {
        val expectedY0 = -10.0 * HEIGHT_MULTIPLIER
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEIGHT_MULTIPLIER },
                TransformVar.WEIGHT to listOf(1.0, 2.0, 4.0, 8.0, 16.0)
            ),
            expected = mapOf(
                Stats.X to listOf(-10.0, 10.0, 0.0, 20.0),
                Stats.Y to listOf(expectedY0, expectedY0, expectedY0 + 20.0, expectedY0 + 20.0),
                Stats.COUNT to listOf(1.0, 4.0, 10.0, 16.0),
                Stats.WIDTH to List(4) { 20.0 },
                Stats.HEIGHT to List(4) { 20 * STAT_HEIGHT_MULTIPLIER }
            ),
            binWidthX = 20.0,
            binWidthY = 20.0
        )
    }

    @Test
    fun paramBinsTest() {
        // TODO
        /*
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 10.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 10.0).map { it * HEIGHT_MULTIPLIER }
            ),
            expected = mapOf(
                Stats.X to listOf(-10.0, 10.0, 0.0, 20.0),
                Stats.Y to listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEIGHT_MULTIPLIER },
                Stats.COUNT to listOf(1.0, 1.0, 1.0, 1.0),
                Stats.WIDTH to List(4) { 20.0 },
                Stats.HEIGHT to List(4) { 20 * HEIGHT_MULTIPLIER * STAT_HEIGHT_MULTIPLIER }
            ),
            binCountX = 2,
            binCountY = 2
        )
        */
    }

    @Test
    fun paramBinwidthTest() {
        // TODO()
    }

    @Test
    fun paramDropTest() {
        // TODO()
    }

    @Test
    fun paramOrientationTest() {
        // TODO()
    }

    @Test
    fun pointOnBorderTest() {
        // TODO()
    }

    @Test
    fun stretchedHexagonsRegressionTest() {
        // TODO()
    }

    @Test
    fun floatingPointRoundingErrorRegressionTest() {
        // TODO()
    }

    private fun checkValues(
        data: Map<DataFrame.Variable, List<Double?>>,
        expected: Map<DataFrame.Variable, List<Double>>,
        binCountX: Int? = null,
        binCountY: Int? = null,
        binWidthX: Double? = null,
        binWidthY: Double? = null,
    ) {
        val df = dataFrame(data)
        val stat = BinHexStat(
            binCountX = binCountX ?: BinHexStat.DEF_BINS,
            binCountY = binCountY ?: BinHexStat.DEF_BINS,
            binWidthX = binWidthX,
            binWidthY = binWidthY
        )
        val statDf = stat.apply(df, statContext(df))

        for (variable in expected.keys) {
            checkStatVarValues(statDf, variable, expected.getValue(variable))
        }
    }

    companion object {
        private val HEIGHT_MULTIPLIER = sqrt(3.0) / 2.0 // inverse of height of right hexagon with width = 1
        private val STAT_HEIGHT_MULTIPLIER = 2.0 * sqrt(3.0) / 3.0 // stat height when binWidthY is equal to 1
    }
}