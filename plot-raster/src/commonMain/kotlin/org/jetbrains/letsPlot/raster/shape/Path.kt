/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Path2d
import org.jetbrains.letsPlot.core.canvas.applyPath


internal class Path : Figure() {
    //var fillRule: PathFillMode? by visualProp(null)
    var pathData: Path2d? by visualProp(null)

    override fun render(canvas: Canvas) {
        val path = pathData ?: return

        fillPaint?.let {
            drawPath(path, canvas.context2d)
            canvas.context2d.fill(it)
        }
        strokePaint?.let {
            drawPath(path, canvas.context2d)
            canvas.context2d.stroke(it)
        }
    }

    fun drawPath(path: Path2d, context2d: Context2d) {
        context2d.beginPath()
        context2d.applyPath(path.getCommands())
    }

    override val localBounds: DoubleRectangle
        get() {
            // `paint.getFillPath()` is not available in skiko v. 0.7.63
//            return (strokePaint?.getFillPath(path) ?: path).bounds

            val path = pathData ?: return DoubleRectangle.XYWH(0, 0, 0, 0)
            val strokeWidth = strokePaint?.strokeWidth ?: return path.bounds

            return path.bounds.inflate(strokeWidth / 2.0)
        }

}
