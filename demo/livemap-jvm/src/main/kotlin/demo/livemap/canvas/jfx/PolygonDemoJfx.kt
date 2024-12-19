/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.canvas.jfx

import demo.livemap.common.canvas.PolygonDemoModel
import javafx.application.Application
import javafx.stage.Stage

class PolygonDemoJfx : Application() {
    override fun start(theStage: Stage) {
        BaseCanvasDemoJfx { canvas, _ ->
            PolygonDemoModel(canvas)
        }.start(theStage)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(PolygonDemoJfx::class.java, *args)
        }
    }
}