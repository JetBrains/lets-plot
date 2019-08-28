package jetbrains.datalore.base.js.dom

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

class DomEventListener<EventT : DomBaseEvent>(private val handler: (EventT) -> Boolean) : EventListener {
    override fun handleEvent(event: Event) {
        handler(event as EventT)
    }
}
