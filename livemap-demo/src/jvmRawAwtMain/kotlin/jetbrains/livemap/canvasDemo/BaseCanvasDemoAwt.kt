/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import org.jetbrains.letsPlot.base.intern.async.Async
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.awt.AwtAnimationTimerPeer
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
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
        dim,
        AwtEventPeer(panel, Rectangle(Vector.ZERO, dim)),
        AwtAnimationTimerPeer()
    )
    panel.add(canvasControl.component())
    frame.add(panel)

    val canvas = canvasControl.createCanvas(dim)
    canvasControl.addChild(canvas)

    demoModel(canvas, canvasControl::createSnapshot)

    canvasControl.component().repaint()
}