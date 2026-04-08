/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration

class TranslatingMouseEventSource(
    private val delegate: MouseEventSource,
    private val dx: Int,
    private val dy: Int
) : MouseEventSource {
    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return delegate.addEventHandler(eventSpec, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                eventHandler.onEvent(event.at(event.x + dx, event.y + dy))
            }
        })
    }
}
