/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d


internal class Line : Figure() {
    var x0: Float by visualProp(0.0f)
    var y0: Float by visualProp(0.0f)
    var x1: Float by visualProp(0.0f)
    var y1: Float by visualProp(0.0f)

    override fun render(ctx: Context2d) {
        val strokePaint = strokePaint ?: return

        ctx.beginPath()
        ctx.moveTo(x0.toDouble(), y0.toDouble())
        ctx.lineTo(x1.toDouble(), y1.toDouble())

        ctx.stroke(strokePaint)
    }

    override val bBox: DoubleRectangle
        get() {
            return DoubleRectangle.LTRB(
                left = minOf(x0, x1),
                top = minOf(y0, y1),
                right = maxOf(x0, x1),
                bottom = maxOf(y0, y1)
            )
        }

    override val boundingClientRect: DoubleRectangle
        get() {
            if (x0 == x1 && y0 == y1) {
                return DoubleRectangle.XYWH(x0.toDouble(), y0.toDouble(), 0.0, 0.0)
            }

            return bBox.inflate(strokeWidth / 2.0)
        }
}

