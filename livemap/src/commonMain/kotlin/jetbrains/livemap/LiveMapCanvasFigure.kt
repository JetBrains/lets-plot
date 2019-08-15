package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.livemap.canvascontrols.LiveMapPresenter

class LiveMapCanvasFigure (private val liveMap: Async<BaseLiveMap>) : CanvasFigure {
    private val myBounds = ValueProperty(DoubleRectangle.span(DoubleVector.ZERO, DoubleVector.ZERO))
    private val myLiveMapPresenter = LiveMapPresenter()

    val isLoading: ReadableProperty<out Boolean>
        get() = myLiveMapPresenter.isLoading

    fun setBounds(bounds: DoubleRectangle) {
        myBounds.set(bounds)
    }

    override fun bounds(): ReadableProperty<DoubleRectangle> {
        return myBounds
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        myLiveMapPresenter.render(
            canvasControl,
            liveMap
        )

        return Registration.from(myLiveMapPresenter)
    }
}