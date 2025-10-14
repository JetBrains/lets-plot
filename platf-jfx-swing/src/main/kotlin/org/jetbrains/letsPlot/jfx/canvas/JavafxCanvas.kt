/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.canvas

import javafx.scene.SnapshotParameters
import javafx.scene.image.Image
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import kotlin.math.roundToInt

internal class JavafxCanvas private constructor(
    val nativeCanvas: javafx.scene.canvas.Canvas,
    override val size: Vector,
    pixelRatio: Double
) : Canvas {
    override val context2d: Context2d = JavafxContext2d(nativeCanvas.graphicsContext2D)
        .also { ctx -> ctx.scale(pixelRatio, pixelRatio) }

    companion object {
        fun create(size: Vector, pixelRatio: Double): JavafxCanvas {
            return JavafxCanvas(javafx.scene.canvas.Canvas(), size, pixelRatio)
        }
    }

    init {
        nativeCanvas.width = size.x * pixelRatio
        nativeCanvas.height = size.y * pixelRatio
    }

    override fun takeSnapshot(): Canvas.Snapshot {
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        return JavafxSnapshot(nativeCanvas.snapshot(params, null))
    }

    internal class JavafxSnapshot(val image: Image) : Canvas.Snapshot {
        override val size: Vector = Vector(
            image.width.roundToInt(),
            image.height.roundToInt()
        )
        override val bitmap: Bitmap
            get() {
                val width = image.width.roundToInt()
                val height = image.height.roundToInt()
                val argbArray = IntArray(width * height)
                image.pixelReader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), argbArray, 0, width)
                return Bitmap(width, height, argbArray)
            }

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