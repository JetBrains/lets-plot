/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import kotlin.math.PI

class ScaleRotateTranslateDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            setStrokeStyle(Color.RED)
            setLineWidth(10.0)
            translate(60.0, 60.0)

            cross()
            stroke()

            translate(150.0, 0.0)
            rotate(PI / 6)
            scale(2.0, 2.0)

            cross()
            stroke()

            rotate(-PI / 6)
            scale(0.25, 0.25)
            translate(150.0, 0.0)

            cross()
            stroke()
        }
    }

    private fun Context2d.cross() {
        beginPath()
        moveTo(0.0, -40.0)
        lineTo(0.0, 40.0)
        moveTo(-40.0, 0.0)
        lineTo(40.0, 0.0)
    }
}