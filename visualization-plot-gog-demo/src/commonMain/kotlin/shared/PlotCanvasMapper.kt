package jetbrains.datalore.visualization.gogDemo

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.svgToCanvas.SvgCanvasRenderer
import jetbrains.datalore.visualization.plot.base.event.MouseEventSpec
import jetbrains.datalore.visualization.plot.base.event.MouseEventSpec.MOUSE_LEFT
import jetbrains.datalore.visualization.plot.base.event.MouseEventSpec.MOUSE_MOVED
import jetbrains.datalore.visualization.plot.builder.Plot
import jetbrains.datalore.visualization.plot.builder.PlotContainer
import jetbrains.datalore.visualization.base.canvas.CanvasControl.EventSpec.MOUSE_LEFT as CANVAS_MOUSE_LEFT
import jetbrains.datalore.visualization.base.canvas.CanvasControl.EventSpec.MOUSE_MOVED as CANVAS_MOUSE_MOVED

class PlotCanvasMapper(plot: Plot, private val canvasControl: CanvasControl,
                       private val consumerTransform: (Consumer<MouseEvent>) -> Consumer<MouseEvent> = { it }) : Disposable {
    private val registration = CompositeRegistration()
    private val plotContainer = PlotContainer(plot, ValueProperty(canvasControl.size.toDoubleVector()))

    init {
        plotContainer.ensureContentBuilt()
        add(SvgCanvasRenderer(plotContainer.svg, canvasControl))
        add(createEventMapper(CANVAS_MOUSE_LEFT, MOUSE_LEFT))
        add(createEventMapper(CANVAS_MOUSE_MOVED, MOUSE_MOVED))
    }

    override fun dispose() {
        registration.dispose()
    }

    private fun add(disposable: Disposable) {
        registration.add(Registration.from(disposable))
    }

    private fun add(reg: Registration) {
        registration.add(reg)
    }

    private fun createEventMapper(canvasEventSpec: CanvasControl.EventSpec, plotEventSpec: MouseEventSpec): Registration {
        return canvasControl.addMouseEventHandler(canvasEventSpec, object : EventHandler<MouseEvent> {
            val consumer = consumerTransform {
                plotContainer.mouseEventPeer.dispatch(plotEventSpec, it)
            }

            override fun onEvent(event: MouseEvent) {
                consumer(event)
            }
        })
    }
}