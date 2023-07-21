/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

import javafx.application.Application
import javafx.stage.Stage

class DrawImageAndClearRectDemoJfx : Application() {

    override fun start(theStage: Stage) {
        BaseCanvasDemoJfx { canvas, createSnapshot ->
            DrawImageAndClearRectDemoModel(canvas, createSnapshot)
        }.start(theStage)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(DrawImageAndClearRectDemoJfx::class.java, *args)
        }
    }
}