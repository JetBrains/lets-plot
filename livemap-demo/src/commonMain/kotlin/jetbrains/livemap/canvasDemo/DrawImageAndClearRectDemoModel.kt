/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.canvas.Canvas

const val dataUrl = "data:image/png;base64,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"

class DrawImageAndClearRectDemoModel(
    canvas: Canvas,
    createSnapshot: (String) -> Async<Canvas.Snapshot>
) {
    init {
        createSnapshot(dataUrl).map { snapshot ->
            with(canvas.context2d) {
                drawImage(snapshot, 10.0, 10.0)
                drawImage(snapshot, 120.0, 35.0, 75.0, 75.0)
                drawImage(snapshot, 0.0, 0.0, 75.0, 75.0, 60.0, 120.0, 100.0, 100.0)

                clearRect(DoubleRectangle(85.0, 60.0, 75.0, 100.0))
            }
        }
    }
}