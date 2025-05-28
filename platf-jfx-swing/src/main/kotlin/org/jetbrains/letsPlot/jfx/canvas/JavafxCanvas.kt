/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.canvas

import javafx.scene.SnapshotParameters
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.jfx.canvas.JavafxCanvasUtil.asyncTakeSnapshotImage
import kotlin.math.roundToInt

typealias FxCanvas = javafx.scene.canvas.Canvas
internal class JavafxCanvas
private constructor(
    val nativeCanvas: FxCanvas,
    override val size: Vector,
    pixelRatio: Double
) : Canvas {

    companion object {
        fun create(size: Vector, pixelRatio: Double): JavafxCanvas {
            return JavafxCanvas(FxCanvas(), size, pixelRatio)
        }
    }

    override val context2d: JavafxContext2d = JavafxContext2d(nativeCanvas.graphicsContext2D, pixelRatio)

    init {
        nativeCanvas.width = size.x.toDouble()// * pixelRatio
        nativeCanvas.height = size.y.toDouble()// * pixelRatio
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return asyncTakeSnapshotImage(nativeCanvas).map(
                success = { image -> JavafxSnapshot(image) }
        )
    }

    override fun immidiateSnapshot(): Canvas.Snapshot {
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        return JavafxSnapshot(nativeCanvas.snapshot(params, null))
    }

    internal class JavafxSnapshot(val image: Image) : Canvas.Snapshot {
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