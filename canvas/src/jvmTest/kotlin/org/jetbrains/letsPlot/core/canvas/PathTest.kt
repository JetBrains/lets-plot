/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.canvas.ContextState.Ellipse
import kotlin.test.Test

class PathTest {
    @Test
    fun `convert 90degree ellipse arc to bezier control points`() {
        val ellipse = Ellipse(
            x = 100.0,
            y = 100.0,
            radiusX = 50.0,
            radiusY = 50.0,
            rotation = 0.0,
            startAngleDeg = 0.0,
            endAngleDeg = 90.0,
            anticlockwise = false,
            transform = AffineTransform.IDENTITY,
        )

        val cpts = ellipse.toBezierControlPoints()
        assertThat(cpts).isEqualTo(Ellipse.BezierSegment(
            cp1 = DoubleVector(50.0, 0.0),
            cp2 = DoubleVector(50, 27.614),
            cp3 = DoubleVector(27.614, 50.0),
            cp4 = DoubleVector(0.0, 50.0),
        ))
        
    }
}