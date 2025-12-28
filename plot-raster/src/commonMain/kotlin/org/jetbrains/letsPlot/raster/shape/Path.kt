/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Path2d
import org.jetbrains.letsPlot.core.canvas.applyPath


internal class Path : Figure() {
    var fillRule: FillRule by visualProp(FillRule.NON_ZERO)
    var pathData: Path2d? by visualProp(null)

    override fun render(ctx: Context2d) {
        val path = pathData ?: return

        fillPaint?.let {
            drawPath(path, ctx)

            when(fillRule) {
                FillRule.NON_ZERO -> ctx.fill(it)
                FillRule.EVEN_ODD -> ctx.fillEvenOdd(it)
            }
        }

        strokePaint?.let {
            drawPath(path, ctx)
            ctx.stroke(it)
        }
    }

    private fun drawPath(path: Path2d, context2d: Context2d) {
        context2d.beginPath()
        context2d.applyPath(path.getCommands())
    }

    override fun calculateLocalBBox(): DoubleRectangle {
        val path = pathData ?: return DoubleRectangle.XYWH(0, 0, 0, 0)

        val bounds = path.bounds
        if (bounds.width == 0.0 && bounds.height == 0.0) {
            // Degenerate path (point)
            return DoubleRectangle.XYWH(bounds.origin.x, bounds.origin.y, 0.0, 0.0)
        }
        return path.bounds.inflate(strokeWidth / 2.0)
    }

    enum class FillRule {
        NON_ZERO,
        EVEN_ODD
    }
}
