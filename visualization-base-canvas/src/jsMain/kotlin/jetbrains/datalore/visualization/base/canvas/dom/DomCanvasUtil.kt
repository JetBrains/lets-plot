package jetbrains.datalore.visualization.base.canvas.dom

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.dom.DomEventUtil.translateInClientCoord
import jetbrains.datalore.base.js.dom.*
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl.EventSpec

internal object DomCanvasUtil {
    private val EVENT_SPEC_MAP = mapOf(
            EventSpec.MOUSE_ENTERED to DomEventType.MOUSE_ENTER,
            EventSpec.MOUSE_LEFT to DomEventType.MOUSE_LEAVE,
            EventSpec.MOUSE_MOVED to DomEventType.MOUSE_MOVE,
            EventSpec.MOUSE_DRAGGED to DomEventType.DRAG_OVER,
            EventSpec.MOUSE_CLICKED to DomEventType.CLICK,
            EventSpec.MOUSE_DOUBLE_CLICKED to DomEventType.DOUBLE_CLICK,
            EventSpec.MOUSE_PRESSED to DomEventType.MOUSE_DOWN,
            EventSpec.MOUSE_RELEASED to DomEventType.MOUSE_UP
    )

    fun addMouseEventHandler(domElement: DomElement, eventSpec: EventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return domElement.onEvent(
                EVENT_SPEC_MAP.getValue(eventSpec),
                convertEventHandler(eventHandler, eventSpec),
                true
        )
    }

    private fun <T : DomMouseEvent> convertEventHandler(handler: EventHandler<MouseEvent>, eventSpec: EventSpec): DomEventListener<T> {
        return DomEventListener {
            handler.onEvent(translateInClientCoord(it))
            false
        }
    }
}
