package jetbrains.datalore.visualization.base.canvasFigure

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl

interface CanvasFigure {
    fun dimension(): ReadableProperty<DoubleVector>

    fun mapToCanvas(canvasControl: CanvasControl): Registration
}
