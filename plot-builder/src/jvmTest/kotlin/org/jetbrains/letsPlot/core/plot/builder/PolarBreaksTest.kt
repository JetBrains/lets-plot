/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.plot.base.scale.ScaleBreaks
import org.jetbrains.letsPlot.core.plot.builder.PolarAxisUtil.breaksData
import org.jetbrains.letsPlot.core.plot.builder.PolarBreaksTest.AxisKind.ANGLE
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.guide.AxisComponent
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PolarBreaksTest {

    @Test
    fun simple() {
        val angleBreaks = computeBreaks(axisKind = ANGLE)

        // Breaks start at top center
        assertDoubleVectorEquals(x = 0, y = -219, angleBreaks.majorBreaks[0])
        assertDoubleVectorEquals(x = 208, y = -67, angleBreaks.majorBreaks[1])
        assertDoubleVectorEquals(x = 128, y = 177, angleBreaks.majorBreaks[2])
        assertDoubleVectorEquals(x = -128, y = 177, angleBreaks.majorBreaks[3])
        assertDoubleVectorEquals(x = -208, y = -67, angleBreaks.majorBreaks[4])
        assertDoubleVectorEquals(x = 0, y = -219, angleBreaks.majorBreaks[5])

        val radiusBreaks = computeBreaks(axisKind = AxisKind.RADIUS)

        assertDoubleVectorEquals(x = 252, y = 252, radiusBreaks.majorBreaks[0])
        assertDoubleVectorEquals(x = 252, y = 200, radiusBreaks.majorBreaks[1])
        assertDoubleVectorEquals(x = 252, y = 148, radiusBreaks.majorBreaks[2])
        assertDoubleVectorEquals(x = 252, y = 96, radiusBreaks.majorBreaks[3])
        assertDoubleVectorEquals(x = 252, y = 44, radiusBreaks.majorBreaks[4])
    }

    @Test
    fun radiusBreaksAreNotAffectedByStartAngle() {
        val angleBreaks = computeBreaks(startAngleDeg = 45.0)

        assertDoubleVectorEquals(x = 155, y = -155, angleBreaks.majorBreaks[0])
        assertDoubleVectorEquals(x = 195, y=100, angleBreaks.majorBreaks[1])
        assertDoubleVectorEquals(x = -34, y = 216, angleBreaks.majorBreaks[2])
        assertDoubleVectorEquals(x = -216, y = 34, angleBreaks.majorBreaks[3])
        assertDoubleVectorEquals(x = -100, y = -195, angleBreaks.majorBreaks[4])
        assertDoubleVectorEquals(x = 155, y = -155, angleBreaks.majorBreaks[5])

        val radiusBreaks = computeBreaks(axisKind = AxisKind.RADIUS)

        assertDoubleVectorEquals(x = 252, y = 252, radiusBreaks.majorBreaks[0])
        assertDoubleVectorEquals(x = 252, y = 200, radiusBreaks.majorBreaks[1])
        assertDoubleVectorEquals(x = 252, y = 148, radiusBreaks.majorBreaks[2])
        assertDoubleVectorEquals(x = 252, y = 96, radiusBreaks.majorBreaks[3])
        assertDoubleVectorEquals(x = 252, y = 44, radiusBreaks.majorBreaks[4])
    }

    @Test
    fun shouldNotFailWithZeroClientSize() {
        // Not failing, breaks count is equals to domain breaks count

        val angleBreaks = computeBreaks(clientSize = DoubleVector(0.0, 0.0))
        assertEquals(6, angleBreaks.majorBreaks.size)
        assertTrue(angleBreaks.majorBreaks.all { it == DoubleVector.ZERO })

        val radiusBreaks = computeBreaks(clientSize = DoubleVector(0.0, 0.0), axisKind = AxisKind.RADIUS)
        assertEquals(5, radiusBreaks.majorBreaks.size)
        assertTrue(radiusBreaks.majorBreaks.all { it == DoubleVector.ZERO })
    }

    @Test
    fun shouldNotFailOnInvalidDomainValues() {
        val angleBreaks = computeBreaks(breaks = listOf(-5.0, -4.0, -3.0, -12.0, -1.0, 0.0))

        assertDoubleVectorEquals(x = 0, y = -219, angleBreaks.majorBreaks[0])
        assertDoubleVectorEquals(x = 208, y = -67, angleBreaks.majorBreaks[1])
        assertDoubleVectorEquals(x = 128, y = 177, angleBreaks.majorBreaks[2])
        assertDoubleVectorEquals(x = -208, y = -67, angleBreaks.majorBreaks[3])
        assertDoubleVectorEquals(x = 0, y = -219, angleBreaks.majorBreaks[4])
    }

    private fun assertDoubleVectorEquals(expected: DoubleVector, actual: DoubleVector, tolerance: Double = 1.0) {
        assertEquals(expected.x, actual.x, tolerance, "Expected: $expected, actual: $actual\n")
        assertEquals(expected.y, actual.y, tolerance, "Expected: $expected, actual: $actual\n")
    }

    private fun assertDoubleVectorEquals(x: Number, y: Number, actual: DoubleVector, tolerance: Double = 1.0) {
        assertDoubleVectorEquals(DoubleVector(x, y), actual, tolerance)
    }

    private fun computeBreaks(
        clientSize: DoubleVector = DoubleVector(504.0, 504.0),
        startAngleDeg: Double = 0.0,
        axisKind: AxisKind = ANGLE,
        breaks: List<Double>? = null
    ): PolarAxisUtil.PolarBreaksData {
        val dataDomain = DoubleRectangle.XYWH(-5.0, 10.0, 5.0, 8.0)
        @Suppress("NAME_SHADOWING")
        val breaks = breaks ?: when (axisKind) {
            ANGLE -> listOf(-5.0, -4.0, -3.0, -2.0, -1.0, 0.0)
            else -> listOf(10.0, 12.0, 14.0, 16.0, 18.0)
        }

        val polarCoordProvider = PolarCoordProvider(
            xLim = Pair(null, null),
            yLim = Pair(null, null),
            flipped = false,
            start = toRadians(startAngleDeg),
            clockwise = true,
            transformBkgr = false
        )
        val adjustedDomain = polarCoordProvider.adjustDomain(dataDomain)
        val gridDomain = polarCoordProvider.gridDomain(adjustedDomain)
        val coordinateSystem = polarCoordProvider.createCoordinateSystem(adjustedDomain, clientSize)

        if (axisKind == ANGLE) {
            return breaksData(
                scaleBreaks = ScaleBreaks(
                    domainValues = breaks,
                    transformedValues = breaks,
                    labels = breaks.map { it.toInt().toString() },
                ),
                coord = coordinateSystem,
                gridDomain = gridDomain,
                flipAxis = false,
                orientation = Orientation.BOTTOM,
                labelAdjustments = AxisComponent.TickLabelAdjustments(Orientation.BOTTOM),
            )
        } else {
            return breaksData(
                scaleBreaks = ScaleBreaks(
                    domainValues = breaks,
                    transformedValues = breaks,
                    labels = breaks.map { it.toInt().toString() },
                ),
                coord = coordinateSystem,
                gridDomain = gridDomain,
                flipAxis = false,
                orientation = Orientation.LEFT,
                labelAdjustments = AxisComponent.TickLabelAdjustments(Orientation.LEFT),
            )
        }
    }

    enum class AxisKind {
        ANGLE, RADIUS
    }
}
