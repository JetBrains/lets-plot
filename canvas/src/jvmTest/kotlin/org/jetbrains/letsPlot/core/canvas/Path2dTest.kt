/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.RecursiveComparisonAssert
import org.assertj.core.util.DoubleComparator
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.math.PI
import kotlin.test.Test

class Path2dTest {
    fun arcControlPoints(
        x: Double,
        y: Double,
        radiusX: Double,
        radiusY: Double,
        rotation: Double,
        startAngle: Double,
        endAngle: Double,
        anticlockwise: Boolean
    ): List<DoubleVector> {
        val arc = Path2d.arc(x, y, radiusX, radiusY, rotation, startAngle, endAngle, anticlockwise)
        return listOfNotNull(arc.start) + arc.controlPoints
    }

    @Test
    fun `ellipse with zero radius returns center point`() {
        val cpts = arcControlPoints(
            x = 100.0,
            y = 100.0,
            radiusX = 0.0,
            radiusY = 0.0,
            rotation = 0.0,
            startAngle = 0.0,
            endAngle = 2 * PI,
            anticlockwise = false
        )

        assertControlPoints(cpts, DoubleVector(100.0, 100.0))
    }
    
    @Test
    fun `negative radius returns center point`() {
        val cpts = arcControlPoints(
            x = 100.0,
            y = 100.0,
            radiusX = -50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngle = 0.0,
            endAngle = 2 * PI,
            anticlockwise = false
        )

        assertControlPoints(cpts, DoubleVector(100.0, 100.0))
    }

    @Test
    fun `bezier curve from 90degree circle arc`() {
        val cpts = arcControlPoints(
            x = 100.0,
            y = 100.0,
            radiusX = 50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngle = 0.0,
            endAngle = PI / 2,
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
        val cpts = arcControlPoints(
            x = 100.0,
            y = 100.0,
            radiusX = 50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngle = 0.0,
            endAngle = PI,
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
        val cpts = arcControlPoints(
            x = 150.0,
            y = 150.0,
            radiusX = 100.0,
            radiusY = 100.0,
            rotation = 0.0,
            startAngle = -PI / 2,
            endAngle = -PI,
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
        val cpts = arcControlPoints(
            x = 100.0,
            y = 100.0,
            radiusX = 50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngle = 0.0,
            endAngle = 2 * PI,
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
