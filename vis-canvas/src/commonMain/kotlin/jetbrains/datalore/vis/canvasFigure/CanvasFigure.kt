package jetbrains.datalore.vis.canvasFigure

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl

interface CanvasFigure {
    fun dimension(): ReadableProperty<DoubleVector>

    fun mapToCanvas(canvasControl: CanvasControl): Registration
}
