package jetbrains.datalore.visualization.gogDemo

import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxCanvasControl
import jetbrains.datalore.visualization.plot.DemoAndTest

object JavafxDemoUtil {
    private var myViewSize = DoubleVector.ZERO
    private var myPlotSpecList: List<MutableMap<String, Any>> = emptyList()

    fun show(viewSize: DoubleVector, plotSpecList: List<MutableMap<String, Any>>) {
        myViewSize = viewSize
        myPlotSpecList = plotSpecList
        Application.launch(MyApplication::class.java)
    }

    class MyApplication : Application() {
        private val myRegistration = CompositeRegistration()

        override fun start(primaryStage: Stage) {

            val flowPane = FlowPane(Orientation.HORIZONTAL)

            myPlotSpecList.forEach { plotSpec ->
                val plot = DemoAndTest.createPlot(plotSpec, false)
                val canvasControl = JavafxCanvasControl(Vector(myViewSize.x.toInt(), myViewSize.y.toInt()), 1.0)
                flowPane.children.add(canvasControl.javafxRoot)

                myRegistration.add(Registration.from(PlotCanvasMapper(plot, canvasControl) { consumer ->
                    { JavafxThreadConsumer(consumer).accept(it) }
                }))
            }

            val scrollPane = ScrollPane()
            scrollPane.content = flowPane
            scrollPane.isPannable = true;
            scrollPane.vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED;
            scrollPane.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER;

            val theScene = Scene(scrollPane, 800.0, 600.0)
            primaryStage.title = "Javafx Livemap Demo"
            primaryStage.scene = theScene
            primaryStage.show()
        }

        @Throws(Exception::class)
        override fun stop() {
            myRegistration.dispose()
            super.stop()
        }
    }
}