/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions

import org.jetbrains.letsPlot.commons.intern.function.Consumer
import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.platf.w3c.dom.events.DomEventType
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget

fun <EventT : Event> EventTarget.on(event: DomEventType<EventT>, handler: Consumer<Event>): Registration {
    return on(event, object : Function<Event, Boolean> {
        override fun apply(value: Event): Boolean {
            handler.invoke(value)
            return true
        }
    })
}

fun <EventT : Event> EventTarget.on(event: DomEventType<EventT>, handler: Function<Event, Boolean>): Registration {
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

fun <EventT : Event> EventTarget.onEvent(
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