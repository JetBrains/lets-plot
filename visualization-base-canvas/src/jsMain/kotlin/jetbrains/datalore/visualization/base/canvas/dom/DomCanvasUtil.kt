package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil.translateInClientCoord
import jetbrains.datalore.base.js.dom.*
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import org.w3c.dom.Image

internal object DomCanvasUtil {
    private val EVENT_SPEC_MAP = mapOf(
            MouseEventSpec.MOUSE_ENTERED to DomEventType.MOUSE_ENTER,
            MouseEventSpec.MOUSE_LEFT to DomEventType.MOUSE_LEAVE,
            MouseEventSpec.MOUSE_MOVED to DomEventType.MOUSE_MOVE,
            MouseEventSpec.MOUSE_DRAGGED to DomEventType.DRAG_OVER,
            MouseEventSpec.MOUSE_CLICKED to DomEventType.CLICK,
            MouseEventSpec.MOUSE_DOUBLE_CLICKED to DomEventType.DOUBLE_CLICK,
            MouseEventSpec.MOUSE_PRESSED to DomEventType.MOUSE_DOWN,
            MouseEventSpec.MOUSE_RELEASED to DomEventType.MOUSE_UP
    )

    fun addMouseEventHandler(domElement: DomElement, eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return domElement.onEvent(
                EVENT_SPEC_MAP.getValue(eventSpec),
                convertEventHandler(eventHandler, eventSpec),
                true
        )
    }

    private fun <T : DomMouseEvent> convertEventHandler(handler: EventHandler<MouseEvent>, eventSpec: MouseEventSpec): DomEventListener<T> {
        return DomEventListener {
            handler.onEvent(translateInClientCoord(it))
            false
        }
    }

    fun imagePngBase64ToImage(dataUrl: String): Async<Image> {
        val async = SimpleAsync<Image>()

        val image = Image()

        image.onload = {
            async.success(image)
        }

        image.src = dataUrl

        return async
    }
}
