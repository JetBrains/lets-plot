/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas


internal class Line : Figure() {
    var x0: Float by visualProp(0.0f)
    var y0: Float by visualProp(0.0f)
    var x1: Float by visualProp(0.0f)
    var y1: Float by visualProp(0.0f)

    override fun render(canvas: Canvas) {
        val strokePaint = strokePaint ?: return

        canvas.context2d.beginPath()
        canvas.context2d.moveTo(x0.toDouble(), y0.toDouble())
        canvas.context2d.lineTo(x1.toDouble(), y1.toDouble())
        canvas.context2d.closePath()

        canvas.context2d.stroke(strokePaint)
    }

    override val localBounds: DoubleRectangle
        get() {
            return DoubleRectangle.LTRB(
                left = minOf(x0, x1),
                top = minOf(y0, y1),
                right = maxOf(x0, x1),
                bottom = maxOf(y0, y1)
            )
        }
}

