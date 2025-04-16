/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvas.jfx

import demo.livemap.common.canvas.ClipDemoModel
import javafx.application.Application
import javafx.stage.Stage

class ClipDemoJfx : Application() {

    override fun start(theStage: Stage) {
        BaseCanvasDemoJfx { canvas, _ ->
            ClipDemoModel(canvas)
        }.start(theStage)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ClipDemoJfx::class.java, *args)
        }
    }
}
