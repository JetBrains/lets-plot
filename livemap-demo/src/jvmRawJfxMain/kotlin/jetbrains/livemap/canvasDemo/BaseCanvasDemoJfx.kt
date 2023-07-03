/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import org.jetbrains.letsPlot.base.intern.async.Async
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl
import jetbrains.datalore.vis.canvas.javaFx.JavafxEventPeer

class BaseCanvasDemoJfx(val demoModel: (canvas: Canvas, createSnapshot: (String) -> Async<Canvas.Snapshot>) -> Unit) {

    fun start(theStage: Stage) {
        val dim = Vector(800, 600)
        val group = Group()
        val javafxCanvasControl = JavafxCanvasControl(group, dim, 1.0, JavafxEventPeer(group, Rectangle(Vector.ZERO, dim)))

        val canvas = javafxCanvasControl.createCanvas(dim)
        demoModel(canvas, javafxCanvasControl::createSnapshot)
        javafxCanvasControl.addChild(canvas)

        theStage.title = "Javafx Simple Demo"
        theStage.scene = Scene(group)

        theStage.show()
    }
}