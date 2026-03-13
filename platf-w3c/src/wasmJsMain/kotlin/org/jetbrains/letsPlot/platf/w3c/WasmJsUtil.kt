@file:OptIn(ExperimentalWasmJsInterop::class)

package org.jetbrains.letsPlot.platf.w3c

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener


fun DoubleArray.toJsArray(): JsArray<JsNumber> {
    val jsArray = JsArray<JsNumber>()
    for (i in indices) {
        jsArray[i] = this[i].toJsNumber()
    }
    return jsArray
}

// Creates a native JS object that satisfies the EventListener interface
fun createEventListener(handler: (Event) -> Unit): EventListener =
    js("({ handleEvent: handler })")

fun <EventT : Event> domEventListener(handler: (EventT) -> Boolean): EventListener {
    return createEventListener { event ->
        @Suppress("UNCHECKED_CAST")
        handler(event as EventT)
    }
}
