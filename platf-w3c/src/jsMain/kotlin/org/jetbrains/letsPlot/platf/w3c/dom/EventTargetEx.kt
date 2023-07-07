/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom

import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.platf.w3c.dom.events.DomEventListener
import org.jetbrains.letsPlot.platf.w3c.dom.events.DomEventType
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

//fun <EventT : Event> EventTarget.onEvent(
//    type: DomEventType<EventT>,
//    listener: DomEventListener<EventT>
//): Registration {
//    return onEvent(type, listener, false)
//}

fun <EventT : Event> EventTarget.onEvent(
    type: DomEventType<EventT>,
    listener: DomEventListener<EventT>,
    capture: Boolean
): Registration {
    addEventListener(type.name, listener, capture)
    return object : Registration() {
        override fun doRemove() {
            removeEventListener(type.name, listener)
        }
    }
}

//fun EventTarget.onClick(handler: () -> Unit): Registration {
//    return on(DomEventType.CLICK, handler)
//}
//
//fun EventTarget.onClick(handler: (MouseEvent) -> Unit): Registration {
//    return on(DomEventType.CLICK, handler)
//}
//
//fun EventTarget.onClick(handler: (MouseEvent) -> Boolean): Registration {
//    return on(DomEventType.CLICK, handler)
//}

fun <EventT : Event> EventTarget.on(event: DomEventType<EventT>, runner: () -> Unit): Registration {
    val consumer: (EventT) -> Unit = { runner() }
    return on(event, consumer)
}

fun <EventT : Event> EventTarget.on(
    event: DomEventType<EventT>,
    consumer: (EventT) -> Unit
): Registration {
    val handler: (EventT) -> Boolean = {
        consumer(it)
        true
    }
    return on(event, handler)
}

fun <EventT : Event> EventTarget.on(
    event: DomEventType<EventT>,
    handler: (EventT) -> Boolean
): Registration {
    return onEvent(event, DomEventListener { evt: EventT ->
        val result = handler(evt)
        if (!result) {
            evt.preventDefault()
            evt.stopPropagation()
        }
        result
    }, false)
}

//fun <EventT : Event> EventTarget.on(
//    event: DomEventType<EventT>,
//    selector: String,
//    handler: (EventT) -> Boolean
//): Registration {
//    return onEvent(event, DomEventListener { evt: EventT ->
//        var result = true
//        val node: Node = evt.target!!.cast()
//
//        if (node.nodeType == DomNodeTypes.ELEMENT_NODE.toShort()) {
//            val element: Element = node.cast()
//
//            if (element.closest(selector) != null) {
//                result = handler(evt)
//            }
//        }
//
//        if (!result) {
//            evt.preventDefault()
//            evt.stopPropagation()
//        }
//        result
//    }, false)
//}
//
//fun <T> EventTarget.cast(): T {
//    @Suppress("UNCHECKED_CAST")
//    return this as T
//}
