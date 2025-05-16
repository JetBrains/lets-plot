/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.handler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration

interface CanvasEventDispatcher {
    fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent)
    fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration
    fun dispatchFrom(mouseEventSource: MouseEventSource): Registration {
        val reg = CompositeRegistration()
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_ENTERED, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_ENTERED, it) }))
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_LEFT, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_LEFT, it) }))
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_MOVED, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_MOVED, it) }))
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_DRAGGED, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_DRAGGED, it) }))
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_CLICKED, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_CLICKED, it) }))
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_DOUBLE_CLICKED, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_DOUBLE_CLICKED, it) }))
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_PRESSED, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_PRESSED, it) }))
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_RELEASED, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_RELEASED, it) }))
        reg.add(mouseEventSource.addEventHandler(MouseEventSpec.MOUSE_WHEEL_ROTATED, handler { dispatchMouseEvent(MouseEventSpec.MOUSE_WHEEL_ROTATED, it) }))
        return reg
    }

    companion object {
        fun from(mouseEventPeer: MouseEventPeer) : CanvasEventDispatcher {
            return object : CanvasEventDispatcher {
                override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
                    mouseEventPeer.dispatch(kind, e)
                }

                override fun addEventHandler(
                    eventSpec: MouseEventSpec,
                    eventHandler: EventHandler<MouseEvent>
                ): Registration {
                    return mouseEventPeer.addEventHandler(eventSpec, eventHandler)
                }
            }
        }

    }
}
