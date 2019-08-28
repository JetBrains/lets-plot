package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil.translateInTargetCoord
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.dom.DomEventPeer.DomEventSpec.*
import org.w3c.dom.Image


internal object DomCanvasUtil {
    private val EVENT_SPEC_MAP = mapOf(
            MouseEventSpec.MOUSE_ENTERED to MOUSE_ENTERED,
            MouseEventSpec.MOUSE_LEFT to MOUSE_EXITED,
            MouseEventSpec.MOUSE_MOVED to MOUSE_MOVED,
            MouseEventSpec.MOUSE_DRAGGED to MOUSE_DRAGGED,
            MouseEventSpec.MOUSE_CLICKED to MOUSE_CLICKED,
            MouseEventSpec.MOUSE_DOUBLE_CLICKED to MOUSE_DOUBLE_CLICKED,
            MouseEventSpec.MOUSE_PRESSED to MOUSE_PRESSED,
            MouseEventSpec.MOUSE_RELEASED to MOUSE_RELEASED
    )

    fun addMouseEventHandler(eventPeer: DomEventPeer, eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return eventPeer.addEventHandler(
            EVENT_SPEC_MAP[eventSpec] ?: error("Unknown MouseEventSpec: $eventSpec"),
            object : EventHandler<W3cMouseEvent> {
                override fun onEvent(event: W3cMouseEvent) {
                    eventHandler.onEvent(translateInTargetCoord(event))
                }
            }
        )
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
