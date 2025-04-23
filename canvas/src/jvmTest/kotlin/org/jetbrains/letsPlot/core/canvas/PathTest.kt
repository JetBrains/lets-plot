/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.RecursiveComparisonAssert
import org.assertj.core.util.DoubleComparator
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.Path.Companion.approximateEllipseWithBezierCurve
import kotlin.test.Test

class PathTest {
    @Test
    fun `negative radius returns center point`() {
        val cpts = approximateEllipseWithBezierCurve(
            x = 100.0,
            y = 100.0,
            radiusX = -50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngleDeg = 0.0,
            endAngleDeg = 360.0,
            anticlockwise = false
        )

        assertControlPoints(cpts, DoubleVector(100.0, 100.0))
    }

    @Test
    fun `bezier curve from 90degree circle arc`() {
        val cpts = approximateEllipseWithBezierCurve(
            x = 100.0,
            y = 100.0,
            radiusX = 50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngleDeg = 0.0,
            endAngleDeg = 90.0,
            anticlockwise = false
        )

        assertControlPoints(
            cpts,
            DoubleVector(150.0, 100.0),
            DoubleVector(150, 127.614),
            DoubleVector(127.614, 150.0),
            DoubleVector(100.0, 150.0),
        )
    }

    @Test
    fun `bezier curve from 180degree circle arc`() {
        val cpts = approximateEllipseWithBezierCurve(
            x = 100.0,
            y = 100.0,
            radiusX = 50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngleDeg = 0.0,
            endAngleDeg = 180.0,
            anticlockwise = false
        )

        assertControlPoints(
            cpts,
            DoubleVector(150.0, 100.0),
            DoubleVector(150, 127.614),
            DoubleVector(127.614, 150.0),
            DoubleVector(100.0, 150.0),
            DoubleVector(72.38576250846033, 150.0),
            DoubleVector(50.0, 127.61423749153968),
            DoubleVector(50.0, 100.0),
        )
    }

    @Test
    fun `bezier curve from negative angle circle arc`() {
        val cpts = approximateEllipseWithBezierCurve(
            x = 150.0,
            y = 150.0,
            radiusX = 100.0,
            radiusY = 100.0,
            rotation = 0.0,
            startAngleDeg = -90.0,
            endAngleDeg = -180.0,
            anticlockwise = true
        )

        assertControlPoints(
            cpts,
            DoubleVector(150.0, 50.0),
            DoubleVector(94.7715, 50.0),
            DoubleVector(50.0, 94.7715),
            DoubleVector(50.0, 150.0),
        )
    }

    @Test
    fun `bezier curve from full circle`() {
        val cpts = approximateEllipseWithBezierCurve(
            x = 100.0,
            y = 100.0,
            radiusX = 50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngleDeg = 0.0,
            endAngleDeg = 360.0,
            anticlockwise = false
        )

        assertControlPoints(cpts,
            DoubleVector(150.0, 100.0),
            DoubleVector(150, 127.614),
            DoubleVector(127.614, 150.0),
            DoubleVector(100.0, 150.0),
            DoubleVector(72.38576250846033, 150.0),
            DoubleVector(50.0, 127.61423749153968),
            DoubleVector(50.0, 100.0),
            DoubleVector(50, 72.38576250846033),
            DoubleVector(72.38576250846033, 50.0),
            DoubleVector(100.0, 50.0),
            DoubleVector(127.61423749153968, 50.0),
            DoubleVector(150.0, 72.38576250846033),
            DoubleVector(150.0, 100.0),
        )
    }

    private fun assertControlPoints(
        actual: List<DoubleVector>,
        vararg expected: DoubleVector
    ): RecursiveComparisonAssert<*> {
        return assertThat(actual)
            .usingRecursiveComparison()
            .withComparatorForType(DoubleComparator(0.01), Double::class.javaObjectType)
            .isEqualTo(expected.toList())
    }
}
