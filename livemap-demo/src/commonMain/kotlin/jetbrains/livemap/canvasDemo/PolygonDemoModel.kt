/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.Context2d

class PolygonDemoModel(canvas: Canvas) {
    private val size = 200.0
    private val delta = 0.01

    init {
        with(canvas.context2d) {
            setFillStyle(Color.PACIFIC_BLUE)
            setStrokeStyle(Color.RED)

            run {
                beginPath()
                rect()
                fill()
                rect(size + delta)
                fill()
            }

            translate(0.0, 300.0)
            run {
                beginPath()
                rect()
                rect(size + delta)
                fill()
            }
        }
    }

    private fun Context2d.rect(originX: Double = 0.0) {
        moveTo(0.0 + originX, 0.0)
        lineTo(0.0 + originX, size)
        lineTo(size + originX, size)
        lineTo(size + originX, 0.0)
        lineTo(0.0 + originX, 0.0)
    }
}