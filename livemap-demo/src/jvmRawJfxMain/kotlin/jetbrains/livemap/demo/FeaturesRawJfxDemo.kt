package jetbrains.livemap.demo

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl

class FeaturesRawJfxDemo : Application() {

    override fun start(theStage: Stage) {
        val dim = Vector(800, 600)
        val javafxCanvasControl = JavafxCanvasControl(dim, 1.0)
        FeaturesDemoModel(dim.toDoubleVector()).show(javafxCanvasControl)

        theStage.title = "Javafx Livemap Demo"
        theStage.scene = Scene(
            Group().apply {
                children.add(javafxCanvasControl.javafxRoot)
            },
            dim.x.toDouble(),
            dim.y.toDouble()
        )
        theStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(FeaturesRawJfxDemo::class.java, *args)
        }
    }
}
