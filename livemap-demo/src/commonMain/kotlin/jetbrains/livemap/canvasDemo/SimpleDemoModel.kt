/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas

class SimpleDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            setFillStyle(Color.BLUE)
            setStrokeStyle(Color.RED)

            beginPath()
            moveTo(20.0, 20.0)
            lineTo(100.0,20.0)
            lineTo(100.0, 100.0)
            closePath()

            fill()
            stroke()
        }
    }
}