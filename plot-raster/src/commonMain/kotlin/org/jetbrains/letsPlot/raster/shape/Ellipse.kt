/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas

internal class Ellipse : Figure() {
    var centerX: Float by visualProp(0.0f)
    var centerY: Float by visualProp(0.0f)
    var radiusX: Float by visualProp(0.0f)
    var radiusY: Float by visualProp(0.0f)

    private val rect: DoubleRectangle by computedProp(Ellipse::centerX, Ellipse::centerY, Ellipse::radiusX, Ellipse::radiusY) {
        DoubleRectangle.LTRB(
            left = centerX - radiusX,
            top = centerY - radiusY,
            right = centerX + radiusX,
            bottom = centerY + radiusY
        )
    }

    override fun render(canvas: Canvas) {
        if (fillPaint == null && strokePaint == null) {
            return
        }

        canvas.context2d.beginPath()
        canvas.context2d.ellipse(centerX.toDouble(), centerY.toDouble(), radiusX.toDouble(), radiusY.toDouble())
        canvas.context2d.closePath()

        fillPaint?.let { canvas.context2d.fill(it) }
        strokePaint?.let { canvas.context2d.stroke(it) }
    }

    override val localBounds: DoubleRectangle
        get() = DoubleRectangle.XYWH(centerX - radiusX, centerY - radiusY, radiusX * 2, radiusY * 2)
}
