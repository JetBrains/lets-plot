/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.common.canvas

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.canvas.Canvas

class FillRectStrokeRectDemoModel(canvas: Canvas) {
    init {
        with(canvas.context2d) {
            setFillStyle(Color.BLUE)
            setStrokeStyle(Color.RED)
            setLineWidth(10.0)
            setLineDash(listOf(10.0, 10.0).toDoubleArray())

            fillRect(20.0, 20.0, 80.0, 80.0 )
            strokeRect(20.0, 20.0, 80.0, 80.0 )
        }
    }
}