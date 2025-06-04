/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.canvas

import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.core.canvas.ScaledCanvas
import kotlin.math.roundToInt

internal class JavafxCanvas
private constructor(
    val nativeCanvas: Canvas,
    size: Vector,
    pixelRatio: Double
) : ScaledCanvas(
    org.jetbrains.letsPlot.jfx.canvas.JavafxContext2d(nativeCanvas.graphicsContext2D),
    size,
    pixelRatio
) {

    companion object {
        fun create(size: Vector, pixelRatio: Double): org.jetbrains.letsPlot.jfx.canvas.JavafxCanvas {
            return org.jetbrains.letsPlot.jfx.canvas.JavafxCanvas(Canvas(), size, pixelRatio)
        }
    }

    init {
        nativeCanvas.width = size.x * pixelRatio
        nativeCanvas.height = size.y * pixelRatio
    }

    override fun takeSnapshot(): org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot {
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        return JavafxSnapshot(nativeCanvas.snapshot(params, null))
    }

    internal class JavafxSnapshot(val image: Image) : org.jetbrains.letsPlot.core.canvas.Canvas.Snapshot {
        override val size: Vector = Vector(
            image.width.roundToInt(),
            image.height.roundToInt()
        )

        override fun copy() =
            JavafxSnapshot(
                WritableImage(
                    image.pixelReader,
                    image.width.roundToInt(),
                    image.height.roundToInt()
                )
            )

        override fun toDataUrl(): String {
            TODO("JavafxCanvas.toDataUrl() - Not yet implemented")
        }
    }
}