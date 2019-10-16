package jetbrains.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.livemap.canvascontrols.LiveMapPresenter

class LiveMapCanvasFigure (private val liveMap: Async<BaseLiveMap>) : CanvasFigure {
    private val myDimension = ValueProperty(DoubleVector.ZERO)
    private val myLiveMapPresenter = LiveMapPresenter()

    val isLoading: ReadableProperty<out Boolean>
        get() = myLiveMapPresenter.isLoading

    fun setDimension(dim: DoubleVector) {
        myDimension.set(dim)
    }

    override fun dimension(): ReadableProperty<DoubleVector> {
        return myDimension
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        myLiveMapPresenter.render(canvasControl, liveMap)

        return Registration.from(myLiveMapPresenter)
    }
}
