/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.platf.jfx.canvas.JavafxCanvasControl
import org.jetbrains.letsPlot.platf.jfx.canvas.JavafxEventPeer

class FeaturesRawJfxDemo : Application() {

    override fun start(theStage: Stage) {
        val dim = Vector(800, 600)
        val group = Group()
        val javafxCanvasControl = JavafxCanvasControl(group, dim, 1.0, JavafxEventPeer(group, Rectangle(Vector.ZERO, dim)))
        FeaturesDemoModel(dim.toDoubleVector()).show(javafxCanvasControl)

        theStage.title = "Javafx Livemap Demo"
        theStage.scene = Scene(group)

        theStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(FeaturesRawJfxDemo::class.java, *args)
        }
    }
}
