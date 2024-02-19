/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.builder.PolarAxisUtil.breaksData
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordinateSystem
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.junit.Ignore
import org.junit.Test
import kotlin.test.assertEquals
@Ignore
class BreaksDataTest {
    @Test
    fun simple() {
        val polarCoordProvider = PolarCoordProvider(
            xLim = Pair(null, null),
            yLim = Pair(null, null),
            flipped = false, start = 0.0, clockwise = true)
        val dataDomain = DoubleRectangle.XYWH(-5.0, 10.0, 5.0, 8.0)
        val adjustedDomain = polarCoordProvider.adjustDomain(dataDomain)
        val gridDomain = polarCoordProvider.gridDomain(adjustedDomain)
        val clientSize = DoubleVector(504.0, 504.0)
        val coordinateSystem = polarCoordProvider.createCoordinateSystem(adjustedDomain, clientSize) as PolarCoordinateSystem
        val breaksData = breaksData(
            scaleBreaks = ScaleBreaks(
                domainValues = listOf(-5.0, -4.0, -3.0, -2.0, -1.0, 0.0),
                transformedValues = listOf(-5.0, -4.0, -3.0, -2.0, -1.0, 0.0),
                labels = listOf("-5", "-4", "-3", "-2", "-1", "0"),
            ),
            coord = coordinateSystem,
            gridDomain = gridDomain,
            flipAxis = false,
            orientation = Orientation.BOTTOM,
            labelAdjustments = AxisComponent.TickLabelAdjustments(Orientation.BOTTOM),
        )

        // Breaks start at top center
        val topCenter = DoubleVector(0, -219)

        assertDoubleVectorEquals(topCenter, breaksData.majorBreaks[0])
        assertDoubleVectorEquals(DoubleVector(208, -67), breaksData.majorBreaks[1])
        assertDoubleVectorEquals(DoubleVector(128, 177), breaksData.majorBreaks[2])
        assertDoubleVectorEquals(DoubleVector(-128, 177), breaksData.majorBreaks[3])
        assertDoubleVectorEquals(DoubleVector(-208, -67), breaksData.majorBreaks[4])
        assertDoubleVectorEquals(topCenter, breaksData.majorBreaks[5])
    }

    private fun assertDoubleVectorEquals(expected: DoubleVector, actual: DoubleVector, tolerance: Double = 1.0) {
        assertEquals(expected.x, actual.x, tolerance, "Expected: $expected, actual: $actual\n")
        assertEquals(expected.y, actual.y, tolerance, "Expected: $expected, actual: $actual\n")
    }
}
