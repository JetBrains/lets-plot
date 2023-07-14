/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import kotlin.test.Test
import kotlin.test.assertEquals

class QuantilesHelperTest {
    @Test
    fun testEmptyValues() {
        checkSplit(
            listOf(),
            listOf(),
            listOf()
        )
        checkLinesNumber(
            listOf(),
            listOf(),
            listOf()
        )
    }

    @Test
    fun testWithoutQuantiles() {
        checkLinesNumber(
            listOf(),
            listOf(0.1, 0.2),
            listOf(1.0, 1.0)
        )
    }

    @Test
    fun testWith1Quantile() {
        checkSplit(
            listOf(0.0, 1.0, 2.0),
            listOf(1.0, 1.0, 1.0),
            listOf(listOf(0.0, 1.0, 2.0))
        )
        checkLinesNumber(
            listOf(0.5),
            listOf(0.1, 0.1, 0.2),
            listOf(0.5, 1.0, 1.0)
        )
    }

    @Test
    fun testWithoutDifferentColors() {
        checkSplit(
            listOf(0.0, 0.0, 1.0, 1.0, 2.0),
            listOf(0.25, 0.5, 0.5, 0.75, 0.75),
            listOf(listOf(0.0, 0.0, 1.0, 1.0, 2.0)),
        )
        checkSplit(
            listOf(0.0, 0.0, 1.0, 1.0, 2.0),
            listOf(0.25, 0.5, 0.5, 0.75, 0.75),
            listOf(listOf(0.0, 0.0, 1.0, 1.0, 2.0)),
            listOf("#ff0000", "#ff0000", "#ff0000", "#ff0000", "#ff0000")
        )
    }

    @Test
    fun testWithBorderQuantiles() {
        checkSplit(
            listOf(0.0, 0.0, 1.0, 1.0, 2.0),
            listOf(0.0, 0.5, 0.5, 1.0, 1.0),
            listOf(listOf(0.0), listOf(0.0, 1.0), listOf(1.0, 2.0)),
            listOf("#ff0000", "#ff0000", "#00ff00", "#00ff00", "#0000ff")
        )
        checkLinesNumber(
            listOf(0.0, 0.5, 1.0),
            listOf(0.1, 0.1, 0.2, 0.2, 0.3),
            listOf(0.0, 0.5, 0.5, 1.0, 1.0)
        )
    }

    @Test
    fun testWith1QuantileAndGroups() {
        checkLinesNumber(
            listOf(0.5),
            listOf(0.1, 0.1, 0.2, -0.2, -0.2, -0.1),
            listOf(0.5, 1.0, 1.0, 0.5, 1.0, 1.0),
            listOf(0, 0, 0, 1, 1, 1)
        )
    }

    @Test
    fun testWithBorderQuantilesAndGroups() {
        checkLinesNumber(
            listOf(0.0, 0.5, 1.0),
            listOf(0.1, 0.1, 0.2, 0.2, 0.3, -0.3, -0.3, -0.2, -0.2, -0.1),
            listOf(0.0, 0.5, 0.5, 1.0, 1.0, 0.0, 0.5, 0.5, 1.0, 1.0),
            listOf(0, 0, 0, 0, 0, 1, 1, 1, 1, 1)
        )
    }

    private fun checkSplit(
        xValues: List<Double>,
        quantileValues: List<Double>,
        expectedXValues: List<List<Double>>,
        colorHexValues: List<String>? = null
    ) {
        val dataPoints = getDataPoints(xValues, quantileValues, colorHexValues = colorHexValues)
        val pos = PositionAdjustments.identity()
        val coord = getCoordinateSystem(xValues)
        val quantilesHelper = QuantilesHelper(pos, coord, BogusContext, emptyList())
        quantilesHelper.splitByQuantiles(dataPoints, Aes.X).forEachIndexed { i, points ->
            for (j in points.indices) {
                assertEquals(expectedXValues[i][j], points[j].x(), "At $i-th bunch, $j-th point, x-values should be equal")
            }
        }
    }

    private fun checkLinesNumber(
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
        groupValues: List<Int>? = null,
        colorHexValues: List<String>? = null
    ): Iterable<DataPointAesthetics> {
        val builder = AestheticsBuilder(xValues.size)
            .x(AestheticsBuilder.list(xValues))
            .aes(Aes.QUANTILE, AestheticsBuilder.list(quantileValues))
        if (groupValues != null) builder.group(AestheticsBuilder.list(groupValues))
        if (colorHexValues != null) builder.color(AestheticsBuilder.list(colorHexValues.map { Color.parseHex(it) }))
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