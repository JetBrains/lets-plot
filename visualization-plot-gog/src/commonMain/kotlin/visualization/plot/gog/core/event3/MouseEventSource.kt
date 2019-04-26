package jetbrains.datalore.visualization.plot.gog.core.event3

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration

interface MouseEventSource {
    fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration

    enum class MouseEventSpec {
        MOUSE_ENTERED,
        MOUSE_LEFT,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_CLICKED,
        MOUSE_DOUBLE_CLICKED,
        MOUSE_PRESSED,
        MOUSE_RELEASED
    }
}
