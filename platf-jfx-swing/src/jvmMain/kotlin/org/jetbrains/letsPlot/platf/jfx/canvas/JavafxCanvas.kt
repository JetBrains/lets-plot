/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.canvas

import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.ScaledCanvas
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.platf.jfx.canvas.JavafxCanvasUtil.asyncTakeSnapshotImage
import kotlin.math.roundToInt

internal class JavafxCanvas
private constructor(
    val nativeCanvas: Canvas,
    size: Vector,
    pixelRatio: Double
) : ScaledCanvas(
    JavafxContext2d(nativeCanvas.graphicsContext2D),
    size,
    pixelRatio
) {

    companion object {
        fun create(size: Vector, pixelRatio: Double): JavafxCanvas {
            return JavafxCanvas(Canvas(), size, pixelRatio)
        }
    }

    init {
        nativeCanvas.width = size.x * pixelRatio
        nativeCanvas.height = size.y * pixelRatio
    }

    override fun takeSnapshot(): Async<org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot> {
        return asyncTakeSnapshotImage(nativeCanvas).map(
                success = { image -> JavafxSnapshot(image) }
        )
    }

    override fun immidiateSnapshot(): org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot {
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        return JavafxSnapshot(nativeCanvas.snapshot(params, null))
    }

    internal class JavafxSnapshot(val image: Image) : org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot {
        override fun copy() =
            JavafxSnapshot(
                WritableImage(
                    image.pixelReader,
                    image.width.roundToInt(),
                    image.height.roundToInt()
                )
            )
    }
}