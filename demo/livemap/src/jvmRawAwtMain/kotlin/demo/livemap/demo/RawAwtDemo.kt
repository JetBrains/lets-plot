/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasControl
import org.jetbrains.letsPlot.awt.canvas.AwtMouseEventMapper
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.Vector
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants.EXIT_ON_CLOSE

class RawAwtDemo(val demoModel: (DoubleVector) -> DemoModelBase) {
    fun start() {
        val canvasSize = Dimension(800, 600)
        val canvasContainer = JPanel(null).apply {
            size = canvasSize
        }

        val canvasControl = AwtCanvasControl(
            size = canvasSize.toVector(),
            animationTimerPeer = AwtAnimationTimerPeer(),
            mouseEventSource = AwtMouseEventMapper(canvasContainer)
        )

        demoModel(canvasSize.toDoubleVector()).show(canvasControl)

        JFrame().apply {
            size = canvasSize
            isVisible = true
            defaultCloseOperation = EXIT_ON_CLOSE
            add(canvasContainer)
        }
        canvasContainer.add(canvasControl.component())
    }

}


fun Dimension.toVector() = Vector(width, height)
fun Dimension.toDoubleVector() = DoubleVector(width.toDouble(), height.toDouble())