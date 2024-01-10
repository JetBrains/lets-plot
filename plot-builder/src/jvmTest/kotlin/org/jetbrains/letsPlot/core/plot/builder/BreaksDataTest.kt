/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.builder.AxisUtil.breaksData
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.junit.Test
import kotlin.test.assertEquals

class BreaksDataTest {
    @Test
    fun simple() {
        val polarCoordProvider = PolarCoordProvider(xLim = null, yLim = null, flipped = false, start = 0.0, clockwise = true)
        val adjustedDomain = DoubleRectangle.XYWH(-5.0, 10.0, 5.0, 8.625)
        val clientSize = DoubleVector(504.0, 504.0)
        val coordinateSystem = polarCoordProvider.createCoordinateSystem(adjustedDomain, clientSize)
        val breaksData = breaksData(
            scaleBreaks = ScaleBreaks(
                domainValues = listOf(-5.0, -4.0, -3.0, -2.0, -1.0, 0.0),
                transformedValues = listOf(-5.0, -4.0, -3.0, -2.0, -1.0, 0.0),
                labels = listOf("-5", "-4", "-3", "-2", "-1", "0"),
            ),
            coord = coordinateSystem,
            domain = adjustedDomain,
            flipAxis = false,
            orientation = Orientation.BOTTOM,
            axisTheme = DefaultTheme.minimal2().horizontalAxis(flipAxis = false),
            labelAdjustments = AxisComponent.TickLabelAdjustments(Orientation.BOTTOM)
        )

        // Breaks start at top center
        val topCenter = DoubleVector(clientSize.x / 2, 0.0)

        assertDoubleVectorEquals(topCenter, breaksData.majorBreaks[0])
        assertDoubleVectorEquals(DoubleVector(491, 174), breaksData.majorBreaks[1])
        assertDoubleVectorEquals(DoubleVector(400, 455), breaksData.majorBreaks[2])
        assertDoubleVectorEquals(DoubleVector(103, 455), breaksData.majorBreaks[3])
        assertDoubleVectorEquals(DoubleVector(12, 174), breaksData.majorBreaks[4])
        assertDoubleVectorEquals(topCenter, breaksData.majorBreaks[5])
    }

    private fun assertDoubleVectorEquals(expected: DoubleVector, actual: DoubleVector, tolerance: Double = 1.0) {
        assertEquals(expected.x, actual.x, tolerance)
        assertEquals(expected.y, actual.y, tolerance)
    }
}
