package jetbrains.datalore.base.js.dom

import jetbrains.datalore.base.registration.Registration
import org.w3c.dom.events.EventTarget

typealias DomEventTarget = EventTarget

fun <EventT : DomBaseEvent> DomEventTarget.onEvent(type: DomEventType<EventT>, listener: DomEventListener<EventT>): Registration {
    return onEvent(type, listener, false)
}

fun <EventT : DomBaseEvent> DomEventTarget.onEvent(type: DomEventType<EventT>, listener: DomEventListener<EventT>, capture: Boolean): Registration {
    addEventListener(type.name, listener, capture)
    return object : Registration() {
        override fun doRemove() {
            removeEventListener(type.name, listener)
        }
    }
}

fun DomEventTarget.onClick(handler: () -> Unit): Registration {
    return on(DomEventType.CLICK, handler)
}

fun DomEventTarget.onClick(handler: (DomMouseEvent) -> Unit): Registration {
    return on(DomEventType.CLICK, handler)
}

fun DomEventTarget.onClick(handler: (DomMouseEvent) -> Boolean): Registration {
    return on(DomEventType.CLICK, handler)
}

fun <EventT : DomBaseEvent> DomEventTarget.on(event: DomEventType<EventT>, runner: () -> Unit): Registration {
    val consumer: (EventT) -> Unit = { runner() }
    return on(event, consumer)
}

fun <EventT : DomBaseEvent> DomEventTarget.on(event: DomEventType<EventT>, consumer: (EventT) -> Unit): Registration {
    val handler: (EventT) -> Boolean = {
        consumer(it)
        true
    }
    return on(event, handler)
}

fun <EventT : DomBaseEvent> DomEventTarget.on(event: DomEventType<EventT>, handler: (EventT) -> Boolean): Registration {
    return onEvent(event, DomEventListener { evt: EventT ->
        val result = handler(evt)
        if (!result) {
            evt.preventDefault()
            evt.stopPropagation()
        }
        result
    }, false)
}

fun <EventT : DomBaseEvent> DomEventTarget.on(event: DomEventType<EventT>, selector: String, handler: (EventT) -> Boolean): Registration {
    return onEvent(event, DomEventListener { evt: EventT ->
        var result = true
        val node: DomNode = evt.eventTarget!!.cast()

        if (node.nodeType == DomNodeTypes.ELEMENT_NODE.toShort()) {
            val element: DomElement = node.cast()

            if (element.closest(selector) != null) {
                result = handler(evt)
            }
        }

        if (!result) {
            evt.preventDefault()
            evt.stopPropagation()
        }
        result
    }, false)
}

fun <T> DomEventTarget.cast(): T {
    return this as T
}
