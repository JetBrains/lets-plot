/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvas.awt

import org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasControl
import org.jetbrains.letsPlot.awt.canvas.AwtMouseEventMapper
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JFrame.EXIT_ON_CLOSE
import javax.swing.JPanel

fun baseCanvasDemo(demoModel: (canvas: Canvas, createSnapshot: (String) -> Async<Canvas.Snapshot>) -> Unit) {
    val dim = Vector(800, 600)
    val frame = JFrame()
    frame.size = Dimension(dim.x, dim.y)
    frame.isVisible = true
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE)

    val panel = JPanel(null)
    val canvasControl = AwtCanvasControl(
        size = dim,
        animationTimerPeer = AwtAnimationTimerPeer(),
        mouseEventSource = AwtMouseEventMapper(panel)
    )

    panel.add(canvasControl.component())
    frame.add(panel)

    val canvas = canvasControl.createCanvas(dim)
    canvasControl.addChild(canvas)

    demoModel(canvas, canvasControl::createSnapshot)

    canvasControl.component().repaint()
}