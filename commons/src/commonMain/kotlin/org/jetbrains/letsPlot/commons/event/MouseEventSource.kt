/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.handler
import org.jetbrains.letsPlot.commons.registration.Registration

interface MouseEventSource {
    fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration
    fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: (MouseEvent) -> Unit): Registration {
        return addEventHandler(eventSpec, handler { eventHandler(it) })
    }
}
