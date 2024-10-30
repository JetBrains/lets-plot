/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvasDemo


import org.jetbrains.letsPlot.commons.values.Color.Companion.BLUE
import org.jetbrains.letsPlot.commons.values.Color.Companion.GREEN
import org.jetbrains.letsPlot.commons.values.Color.Companion.ORANGE
import org.jetbrains.letsPlot.commons.values.Color.Companion.RED
import org.jetbrains.letsPlot.core.canvas.*

class ClipDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            restore()

            setFillStyle(GREEN)
            setStrokeStyle(ORANGE)
            setLineWidth(1.0)
            setLineJoin(LineJoin.ROUND)
            setLineCap(LineCap.ROUND)

            save()
            scale(.1, .1)
            setRhombus()
            stroke()
            clip()
            save()

            restore()

            setRhombus()
            clip()

            setFillStyle(BLUE)
            setStrokeStyle(RED)
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