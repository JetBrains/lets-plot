/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.canvas

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d

class PathDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            setFillStyle(Color.BLUE)
            setStrokeStyle(Color.RED)

            q()

            fill()
            stroke()

            translate(150.0, 0.0)

            q()

            fillEvenOdd()
            stroke()
        }
    }

    private fun Context2d.q() {
        beginPath()
        moveTo(20.0, 20.0)
        lineTo(80.0,20.0)
        lineTo(80.0, 100.0)
        lineTo(60.0, 100.0)
        lineTo(60.0, 40.0)
        lineTo(40.0, 40.0)
        lineTo(40.0, 60.0)
        lineTo(100.0, 60.0)
        lineTo(100.0, 80.0)
        bezierCurveTo(80.0, 100.0, 40.0, 100.0, 20.0, 80.0)

        closePath()
    }
}