package org.jetbrains.letsPlot.platf.w3c

import org.jetbrains.letsPlot.platf.w3c.interop.createEventListener
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

fun <EventT : Event> domEventListener(handler: (EventT) -> Boolean): EventListener {
    return createEventListener { event ->
        @Suppress("UNCHECKED_CAST")
        handler(event as EventT)
    }
}