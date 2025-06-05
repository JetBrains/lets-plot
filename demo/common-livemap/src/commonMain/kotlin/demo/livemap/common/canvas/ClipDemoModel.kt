/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.canvas

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.LineCap
import org.jetbrains.letsPlot.core.canvas.LineJoin

class ClipDemoModel(canvas: Canvas) {
    init {
        val ctx = canvas.context2d
        ctx.save()
        ctx.scale(.1, .1)
        ctx.beginPath()
        ctx.moveTo(200.0, 300.0)
        ctx.lineTo(400.0, 100.0)
        ctx.lineTo(600.0, 300.0)
        ctx.lineTo(400.0, 500.0)
        ctx.closePath()
        ctx.setLineWidth(10.0)
        ctx.stroke()
        ctx.clip()

        ctx.restore()

        ctx.save()

        ctx.translate(200.0, 200.0)
        ctx.beginPath()
        ctx.moveTo(0.0, 100.0)
        ctx.lineTo(200.0, -100.0)
        ctx.lineTo(400.0, 100.0)
        ctx.lineTo(200.0, 300.0)
        ctx.closePath()
        ctx.clip()

        ctx.setFillStyle(Color.Companion.BLUE)
        ctx.setStrokeStyle(Color.Companion.RED)
        ctx.setLineWidth(4.0)
        ctx.setLineJoin(LineJoin.ROUND)
        ctx.setLineCap(LineCap.ROUND)

        ctx.fillRect(0.0, 0.0, 300.0, 350.0)
        ctx.strokeRect(0.0, 0.0, 300.0, 350.0)
        ctx.restore()

        ctx.strokeRect(0.0, 0.0, 600.0, 600.0)
    }

}