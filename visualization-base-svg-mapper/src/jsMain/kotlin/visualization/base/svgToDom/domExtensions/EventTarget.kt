package visualization.base.svgToDom.domExtensions

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.svgToDom.domUtil.DomEventType
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget

fun <EventT: Event> EventTarget.on(event: DomEventType<EventT>, handler: Consumer<Event>): Registration {
    return on(event, object : Function<Event, Boolean> {
        override fun apply(value: Event): Boolean {
            handler.invoke(value)
            return true
        }
    })
}

fun <EventT: Event> EventTarget.on(event: DomEventType<EventT>, handler: Function<Event, Boolean>): Registration {
    return onEvent(event, object : EventListener {
        override fun handleEvent(event: Event) {
            val result = handler.apply(event)
            if (!result) {
                event.preventDefault()
                event.stopPropagation()
            }
        }
    }, false)
}

fun <EventT: Event> EventTarget.onEvent(
        type: DomEventType<EventT>,
        listener: EventListener,
        capture: Boolean
): Registration {
    addEventListener(type.name, listener, capture)
    return object : Registration() {
        override fun doRemove() {
            removeEventListener(type.name, listener)
        }
    }
}