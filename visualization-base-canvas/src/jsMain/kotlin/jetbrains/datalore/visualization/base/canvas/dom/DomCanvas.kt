package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.Asyncs
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.setHeight
import jetbrains.datalore.base.js.css.setWidth
import jetbrains.datalore.base.js.dom.DomApi
import jetbrains.datalore.base.js.dom.DomHTMLCanvasElement
import jetbrains.datalore.base.js.dom.DomWindow
import jetbrains.datalore.base.js.dom.context2d
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.ScaledCanvas
import kotlin.math.ceil

internal class DomCanvas private constructor(val domHTMLCanvasElement: DomHTMLCanvasElement, size: Vector, pixelRatio: Double)
    : ScaledCanvas(DomContext2d(domHTMLCanvasElement.context2d), size, pixelRatio) {

    init {
        domHTMLCanvasElement.style.setWidth(size.x)
        domHTMLCanvasElement.style.setHeight(size.y)
        domHTMLCanvasElement.width = ceil(size.x * pixelRatio).toInt()
        domHTMLCanvasElement.height = ceil(size.y * pixelRatio).toInt()
    }

    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return Asyncs.constant(DomSnapshot())
    }

    internal inner class DomSnapshot : Canvas.Snapshot {
        val canvasElement: DomHTMLCanvasElement
            get() = domHTMLCanvasElement
    }

    companion object {
        private val DEVICE_PIXEL_RATIO = DomWindow.getWindow().devicePixelRatio

        fun create(size: Vector): DomCanvas {
            return DomCanvas(DomApi.createCanvas(), size, DEVICE_PIXEL_RATIO)
        }
    }
}
