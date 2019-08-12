package jetbrains.datalore.base.event

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration

interface MouseEventSource {
    fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration

}
