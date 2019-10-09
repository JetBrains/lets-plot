package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil.translateInTargetCoord
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.css.enumerables.CssPosition
import jetbrains.datalore.base.js.css.setPosition
import jetbrains.datalore.base.js.dom.DomEventListener
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationEventHandler
import jetbrains.datalore.visualization.base.canvas.AnimationProvider.AnimationTimer
import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.base.canvas.EventPeer
import jetbrains.datalore.visualization.base.canvas.dom.DomCanvas.Companion.DEVICE_PIXEL_RATIO
import jetbrains.datalore.visualization.base.canvas.dom.DomCanvasUtil.imagePngBase64ToImage
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import kotlin.browser.document
import org.w3c.dom.events.MouseEvent as W3cMouseEvent

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
        return eventPeer.addEventHandler(
            eventSpec,
            handler { eventHandler.onEvent(translateInTargetCoord(it)) }
        )
    }

    override fun createCanvas(size: Vector): Canvas {
        val domCanvas = DomCanvas.create(size, DEVICE_PIXEL_RATIO)
        domCanvas.canvasElement.style.setPosition(CssPosition.ABSOLUTE)
        return domCanvas
    }

    override fun createSnapshot(dataUrl: String): Async<Canvas.Snapshot> {
        val async = SimpleAsync<Canvas.Snapshot>()

        imagePngBase64ToImage(dataUrl).onSuccess { image ->
            val domCanvas = DomCanvas.create(Vector(image.width, image.height), 1.0)
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

    private class DomEventPeer (private val myRootElement: Node) :
        EventPeer<MouseEventSpec, W3cMouseEvent>(MouseEventSpec::class) {
        private var myButtonPressed = false

        init {
            handle(DomEventType.MOUSE_ENTER) { dispatch(MouseEventSpec.MOUSE_ENTERED, it) }

            handle(DomEventType.MOUSE_LEAVE) { dispatch(MouseEventSpec.MOUSE_LEFT, it) }

            handle(DomEventType.CLICK) { dispatch(MouseEventSpec.MOUSE_CLICKED, it) }

            handle(DomEventType.DOUBLE_CLICK) { dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, it) }

            handle(DomEventType.MOUSE_DOWN) {
                myButtonPressed = true
                dispatch(MouseEventSpec.MOUSE_PRESSED, it)
            }

            handle(DomEventType.MOUSE_UP) {
                myButtonPressed = false
                dispatch(MouseEventSpec.MOUSE_RELEASED, it)
            }

            handle(DomEventType.MOUSE_MOVE) {
                if (myButtonPressed) {
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, it)
                } else {
                    dispatch(MouseEventSpec.MOUSE_MOVED, it)
                }
            }
        }

        private fun handle(eventSpec: DomEventType<W3cMouseEvent>, handler: (W3cMouseEvent) -> Unit) {
            myRootElement.addEventListener(eventSpec.name, DomEventListener<W3cMouseEvent> {
                handler(it)
                false
            })
        }

        override fun onSpecAdded(spec: MouseEventSpec) {}

        override fun onSpecRemoved(spec: MouseEventSpec) {}
    }
}
