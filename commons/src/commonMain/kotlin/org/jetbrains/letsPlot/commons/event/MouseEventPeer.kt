/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration

class MouseEventPeer : MouseEventSource {
    private val myEventHandlers = HashMap<MouseEventSpec, Listeners<EventHandler<MouseEvent>>>()
    private val myEventSources = ArrayList<MouseEventSource>()
    private val mySourceRegistrations = HashMap<MouseEventSpec, CompositeRegistration>()

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        if (!myEventHandlers.containsKey(eventSpec)) {
            myEventHandlers[eventSpec] = Listeners()
            onAddSpec(eventSpec)
        }

        val addReg = myEventHandlers[eventSpec]?.add(eventHandler)
        return object : Registration() {
            override fun doRemove() {
                addReg?.remove()
                if (myEventHandlers[eventSpec]!!.isEmpty) {
                    myEventHandlers.remove(eventSpec)
                    onRemoveSpec(eventSpec)
                }
            }
        }
    }

    fun dispatch(eventSpec: MouseEventSpec, mouseEvent: MouseEvent) {
        if (myEventHandlers.containsKey(eventSpec)) {
            myEventHandlers[eventSpec]?.fire(object : ListenerCaller<EventHandler<MouseEvent>> {
                override fun call(l: EventHandler<MouseEvent>) {
                    l.onEvent(mouseEvent)
                }
            })
        }
    }

    fun addEventSource(eventSource: MouseEventSource) {
        myEventHandlers.keys.forEach { eventSpec -> startHandleSpecInSource(eventSource, eventSpec) }
        myEventSources.add(eventSource)
    }

    private fun onAddSpec(eventSpec: MouseEventSpec) {
        myEventSources.forEach { eventSource -> startHandleSpecInSource(eventSource, eventSpec) }
    }

    private fun startHandleSpecInSource(eventSource: MouseEventSource, eventSpec: MouseEventSpec) {
        val registration = eventSource.addEventHandler(eventSpec, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                dispatch(eventSpec, event)
            }
        })

        if (!mySourceRegistrations.containsKey(eventSpec)) {
            mySourceRegistrations[eventSpec] = CompositeRegistration()
        }
        mySourceRegistrations[eventSpec]?.add(registration)
    }

    private fun onRemoveSpec(eventSpec: MouseEventSpec) {
        if (mySourceRegistrations.containsKey(eventSpec)) {
            mySourceRegistrations.remove(eventSpec)?.dispose()
        }
    }
}
