/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Canvas


internal class Path : Figure() {
    //var fillRule: PathFillMode? by visualProp(null)
    var skiaPath: PathData? by visualProp(null, managed = true)

    override fun render(canvas: Canvas) {
        val path = skiaPath ?: return

        //fillPaint?.let { canvas.drawPath(path, it) }
        //strokePaint?.let { canvas.drawPath(path, it) }
    }

    override val localBounds: DoubleRectangle
        get() {
            // `paint.getFillPath()` is not available in skiko v. 0.7.63
//            return (strokePaint?.getFillPath(path) ?: path).bounds

            val path = skiaPath ?: return DoubleRectangle.XYWH(0, 0, 0, 0)
            val strokeWidth = strokePaint?.strokeWidth ?: return path.bounds

            return path.bounds.inflate(strokeWidth / 2.0)
        }


    class PathData {
        val bounds: DoubleRectangle = DoubleRectangle.XYWH(0.0, 0.0, 0.0, 0.0)

        companion object {
            fun parse(string: String): PathData {
                return PathData()
            }
        }
    }
}
