package jetbrains.datalore.visualization.base.canvasGwt

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.domCore.dom.DomApi
import jetbrains.datalore.base.domCore.dom.DomHTMLCanvasElement
import jetbrains.datalore.base.domCore.dom.DomWindow
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.ScaledCanvas
import kotlin.math.ceil

internal class GwtCanvas private constructor(val domHTMLCanvasElement: DomHTMLCanvasElement, size: Vector, pixelRatio: Double)
    : ScaledCanvas(GwtContext2d(domHTMLCanvasElement.context2d), size, pixelRatio) {

    init {
        domHTMLCanvasElement.style.setWidth(size.x)
        domHTMLCanvasElement.style.setHeight(size.y)
        domHTMLCanvasElement.width = ceil(size.x * pixelRatio).toInt()
        domHTMLCanvasElement.height = ceil(size.y * pixelRatio).toInt()
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return Asyncs.constant(GwtSnapshot())
    }

    internal inner class GwtSnapshot : Canvas.Snapshot {
        val canvasElement: DomHTMLCanvasElement
            get() = domHTMLCanvasElement
    }

    companion object {
        private val DEVICE_PIXEL_RATIO = DomWindow.getWindow().devicePixelRatio

        fun create(size: Vector): GwtCanvas {
            return GwtCanvas(DomApi.createCanvas(), size, DEVICE_PIXEL_RATIO)
        }
    }
}
