/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d

class LineJoinLineCapDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            setStrokeStyle(Color.RED)
            setLineWidth(10.0)

            val join = listOf(Context2d.LineJoin.BEVEL, Context2d.LineJoin.MITER, Context2d.LineJoin.ROUND)
            val cap = listOf(Context2d.LineCap.SQUARE, Context2d.LineCap.BUTT, Context2d.LineCap.ROUND)

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