package jetbrains.livemap.demo

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.stage.Stage
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.vis.canvas.javaFx.JavafxCanvasControl

class SimpleRawJfxDemo : Application() {

    override fun start(theStage: Stage) {
        val javafxCanvasControl = JavafxCanvasControl(Vector(800, 600), 1.0)
        EmptyLiveMapDemoModel(javafxCanvasControl).show()

        theStage.title = "Javafx Livemap Demo"
        theStage.scene = Scene(
            Group().apply {
                children.add(javafxCanvasControl.javafxRoot)
            },
            800.0,
            600.0
        )
        theStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(SimpleRawJfxDemo::class.java, *args)
        }
    }
}
