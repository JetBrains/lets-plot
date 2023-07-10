/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Group
import javafx.scene.Scene
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import org.jetbrains.letsPlot.platf.jfx.canvas.JavafxCanvasControl
import org.jetbrains.letsPlot.platf.jfx.canvas.JavafxEventPeer
import java.awt.BorderLayout
import javax.swing.JFrame
import javax.swing.WindowConstants

open class DemoBaseJfx(private val demoModelProvider: (DoubleVector) -> DemoModelBase) {
    private val size: Vector get() = Vector(800, 600)

    internal fun show() {
        val group = Group()
        val component = JFXPanel().apply { scene = Scene(group) }
        val canvasControl = JavafxCanvasControl(group, size, 1.0, JavafxEventPeer(group, Rectangle(Vector.ZERO, size)))

        val frame = JFrame("JFX LiveMap Demo")
        frame.layout = BorderLayout()
        frame.contentPane.add(component)
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.setSize(canvasControl.size.x, canvasControl.size.y)
        frame.isVisible = true

        Platform.runLater {
            demoModelProvider(size.toDoubleVector()).show(canvasControl)
        }
    }
}
