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
            x = 150.0,
            y = 375.0,
            radiusX = 100.0,
            radiusY = 100.0,
            rotation = 0.0,
            startAngleDeg = -90.0,
            endAngleDeg = -180.0,
            anticlockwise = true,
            transform = AffineTransform.IDENTITY,
        )

        val cpts = ellipse.toBezierControlPoints()
        assertThat(cpts).isEqualTo(Ellipse.ControlPoints(
            cp1 = DoubleVector(150.0, 275.0),
            cp2 = DoubleVector(94.77152501692068, 275.0),
            cp3 = DoubleVector(50.0, 319.77152501692063),
            cp4 = DoubleVector(50.0, 375.0),
        ))
        
    }
}