/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import javafx.application.Platform
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.WindowConstants

open class DemoBaseJfx(private val demoModelProvider: (DoubleVector) -> DemoModelBase) {
    private val size: Vector get() = Vector(800, 600)

    internal fun show() {
        val canvasControl = JavafxCanvasControl(size, 1.0)

        Platform.runLater { demoModelProvider(size.toDoubleVector()).show(canvasControl) }

        showCanvasControl("AWT LiveMap Demo", canvasControl)
    }

    private fun showCanvasControl(title: String, canvasControl: JavafxCanvasControl) {
        val frame = JFrame(title)
        frame.layout = BorderLayout()
        frame.contentPane.add(canvasControl.component)
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.setSize(canvasControl.size.x, canvasControl.size.y)
        frame.isVisible = true
    }
}
