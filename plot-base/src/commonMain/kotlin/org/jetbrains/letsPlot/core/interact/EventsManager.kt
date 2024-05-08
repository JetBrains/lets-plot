/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.observable.event.handler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.interact.ui.UiControl

typealias UiEventHandler = (UiControl?, MouseEvent) -> Unit

class EventsManager : Disposable {
    private val globalMouseEventHandlers = mutableMapOf<MouseEventSpec, MutableList<UiEventHandler>>()

    private val mouseEventPeer = MouseEventPeer()
    private val controls = mutableSetOf<UiControl>()
    private var hoveredControl: UiControl? = null
    private val regs = CompositeRegistration()
    private val debugTrace = false

    init {
        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_ENTERED, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_ENTERED, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_ENTERED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_LEFT, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_LEFT, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_LEFT, e)
                hoveredControl = null
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_MOVED, handler { e ->
                val p = DoubleVector(e.x.toDouble(), e.y.toDouble())

                val c = controls.lastOrNull() { it.bbox.contains(p) }

                if (hoveredControl != null) {
                    if (c == null) {
                        if (debugTrace) {
                            println("left $hoveredControl")
                        }

                        hoveredControl!!.dispatch(MouseEventSpec.MOUSE_LEFT, e)
                        hoveredControl = null
                    } else if (c != hoveredControl) {
                        if (debugTrace) {
                            println("$hoveredControl -> $c")
                        }

                        hoveredControl!!.dispatch(MouseEventSpec.MOUSE_LEFT, e)

                        hoveredControl = c
                        hoveredControl!!.dispatch(MouseEventSpec.MOUSE_ENTERED, e)
                    } else if (c == hoveredControl) {
                        if (debugTrace) {
                            println("moved on $hoveredControl")
                        }

                        c.dispatch(MouseEventSpec.MOUSE_MOVED, e)

                    } else {
                        error("Unexpected")
                    }
                } else {
                    if (c == null) {
                        // do nothing
                    } else {
                        if (debugTrace) {
                            println("entered on $hoveredControl")
                        }

                        hoveredControl = c
                        hoveredControl!!.dispatch(MouseEventSpec.MOUSE_ENTERED, e)
                    }
                }

                dispatchGlobalEvent(MouseEventSpec.MOUSE_MOVED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_DRAGGED, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_DRAGGED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_CLICKED, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_CLICKED, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_CLICKED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_DOUBLE_CLICKED, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_DOUBLE_CLICKED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_PRESSED, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_PRESSED, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_PRESSED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_RELEASED, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_RELEASED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_ENTERED, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_ENTERED, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_ENTERED, e)
            })
        )

        regs.add(
            mouseEventPeer.addEventHandler(MouseEventSpec.MOUSE_WHEEL_ROTATED, handler { e ->
                hoveredControl?.dispatch(MouseEventSpec.MOUSE_WHEEL_ROTATED, e)
                dispatchGlobalEvent(MouseEventSpec.MOUSE_WHEEL_ROTATED, e)
            })
        )
    }

    fun setEventSource(s: MouseEventSource) {
        mouseEventPeer.addEventSource(s)
    }

    fun register(control: UiControl) {
        controls.add(control)
        control.children.forEach(controls::add)
    }

    fun onMouseEvent(e: MouseEventSpec, handler: (UiControl?, MouseEvent) -> Unit): Registration {
        globalMouseEventHandlers.getOrPut(e, { mutableListOf() }).add(handler)
        return object : Registration() {
            override fun doRemove() {
                globalMouseEventHandlers.getValue(e).remove(handler)
            }
        }
    }

    private fun dispatchGlobalEvent(e: MouseEventSpec, event: MouseEvent) {
        globalMouseEventHandlers[e]?.forEach { it(hoveredControl, event) }
    }

    override fun dispose() {
        regs.dispose()
    }
}