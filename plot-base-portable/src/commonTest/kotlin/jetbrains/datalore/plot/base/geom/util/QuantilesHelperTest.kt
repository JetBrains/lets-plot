/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import kotlin.test.Test
import kotlin.test.assertEquals

class QuantilesHelperTest {
    @Test
    fun testEmptyValues() {
        compareWithExpectedLines(
            listOf(),
            listOf(),
            listOf()
        )
    }

    @Test
    fun testWithoutQuantiles() {
        compareWithExpectedLines(
            listOf(),
            listOf(0.1, 0.2),
            listOf(1.0, 1.0)
        )
    }

    @Test
    fun testWith1Quantile() {
        compareWithExpectedLines(
            listOf(0.5),
            listOf(0.1, 0.1, 0.2),
            listOf(0.5, 1.0, 1.0)
        )
    }

    @Test
    fun testWithBorderQuantiles() {
        compareWithExpectedLines(
            listOf(0.0, 0.5, 1.0),
            listOf(0.1, 0.1, 0.2, 0.2, 0.3),
            listOf(0.0, 0.5, 0.5, 1.0, 1.0)
        )
    }

    @Test
    fun testWith1QuantileAndGroups() {
        compareWithExpectedLines(
            listOf(0.5),
            listOf(0.1, 0.1, 0.2, -0.2, -0.2, -0.1),
            listOf(0.5, 1.0, 1.0, 0.5, 1.0, 1.0),
            listOf(0, 0, 0, 1, 1, 1)
        )
    }

    @Test
    fun testWithBorderQuantilesAndGroups() {
        compareWithExpectedLines(
            listOf(0.0, 0.5, 1.0),
            listOf(0.1, 0.1, 0.2, 0.2, 0.3, -0.3, -0.3, -0.2, -0.2, -0.1),
            listOf(0.0, 0.5, 0.5, 1.0, 1.0, 0.0, 0.5, 0.5, 1.0, 1.0),
            listOf(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)
        )
    }

    private fun compareWithExpectedLines(
        quantiles: List<Double>,
        xValues: List<Double>,
        quantileValues: List<Double>,
        groupValues: List<Int>? = null
    ) {
        val dataPoints = getDataPoints(xValues, quantileValues, groupValues)
        val pos = PositionAdjustments.identity()
        val coord = getCoordinateSystem(xValues)
        val quantilesHelper = QuantilesHelper(pos, coord, BogusContext, quantiles)
        val quantileLines = quantilesHelper.getQuantileLineElements(
            dataPoints,
            Aes.X,
            { p -> DoubleVector(p.x()!!, 0.0) },
            { p -> DoubleVector(p.x()!!, 0.0) }
        )
        val expectedLinesNumber = if (groupValues != null) {
            groupValues.toSet().size * quantiles.size
        } else {
            quantiles.size
        }
        val actualLinesNumber = quantileLines.map { it.x1().get() }.toSet().size
        assertEquals(expectedLinesNumber, actualLinesNumber, "Count of quantile line elements should be equal to size of quantiles parameter")
    }

    private fun getDataPoints(
        xValues: List<Double>,
        quantileValues: List<Double>,
        groupValues: List<Int>?
    ): Iterable<DataPointAesthetics> {
        val builder = AestheticsBuilder(xValues.size)
            .x(AestheticsBuilder.list(xValues))
            .aes(Aes.QUANTILE, AestheticsBuilder.list(quantileValues))
        if (groupValues != null) builder.group(AestheticsBuilder.list(groupValues))
        return builder.build().dataPoints()
    }

    private fun getCoordinateSystem(
        xValues: List<Double>,
        extend: Double = 1.0
    ): CoordinateSystem {
        val xMin = xValues.minOrNull() ?: 0.0
        val xMax = xValues.maxOrNull() ?: 0.0
        return Coords.DemoAndTest.create(
            DoubleSpan(xMin - extend, xMax + extend),
            DoubleSpan(-extend, extend),
            DoubleVector(1.0, 1.0)
        )
    }
}