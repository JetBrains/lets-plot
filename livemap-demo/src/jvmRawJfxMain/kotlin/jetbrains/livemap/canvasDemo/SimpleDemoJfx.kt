/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl
import jetbrains.datalore.vis.canvas.javaFx.JavafxEventPeer

class SimpleDemoJfx : Application() {

    override fun start(theStage: Stage) {
        BaseCanvasDemoJfx { canvas, _ ->
            SimpleDemoModel(canvas)
        }.start(theStage)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(SimpleDemoJfx::class.java, *args)
        }
    }
}