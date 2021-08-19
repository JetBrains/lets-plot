/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.canvasDemo

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