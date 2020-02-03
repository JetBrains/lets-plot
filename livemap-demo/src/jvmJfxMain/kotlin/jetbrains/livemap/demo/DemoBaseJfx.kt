/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import javafx.application.Platform
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.awt.AwtCanvasDemoUtil
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl

open class DemoBaseJfx(private val demoModelProvider: (DoubleVector) -> DemoModelBase) {
    private val size: Vector get() = Vector(800, 600)

    internal fun show() {
        val canvasControl = JavafxCanvasControl(size, 1.0)

        Platform.runLater { demoModelProvider(size.toDoubleVector()).show(canvasControl) }

        AwtCanvasDemoUtil.showCanvasControl("AWT Livemap Demo", canvasControl)
    }
}
