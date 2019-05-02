package jetbrains.datalore.visualization.base.canvasFigure

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl

interface CanvasFigure {
    fun bounds(): ReadableProperty<DoubleRectangle>

    fun mapToCanvas(canvasControl: CanvasControl): Registration
}
