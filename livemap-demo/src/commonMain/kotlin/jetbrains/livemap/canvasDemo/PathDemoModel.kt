/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas

class PathDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            setFillStyle(Color.BLUE)
            setStrokeStyle(Color.RED)

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
            lineTo(20.0, 80.0)

            closePath()

            fill()
            stroke()

            beginPath()
            moveTo(120.0, 20.0)
            lineTo(180.0,20.0)
            lineTo(180.0, 100.0)
            lineTo(160.0, 100.0)
            lineTo(160.0, 40.0)
            lineTo(140.0, 40.0)
            lineTo(140.0, 60.0)
            lineTo(200.0, 60.0)
            lineTo(200.0, 80.0)
            lineTo(120.0, 80.0)
            closePath()

            fillEvenOdd()
            stroke()
        }
    }
}