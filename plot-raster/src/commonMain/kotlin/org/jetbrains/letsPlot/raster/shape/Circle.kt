/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas
import kotlin.math.PI


internal class Circle : Figure() {
    var centerX: Float by visualProp(0.0f)
    var centerY: Float by visualProp(0.0f)
    var radius: Float by visualProp(0.0f)

    override fun render(canvas: Canvas) {
        if (fillPaint == null && strokePaint == null) {
            return
        }

        canvas.context2d.beginPath()
        canvas.context2d.arc(centerX.toDouble(), centerY.toDouble(), radius.toDouble(), 0.0, 2* PI)
        canvas.context2d.closePath()

        fillPaint?.let { canvas.context2d.fill(it) }
        strokePaint?.let { canvas.context2d.stroke(it) }
    }

    override val localBounds: DoubleRectangle
        get() = DoubleRectangle.XYWH(
            centerX - radius,
            centerY - radius,
            radius * 2,
            radius * 2
        )
}
