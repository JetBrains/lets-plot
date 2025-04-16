/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.canvas

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.LineCap
import org.jetbrains.letsPlot.core.canvas.LineJoin

class ClipDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            save()
            scale(.1, .1)
            setRhombus()
            setLineWidth(10.0)
            stroke()
            clip()

            restore()

            setRhombus()
            clip()

            setFillStyle(Color.Companion.BLUE)
            setStrokeStyle(Color.Companion.RED)
            setLineWidth(4.0)
            setLineJoin(LineJoin.ROUND)
            setLineCap(LineCap.ROUND)

            fillRect(200.0, 200.0, 300.0, 350.0)
            strokeRect(200.0, 200.0, 300.0, 350.0)

        }
    }

    private fun Context2d.setRhombus() {
        beginPath()
        moveTo(200.0, 300.0)
        lineTo(400.0,100.0)
        lineTo(600.0,300.0)
        lineTo(400.0,500.0)
        closePath()
    }
}