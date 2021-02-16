/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvas.awt.AwtRepaintTimer
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel

fun baseCanvasDemo(demoModel: (canvas: Canvas, createSnapshot: (String) -> Async<Canvas.Snapshot>) -> Unit) {
    val dim = Vector(800, 600)
    val panel = JPanel()
    val timer = AwtRepaintTimer(panel::repaint)
    val canvasControl = AwtCanvasControl(panel, dim, 1.0, AwtEventPeer(panel, Rectangle(Vector.ZERO, dim)), timer)

    val canvas = canvasControl.createCanvas(dim)
    demoModel(canvas, canvasControl::createSnapshot)
    canvasControl.addChild(canvas)

    val frame = JFrame()
    frame.add(panel)
    frame.size = Dimension(dim.x, dim.y)

    frame.isVisible = true
}