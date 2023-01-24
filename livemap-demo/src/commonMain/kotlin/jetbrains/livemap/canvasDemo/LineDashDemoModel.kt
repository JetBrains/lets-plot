/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d
import jetbrains.datalore.vis.canvas.LineCap

class LineDashDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            setStrokeStyle(Color.RED)
            setLineWidth(10.0)

            translate(0.0, 20.0)
            setLineDash(listOf(20.0).toDoubleArray())
            line()
            stroke()

            translate(0.0, 20.0)
            setLineDash(listOf(20.0, 20.0).toDoubleArray())
            line()
            stroke()

            translate(0.0, 20.0)
            setLineDash(listOf(20.0, 10.0, 20.0).toDoubleArray())
            line()
            stroke()

            setLineCap(LineCap.ROUND)

            translate(0.0, 20.0)
            setLineDash(listOf(20.0).toDoubleArray())
            line()
            stroke()

            translate(0.0, 20.0)
            setLineDash(listOf(20.0, 20.0).toDoubleArray())
            line()
            stroke()

            translate(0.0, 20.0)
            setLineDash(listOf(20.0, 10.0, 20.0).toDoubleArray())
            line()
            stroke()

            setLineCap(LineCap.SQUARE)

            translate(0.0, 20.0)
            setLineDash(listOf(20.0).toDoubleArray())
            line()
            stroke()

            translate(0.0, 20.0)
            setLineDash(listOf(20.0, 20.0).toDoubleArray())
            line()
            stroke()

            translate(0.0, 20.0)
            setLineDash(listOf(20.0, 10.0, 20.0).toDoubleArray())
            line()
            stroke()
        }
    }

    private fun Context2d.line() {
        beginPath()
        moveTo(0.0, 0.0)
        lineTo(400.0,0.0)
    }
}