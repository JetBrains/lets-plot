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

    override fun calculateLocalBBox(): DoubleRectangle {
        val minX = minOf(x0, x1).toDouble()
        val minY = minOf(y0, y1).toDouble()
        val maxX = maxOf(x0, x1).toDouble()
        val maxY = maxOf(y0, y1).toDouble()

        if (minX == maxX && minY == maxY) {
            // Degenerate line (point)
            return DoubleRectangle.XYWH(minX, minY, 0.0, 0.0)
        }

        return DoubleRectangle.LTRB(minX, minY, maxX, maxY).inflate(strokeWidth / 2.0)
    }

}

