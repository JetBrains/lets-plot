/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvas.jfx

import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.jfx.canvas.JavafxCanvasControl
import org.jetbrains.letsPlot.jfx.canvas.JavafxEventPeer

class BaseCanvasDemoJfx(val demoModel: (canvas: Canvas, createSnapshot: (String) -> Async<Canvas.Snapshot>) -> Unit) {

    fun start(theStage: Stage) {
        val dim = Vector(800, 600)
        val group = Group()
        val javafxCanvasControl = JavafxCanvasControl(
            group,
            dim,
            1.0,
            JavafxEventPeer(group, Rectangle(Vector.ZERO, dim))
        )

        val canvas = javafxCanvasControl.createCanvas(dim)
        demoModel(canvas, javafxCanvasControl::createSnapshot)
        javafxCanvasControl.addChild(canvas)

        theStage.title = "Javafx Simple Demo"
        theStage.scene = Scene(group)

        theStage.show()
    }
}