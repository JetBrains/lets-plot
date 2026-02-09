/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import org.jetbrains.letsPlot.commons.registration.Registration

class MouseEventPeer : MouseEventSource {
    private val myEventHandlers = HashMap<MouseEventSpec, Listeners<EventHandler<MouseEvent>>>()
    private val myEventSources = ArrayList<MouseEventSource>()

    private val mySourceRegistrations = HashMap<MouseEventSource, HashMap<MouseEventSpec, Registration>>()

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        if (!myEventHandlers.containsKey(eventSpec)) {
            myEventHandlers[eventSpec] = Listeners()
            onAddSpec(eventSpec)
        }

        val addReg = myEventHandlers[eventSpec]?.add(eventHandler)
        return object : Registration() {
            override fun doRemove() {
                addReg?.remove()
                if (myEventHandlers[eventSpec]?.isEmpty == true) {
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

    fun addEventSource(eventSource: MouseEventSource): Registration {
        myEventSources.add(eventSource)

        // Start listening for all currently active specs on this new source
        myEventHandlers.keys.forEach { eventSpec ->
            startHandleSpecInSource(eventSource, eventSpec)
        }

        return object : Registration() {
            override fun doRemove() {
                myEventSources.remove(eventSource)
                // Dispose all registrations associated with this source
                mySourceRegistrations.remove(eventSource)?.values?.forEach { it.dispose() }
            }
        }
    }

    private fun onAddSpec(eventSpec: MouseEventSpec) {
        // When a client wants a new event type, listen for it on all upstream sources
        myEventSources.forEach { eventSource ->
            startHandleSpecInSource(eventSource, eventSpec)
        }
    }

    private fun startHandleSpecInSource(eventSource: MouseEventSource, eventSpec: MouseEventSpec) {
        // Prevent double registration
        val sourceRegs = mySourceRegistrations.getOrPut(eventSource) { HashMap() }
        if (sourceRegs.containsKey(eventSpec)) return

        val registration = eventSource.addEventHandler(eventSpec, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                dispatch(eventSpec, event)
            }
        })

        sourceRegs[eventSpec] = registration
    }

    private fun onRemoveSpec(eventSpec: MouseEventSpec) {
        // Client no longer wants this event type. Stop listening on all sources.
        myEventSources.forEach { eventSource ->
            mySourceRegistrations[eventSource]?.remove(eventSpec)?.dispose()
        }
    }
}