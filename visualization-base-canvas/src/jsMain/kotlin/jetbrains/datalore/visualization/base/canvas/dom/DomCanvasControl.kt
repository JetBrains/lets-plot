package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.enumerables.CssPosition
import jetbrains.datalore.base.js.css.setPosition
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.dom.DomCanvasUtil.imagePngBase64ToImage
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLElement
import kotlin.browser.document

class DomCanvasControl(override val size: Vector) : CanvasControl {
    val rootElement: HTMLElement = document.createElement("div") as HTMLElement
    private val eventPeer = DomEventPeer(rootElement)

    init {
        rootElement.style.setPosition(CssPosition.RELATIVE)
    }

    override fun createAnimationTimer(eventHandler: AnimationEventHandler): AnimationTimer {
        return object : DomAnimationTimer(rootElement) {
            override fun handle(millisTime: Long) {
                eventHandler.onEvent(millisTime)
            }
        }
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return DomCanvasUtil.addMouseEventHandler(eventPeer, eventSpec, eventHandler)
    }

    override fun createCanvas(size: Vector): Canvas {
        val domCanvas = DomCanvas.create(size)
        domCanvas.canvasElement.style.setPosition(CssPosition.ABSOLUTE)
        return domCanvas
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        val async = SimpleAsync<Canvas.Snapshot>()

        imagePngBase64ToImage(dataUrl).onSuccess { image ->
            val domCanvas = DomCanvas.create(Vector(image.width, image.height))
            val ctx = domCanvas.canvasElement.getContext("2d") as CanvasRenderingContext2D
            ctx.drawImage(image, 0.0, 0.0)

            domCanvas.takeSnapshot().onSuccess { async.success(it) }
        }

        return async
    }

    override fun addChild(canvas: Canvas) {
        rootElement.appendChild((canvas as DomCanvas).canvasElement)
    }

    override fun removeChild(canvas: Canvas) {
        rootElement.removeChild((canvas as DomCanvas).canvasElement)
    }
}
