/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import kotlin.math.PI

class SectorDemoModel(canvas: Canvas) {

    init {
        with(canvas.context2d) {
            setFillStyle(Color.DARK_GREEN)

            drawSector(60.0, 60.0, 50.0, 0.0, PI)
            drawSector(170.0, 60.0, 50.0, 0.0, 2 * PI)
            drawSector(280.0, 60.0, 50.0, 0.0, 0.0)
            drawSector(390.0, 60.0, 50.0, 0.0, 3 * PI)
            drawSector(500.0, 60.0, 50.0, 0.2 * PI , 0.4 * PI)
            drawSector(610.0, 60.0, 50.0, -0.25 * PI , 0.25 * PI)
            drawSector(720.0, 60.0, 50.0, 0.25 * PI , -0.25 * PI)


            drawSector(60.0, 170.0, 50.0, 0.0, PI, true)
            drawSector(170.0, 170.0, 50.0, 0.0, 2 * PI, true)
            drawSector(280.0, 170.0, 50.0, 0.0, 0.0, true)
            drawSector(390.0, 170.0, 50.0, 0.0, 3 * PI, true)
            drawSector(500.0, 170.0, 50.0, 0.2 * PI , 0.4 * PI, true)
            drawSector(610.0, 170.0, 50.0, -0.25 * PI , 0.25 * PI, true)
            drawSector(720.0, 170.0, 50.0, 0.25 * PI , -0.25 * PI, true)
        }
    }

    private fun Context2d.drawSector(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double, anticlockwise: Boolean = false) {
        beginPath()
        moveTo(x, y)
        arc(x, y, radius, startAngle, endAngle, anticlockwise)
        fill()
    }
}