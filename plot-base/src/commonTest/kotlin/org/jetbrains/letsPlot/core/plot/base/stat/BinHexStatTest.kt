/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.intern.indicesOf
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import kotlin.math.sqrt
import kotlin.test.Test

class BinHexStatTest : BaseStatTest() {
    @Test
    fun basicTest() {
        check(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEX_HEIGHT_INV }
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(-10.0, 10.0, 0.0, 20.0)),
                Stats.Y to checkValues(listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEX_HEIGHT_INV }),
                Stats.COUNT to checkValues(listOf(1.0, 1.0, 2.0, 1.0))
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
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0),
                TransformVar.Y to listOf(0.0)
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(0.0)),
                Stats.Y to checkValues(listOf(0.0)),
                Stats.COUNT to checkValues(listOf(1.0))
            ),
            binWidthX = 1.0,
            binWidthY = 1.0
        )
    }

    @Test
    fun withNanValues() {
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0, null, 1.0),
                TransformVar.Y to listOf(0.0, 1.0, null)
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(0.0), filterFinite = true),
                Stats.Y to checkValues(listOf(0.0), filterFinite = true),
                Stats.COUNT to checkValues(listOf(1.0), filterFinite = true)
            ),
            binWidthX = 1.0,
            binWidthY = 1.0
        )
    }

    @Test
    fun aesWeight() {
        check(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEX_HEIGHT_INV },
                TransformVar.WEIGHT to listOf(1.0, 2.0, 4.0, 8.0, 16.0)
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(-10.0, 10.0, 0.0, 20.0)),
                Stats.Y to checkValues(listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEX_HEIGHT_INV }),
                Stats.COUNT to checkValues(listOf(1.0, 4.0, 10.0, 16.0))
            ),
            binWidthX = 20.0,
            binWidthY = 20.0
        )
    }

    @Test
    fun paramBinsTest() {
        check(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 10.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 10.0).map { it * HEX_HEIGHT_INV }
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(-10.0, 10.0, 0.0, 20.0)),
                Stats.Y to checkValues(listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEX_HEIGHT_INV }),
                Stats.COUNT to checkValues(listOf(1.0, 1.0, 1.0, 1.0))
            ),
            binCountX = 2,
            binCountY = 2
        )
    }

    @Test
    fun paramBinwidthTest() {
        check(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEX_HEIGHT_INV }
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(-10.0, 10.0, -10.0, 10.0), filterFinite = true),
                Stats.Y to checkValues(listOf(-10.0, -10.0, 10.0, 10.0).map { it * HEX_HEIGHT_INV }, filterFinite = true),
                Stats.COUNT to checkValues(listOf(1.0, 1.0, 1.0, 2.0), filterFinite = true)
            ),
            binWidthX = 10.0,
            binWidthY = 10.0
        )
    }

    @Test
    fun paramDropTest() {
        check(
            data = mapOf(
                TransformVar.X to listOf(-10.0, -10.0, 10.0, 9.0, 11.0),
                TransformVar.Y to listOf(-10.0, 10.0, -10.0, 9.0, 11.0).map { it * HEX_HEIGHT_INV }
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(-10.0, 0.0, 10.0, 20.0,
                                               -5.0, 5.0, 15.0, 25.0,
                                              -10.0, 0.0, 10.0, 20.0,
                                               -5.0, 5.0, 15.0, 25.0)),
                Stats.Y to checkValues(listOf(-10.0, -10.0, -10.0, -10.0,
                                                0.0,   0.0,   0.0,   0.0,
                                               10.0,  10.0,  10.0,  10.0,
                                               20.0,  20.0,  20.0,  20.0).map { it * HEX_HEIGHT_INV }),
                Stats.COUNT to checkValues(listOf(1.0, 0.0, 1.0, 0.0,
                                                  0.0, 0.0, 0.0, 0.0,
                                                  1.0, 0.0, 2.0, 0.0,
                                                  0.0, 0.0, 0.0, 0.0))
            ),
            binWidthX = 10.0,
            binWidthY = 10.0,
            drop = false
        )
    }

    @Test
    fun pointOnBorderTest() {
        val halfHexHeight = 0.5 * HEX_HEIGHT
        checkBorderValues(0.0, halfHexHeight, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(0.25, 0.75 * halfHexHeight, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(0.5, 0.5 * halfHexHeight, listOf(1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0))
        checkBorderValues(0.5, 0.0, listOf(1.0, 1.0, 1.0, 1.0, 2.0, 1.0, 1.0))
        checkBorderValues(0.5, -0.5 * halfHexHeight, listOf(1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0))
        checkBorderValues(0.25, -0.75 * halfHexHeight, listOf(1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0))
        checkBorderValues(0.0, -halfHexHeight, listOf(1.0, 2.0, 1.0, 1.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.25, -0.75 * halfHexHeight, listOf(2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.5, -0.5 * halfHexHeight, listOf(2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.5, 0.0, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.5, 0.5 * halfHexHeight, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
        checkBorderValues(-0.25, 0.75 * halfHexHeight, listOf(1.0, 1.0, 1.0, 2.0, 1.0, 1.0, 1.0))
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
        checkFloatingPointRoundingErrorRegression(1e-15, 1e-15)
        checkFloatingPointRoundingErrorRegression(1e-15, 1e15)
        checkFloatingPointRoundingErrorRegression(1e15, 1e-15)
        checkFloatingPointRoundingErrorRegression(1e15, 1e15)
    }

    private fun checkBorderValues(
        x: Double,
        y: Double,
        expectedCounts: List<Double>
    ) {
        check(
            data = mapOf(
                TransformVar.X to listOf(-1.0, 0.0, -1.5, -0.5, 0.5, -1.0, 0.0) + listOf(x),
                TransformVar.Y to listOf(-1.0, -1.0, 0.0, 0.0, 0.0, 1.0, 1.0).map { it * HEX_HEIGHT_INV } + listOf(y)
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(-0.5, 0.5,
                                              -1.0, 0.0, 1.0,
                                              -0.5, 0.5),
                                       filterFinite = true),
                Stats.Y to checkValues(listOf(-HEX_HEIGHT_INV, -HEX_HEIGHT_INV,
                                               0.0, 0.0, 0.0,
                                               HEX_HEIGHT_INV, HEX_HEIGHT_INV),
                                       filterFinite = true),
                Stats.COUNT to checkValues(expectedCounts, filterFinite = true)
            ),
            binWidthX = 1.0,
            binWidthY = 1.0
        )
    }

    private fun checkStretchedHexagonsRegression(
        xStretch: Double,
        yStretch: Double
    ) {
        check(
            data = mapOf(
                TransformVar.X to listOf(0.0, 0.0, 1.0, 1.0).map { it * xStretch },
                TransformVar.Y to listOf(0.0, 1.0, 0.0, 1.0).map { it * yStretch }
            ),
            expected = mapOf(
                Stats.X to checkValues(listOf(0.0, 1.0).map { it * xStretch }, filterFinite = true),
                Stats.Y to checkValues(listOf(0.0, 0.0), filterFinite = true),
                Stats.COUNT to checkValues(listOf(2.0, 2.0), filterFinite = true)
            ),
            binWidthX = xStretch,
            binWidthY = 2.0 * yStretch
        )
    }

    private fun checkFloatingPointRoundingErrorRegression(
        xStretch: Double,
        yStretch: Double
    ) {
        check(
            data = mapOf(
                TransformVar.X to listOf(-1.0, 1.0, 0.0).map { it * xStretch },
                TransformVar.Y to listOf(-6.0 * HEX_HEIGHT, 0.0, HEX_HEIGHT).map { it * yStretch }
            ),
            expected = mapOf(
                Stats.X to { statDf, variable -> checkStatVarSize(filterFinite(statDf), variable, 3) },
                Stats.Y to { statDf, variable -> checkStatVarSize(filterFinite(statDf), variable, 3) },
                Stats.COUNT to checkValues(listOf(1.0, 1.0, 1.0), filterFinite = true)
            ),
            binWidthX = xStretch,
            binWidthY = 2.0 * yStretch
        )
    }

    private fun checkValues(
        expectedValues: List<Double>,
        epsilon: Double = DEF_EPSILON,
        filterFinite: Boolean = false
    ): (DataFrame, DataFrame.Variable) -> Unit {
        return { statDf, variable ->
            val df = if (filterFinite) filterFinite(statDf) else statDf
            checkStatVarValues(df, variable, expectedValues, epsilon = epsilon)
        }
    }

    private fun check(
        data: Map<DataFrame.Variable, List<Double?>>,
        expected: Map<DataFrame.Variable, (DataFrame, DataFrame.Variable) -> Unit>,
        binCountX: Int? = null,
        binCountY: Int? = null,
        binWidthX: Double? = null,
        binWidthY: Double? = null,
        drop: Boolean = true
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
            expected.getValue(variable)(statDf, variable)
        }
    }

    private fun filterFinite(df: DataFrame): DataFrame {
        val rows = (0 until df.rowCount()).toMutableSet()
        df.variables().forEach { variable ->
            if (df.isNumeric(variable)) {
                rows -= df.getNumeric(variable).indicesOf { !SeriesUtil.isFinite(it) }.toSet()
            }
        }
        return df.slice(rows)
    }

    companion object {
        private const val DEF_EPSILON = 1e-12
        private val HEX_HEIGHT = 2.0 / sqrt(3.0) // height of right hexagon with width = 1
        private val HEX_HEIGHT_INV = sqrt(3.0) / 2.0
    }
}