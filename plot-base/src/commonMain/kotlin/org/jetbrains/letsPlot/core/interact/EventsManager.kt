/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.intern.observable.event.handler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.event.ModifiersMatcher

class EventsManager : Disposable {
    private val globalMouseEventHandlers = mutableMapOf<MouseEventSpec, MutableList<ModifiersMatcherAndEventHandler>>()

    private val mouseEventPeer = MouseEventPeer()
    private val regs = CompositeRegistration()
    private val debugTrace = false

    init {
        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_ENTERED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_ENTERED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_LEFT, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_LEFT, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_MOVED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_MOVED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_DRAGGED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_DRAGGED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_CLICKED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_CLICKED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_DOUBLE_CLICKED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_DOUBLE_CLICKED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_PRESSED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_PRESSED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_RELEASED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_RELEASED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_ENTERED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_ENTERED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_WHEEL_ROTATED, handler { e ->
                dispatchGlobalEvent(MouseEventSpec.MOUSE_WHEEL_ROTATED, e)
            })
        )
    }

    fun setEventSource(s: MouseEventSource) {
        mouseEventPeer.addEventSource(s)
    }

    fun onMouseEvent(
        eventKind: MouseEventSpec,
        modifiersMatcher: ModifiersMatcher,
        handler: (MouseEvent) -> Unit
    ): Registration {
        val eventHandler = ModifiersMatcherAndEventHandler(modifiersMatcher, handler)
        globalMouseEventHandlers.getOrPut(eventKind, { mutableListOf() })
            .add(eventHandler)
        return object : Registration() {
            override fun doRemove() {
                globalMouseEventHandlers.getValue(eventKind).remove(eventHandler)
            }
        }
    }

    private fun dispatchGlobalEvent(eventKind: MouseEventSpec, event: MouseEvent) {
        globalMouseEventHandlers[eventKind]?.forEach {
            if (it.modifiersMatcher.match(event)) {
                if (debugTrace) {
                    println("Event: $eventKind, modifiers: ${event.modifiers}")
                }
                it.handler.invoke(event)
            }
        }
    }

    override fun dispose() {
        regs.dispose()
    }

    private class ModifiersMatcherAndEventHandler(
        val modifiersMatcher: ModifiersMatcher,
        val handler: (MouseEvent) -> Unit
    )
}