/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration

abstract class MouseEventSourceBase: MouseEventSource {
    private val myEventHandlers = HashMap<MouseEventSpec, MutableList<EventHandler<MouseEvent>>>()

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        myEventHandlers.getOrPut(eventSpec, ::mutableListOf).add(eventHandler)

        return object : Registration() {
            override fun doRemove() {
                myEventHandlers[eventSpec]?.let {
                    it.remove(eventHandler)
                    if (it.isEmpty()) {
                        myEventHandlers.remove(eventSpec)
                    }
                }
            }
        }
    }

    protected fun fire(eventSpec: MouseEventSpec, event: MouseEvent) {
        myEventHandlers[eventSpec]?.forEach { it.onEvent(event) }
    }
}
