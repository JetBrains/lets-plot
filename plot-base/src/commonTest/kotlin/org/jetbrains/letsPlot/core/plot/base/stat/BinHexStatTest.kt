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
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEX_HEIGHT_INV }
            ),
            expected = mapOf(
                Stats.X to listOf(-10.0, 10.0, 0.0, 20.0),
                Stats.Y to listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEX_HEIGHT_INV },
                Stats.COUNT to listOf(1.0, 1.0, 2.0, 1.0),
                Stats.WIDTH to List(4) { 20.0 },
                Stats.HEIGHT to List(4) { 20.0 }
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
                Stats.HEIGHT to listOf(1.0)
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
                Stats.HEIGHT to listOf(1.0)
            ),
            binWidthX = 1.0,
            binWidthY = 1.0
        )
    }

    @Test
    fun aesWeight() {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEX_HEIGHT_INV },
                TransformVar.WEIGHT to listOf(1.0, 2.0, 4.0, 8.0, 16.0)
            ),
            expected = mapOf(
                Stats.X to listOf(-10.0, 10.0, 0.0, 20.0),
                Stats.Y to listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEX_HEIGHT_INV },
                Stats.COUNT to listOf(1.0, 4.0, 10.0, 16.0),
                Stats.WIDTH to List(4) { 20.0 },
                Stats.HEIGHT to List(4) { 20.0 }
            ),
            binWidthX = 20.0,
            binWidthY = 20.0
        )
    }

    @Test
    fun paramBinsTest() {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 10.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 10.0).map { it * HEX_HEIGHT_INV }
            ),
            expected = mapOf(
                Stats.X to listOf(-10.0, 10.0, 0.0, 20.0),
                Stats.Y to listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEX_HEIGHT_INV },
                Stats.COUNT to listOf(1.0, 1.0, 1.0, 1.0),
                Stats.WIDTH to List(4) { 20.0 },
                Stats.HEIGHT to List(4) { 20.0 }
            ),
            binCountX = 2,
            binCountY = 2
        )
    }

    @Test
    fun paramBinwidthTest() {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEX_HEIGHT_INV }
            ),
            expected = mapOf(
                Stats.X to listOf(-10.0, 10.0, -10.0, 10.0),
                Stats.Y to listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEX_HEIGHT_INV },
                Stats.COUNT to listOf(1.0, 1.0, 1.0, 2.0),
                Stats.WIDTH to List(4) { 10.0 },
                Stats.HEIGHT to List(4) { 10.0 }
            ),
            binWidthX = 10.0,
            binWidthY = 10.0
        )
    }

    @Test
    fun paramDropTest() {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEX_HEIGHT_INV }
            ),
            expected = mapOf(
                Stats.X to listOf(-10.0, 0.0, 10.0, 20.0,
                                   -5.0, 5.0, 15.0, 25.0,
                                  -10.0, 0.0, 10.0, 20.0,
                                   -5.0, 5.0, 15.0, 25.0),
                Stats.Y to listOf(-10.0, -10.0, -10.0, -10.0,
                                    0.0,   0.0,   0.0,   0.0,
                                   10.0,  10.0,  10.0,  10.0,
                                   20.0,  20.0,  20.0,  20.0).map { it * HEX_HEIGHT_INV },
                Stats.COUNT to listOf(1.0, 0.0, 1.0, 0.0,
                                      0.0, 0.0, 0.0, 0.0,
                                      1.0, 0.0, 2.0, 0.0,
                                      0.0, 0.0, 0.0, 0.0),
                Stats.WIDTH to List(16) { 10.0 },
                Stats.HEIGHT to List(16) { 10.0 }
            ),
            binWidthX = 10.0,
            binWidthY = 10.0,
            drop = false
        )
    }

    @Test
    fun pointOnBorderTest() {
        val halfHexHeight = 0.5 * HEX_HEIGHT
        checkBorderValues(0.0, halfHexHeight, listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0))
        checkBorderValues(0.25, 0.75 * halfHexHeight, listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0))
        checkBorderValues(0.5, 0.5 * halfHexHeight, listOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 2.0))
        checkBorderValues(0.5, 0.0, listOf(1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0))
        checkBorderValues(0.5, -0.5 * halfHexHeight, listOf(1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0))
        checkBorderValues(0.25, -0.75 * halfHexHeight, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(0.0, -halfHexHeight, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.25, -0.75 * halfHexHeight, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.5, -0.5 * halfHexHeight, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.5, 0.0, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.5, 0.5 * halfHexHeight, listOf(1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0))
        checkBorderValues(-0.25, 0.75 * halfHexHeight, listOf(1.0, 1.0, 1.0, 1.0, 1.0, 2.0, 1.0))
    }

    @Test
    fun stretchedHexagonsRegressionTest() {
        checkStretchedHexagonsRegression(1.0, 1.0)
        checkStretchedHexagonsRegression(1e-8, 1e-8)
        checkStretchedHexagonsRegression(1e-8, 1e8)
        checkStretchedHexagonsRegression(1e8, 1e-8)
        checkStretchedHexagonsRegression(1e8, 1e8)
    }

    @Test
    fun floatingPointRoundingErrorRegressionTest() {
        checkFloatingPointRoundingErrorRegression(1.0, 1.0)
        checkFloatingPointRoundingErrorRegression(1e-8, 1e-8)
        checkFloatingPointRoundingErrorRegression(1e-8, 1e8, epsilon = 1e-6)
        checkFloatingPointRoundingErrorRegression(1e8, 1e-8)
        checkFloatingPointRoundingErrorRegression(1e8, 1e8, epsilon = 1e-6)
    }

    private fun checkBorderValues(
        x: Double,
        y: Double,
        expectedCounts: List<Double>
    ) {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-1.0, 0.0, -1.5, -0.5, 0.5, -1.0, 0.0) + listOf(x),
                TransformVar.Y to listOf(-1.0, -1.0, 0.0, 0.0, 0.0, 1.0, 1.0).map { it * HEX_HEIGHT_INV } + listOf(y)
            ),
            expected = mapOf(
                Stats.X to listOf(-0.5, 0.5,
                                  -1.0, 0.0, 1.0,
                                  -0.5, 0.5),
                Stats.Y to listOf(-HEX_HEIGHT_INV, -HEX_HEIGHT_INV,
                                  0.0, 0.0, 0.0,
                                  HEX_HEIGHT_INV, HEX_HEIGHT_INV),
                Stats.COUNT to expectedCounts,
                Stats.WIDTH to List(7) { 1.0 },
                Stats.HEIGHT to List(7) { 1.0 }
            ),
            binWidthX = 1.0,
            binWidthY = 1.0
        )
    }

    private fun checkStretchedHexagonsRegression(
        xStretch: Double,
        yStretch: Double
    ) {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(0.0, 0.0, 1.0, 1.0).map { it * xStretch },
                TransformVar.Y to listOf(0.0, 1.0, 0.0, 1.0).map { it * yStretch }
            ),
            expected = mapOf(
                Stats.X to listOf(0.0, 1.0).map { it * xStretch },
                Stats.Y to listOf(0.0, 0.0),
                Stats.COUNT to listOf(2.0, 2.0),
                Stats.WIDTH to List(2) { xStretch },
                Stats.HEIGHT to List(2) { 2.0 * yStretch }
            ),
            binWidthX = xStretch,
            binWidthY = 2.0 * yStretch
        )
    }

    private fun checkFloatingPointRoundingErrorRegression(
        xStretch: Double,
        yStretch: Double,
        epsilon: Double = DEF_EPSILON
    ) {
        checkValues(
            data = mapOf(
                TransformVar.X to listOf(-1.0, 1.0, 0.0).map { it * xStretch },
                TransformVar.Y to listOf(-6.0 * HEX_HEIGHT, 0.0, HEX_HEIGHT).map { it * yStretch }
            ),
            expected = mapOf(
                Stats.X to listOf(-1.0, 1.0, 0.5).map { it * xStretch },
                Stats.Y to listOf(-6.0 * HEX_HEIGHT, 0.0, HEX_HEIGHT * 3.0 / 2.0).map { it * yStretch },
                Stats.COUNT to listOf(1.0, 1.0, 1.0),
                Stats.WIDTH to List(3) { xStretch },
                Stats.HEIGHT to List(3) { 2.0 * yStretch }
            ),
            binWidthX = xStretch,
            binWidthY = 2.0 * yStretch,
            epsilon = epsilon
        )
    }

    private fun checkValues(
        data: Map<DataFrame.Variable, List<Double?>>,
        expected: Map<DataFrame.Variable, List<Double>>,
        binCountX: Int? = null,
        binCountY: Int? = null,
        binWidthX: Double? = null,
        binWidthY: Double? = null,
        drop: Boolean = true,
        epsilon: Double = DEF_EPSILON
    ) {
        val df = dataFrame(data)
        val stat = BinHexStat(
            binCountX = binCountX ?: BinHexStat.DEF_BINS,
            binCountY = binCountY ?: BinHexStat.DEF_BINS,
            binWidthX = binWidthX,
            binWidthY = binWidthY,
            drop = drop
        )
        val statDf = stat.apply(df, statContext(df))

        for (variable in expected.keys) {
            checkStatVarValues(statDf, variable, expected.getValue(variable), epsilon = epsilon)
        }
    }

    companion object {
        private const val DEF_EPSILON = 1e-12
        private val HEX_HEIGHT = 2.0 / sqrt(3.0) // height of right hexagon with width = 1
        private val HEX_HEIGHT_INV = sqrt(3.0) / 2.0
    }
}