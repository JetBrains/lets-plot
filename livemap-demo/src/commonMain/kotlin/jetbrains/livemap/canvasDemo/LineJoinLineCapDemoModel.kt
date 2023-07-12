/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.LineCap
import org.jetbrains.letsPlot.core.canvas.LineJoin

class LineJoinLineCapDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            setStrokeStyle(Color.RED)
            setLineWidth(10.0)

            val join = listOf(LineJoin.BEVEL, LineJoin.MITER, LineJoin.ROUND)
            val cap = listOf(LineCap.SQUARE, LineCap.BUTT, LineCap.ROUND)

            for (i in 0..2) {
                setTransform(1.0, 0.0, 0.0, 1.0, 100.0 * i + 20,20.0)
                setLineCap(cap[i])
                setLineJoin(join[i])
                corner()
                stroke()
            }
        }
    }

    private fun Context2d.corner() {
        beginPath()
        moveTo(0.0, 40.0)
        lineTo(40.0, 0.0)
        lineTo(80.0, 40.0)
    }
}