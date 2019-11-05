/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.javaFx

import javafx.embed.swing.JFXPanel
import javafx.scene.image.Image
import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.ScaledCanvas
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasUtil.asyncTakeSnapshotImage
import javafx.scene.canvas.Canvas as NativeCanvas

internal class JavafxCanvas
private constructor(
        val nativeCanvas: NativeCanvas,
        size: Vector,
        pixelRatio: Double) :
        ScaledCanvas(
            JavafxContext2d(nativeCanvas.graphicsContext2D),
                size,
                pixelRatio) {

    companion object {
        init {
            //initialize Toolkit
            // ToDo: remove when switching to SwingJfxPanel
            JFXPanel()
        }

        fun create(size: Vector, pixelRatio: Double): JavafxCanvas {
            return JavafxCanvas(NativeCanvas(), size, pixelRatio)
        }
    }

    init {
        nativeCanvas.width = size.x * pixelRatio
        nativeCanvas.height = size.y * pixelRatio
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return asyncTakeSnapshotImage(nativeCanvas).map(
                success = { image -> JavafxSnapshot(image) }
        )
    }

    internal class JavafxSnapshot(val image: Image) : Canvas.Snapshot
}
