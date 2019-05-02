package jetbrains.datalore.visualization.base.canvasFigure

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector.Companion.ZERO
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.observable.property.ValueProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svgToCanvas.SvgCanvasRenderer

class SvgCanvasFigure : CanvasFigure {
    val svgGElement = SvgGElement()
    private val myBounds = ValueProperty(DoubleRectangle(ZERO, ZERO))

    fun setBounds(bounds: DoubleRectangle) {
        myBounds.set(bounds)
    }

    override fun bounds(): ReadableProperty<DoubleRectangle> {
        return myBounds
    }

    override fun mapToCanvas(canvasControl: CanvasControl): Registration {
        SvgCanvasRenderer.draw(svgGElement, canvasControl)
        return Registration.EMPTY
    }
}
