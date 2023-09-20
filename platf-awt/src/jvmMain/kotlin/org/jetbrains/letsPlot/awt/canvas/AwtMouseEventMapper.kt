/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.awt.util.AwtEventUtil
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseEvent as AwtMouseEvent

private const val ENABLE_DEBUG_LOG = false

class AwtMouseEventMapper(
    eventSource: Component,
    private val bounds: Rectangle? = null
) : MouseEventSource {
    private val mouseEventPeer = MouseEventPeer()

    private var state: MouseState = HoverState()
        set(value) {
            if (ENABLE_DEBUG_LOG) {
                println("state(${this@AwtMouseEventMapper.hashCode().toString(32)}): ${field.name} -> ${value.name}")
            }
            field = value
        }

    init {
        eventSource.addMouseListener(object : MouseListener {
            override fun mouseClicked(e: MouseEvent) = state.onMouseEvent(AwtMouseEventType.MOUSE_CLICKED, e)
            override fun mousePressed(e: MouseEvent) = state.onMouseEvent(AwtMouseEventType.MOUSE_PRESSED, e)
            override fun mouseReleased(e: MouseEvent) = state.onMouseEvent(AwtMouseEventType.MOUSE_RELEASED, e)
            override fun mouseEntered(e: MouseEvent) = state.onMouseEvent(AwtMouseEventType.MOUSE_ENTERED, e)
            override fun mouseExited(e: MouseEvent) = state.onMouseEvent(AwtMouseEventType.MOUSE_EXITED, e)
        })
        eventSource.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: MouseEvent) = state.onMouseEvent(AwtMouseEventType.MOUSE_DRAGGED, e)
            override fun mouseMoved(e: MouseEvent) = state.onMouseEvent(AwtMouseEventType.MOUSE_MOVED, e)
        })
    }

    private abstract inner class MouseState(
        val name: String,
        private val canHandleOutsideOfBounds: Boolean = false
    ) {
        fun onMouseEvent(type: AwtMouseEventType, e: AwtMouseEvent) {
            if (canHandleOutsideOfBounds || isHitWithinBounds(e)) {
                log(type.name)
                handleEvent(type, e)
            }
        }

        abstract fun handleEvent(type: AwtMouseEventType, e: AwtMouseEvent)

        fun log(str: String) {
            if (ENABLE_DEBUG_LOG) {
                println("$name(${this@AwtMouseEventMapper.hashCode().toString(32)}): $str")
            }
        }
    }

    private inner class HoverState : MouseState("HoverState") {
        override fun handleEvent(type: AwtMouseEventType, e: MouseEvent) {
            when (type) {
                AwtMouseEventType.MOUSE_PRESSED -> {
                    dispatch(MouseEventSpec.MOUSE_PRESSED, e)
                    state = ButtonDownState()
                }

                AwtMouseEventType.MOUSE_MOVED -> dispatch(MouseEventSpec.MOUSE_MOVED, e)
                AwtMouseEventType.MOUSE_EXITED -> dispatch(MouseEventSpec.MOUSE_LEFT, e)
                AwtMouseEventType.MOUSE_ENTERED -> dispatch(MouseEventSpec.MOUSE_ENTERED, e)
                AwtMouseEventType.MOUSE_DRAGGED -> {} // ignore drag events from another facet
                AwtMouseEventType.MOUSE_RELEASED -> {} // ignore button release (drag end from another facet)
                AwtMouseEventType.MOUSE_CLICKED -> {} // ignore button  (drag end from another facet)
            }
        }
    }

    private inner class ButtonDownState : MouseState("ButtonDownState") {
        override fun handleEvent(type: AwtMouseEventType, e: MouseEvent) {
            when (type) {
                AwtMouseEventType.MOUSE_RELEASED -> dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                AwtMouseEventType.MOUSE_CLICKED -> {
                    when (e.clickCount) {
                        1 -> dispatch(MouseEventSpec.MOUSE_CLICKED, e)
                        2 -> dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, e)
                        else -> {}
                    }
                    state = HoverState()
                }

                AwtMouseEventType.MOUSE_DRAGGED -> {
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
                    state = Dragging()
                }

                else -> {
                    state = HoverState() // safety net
                    error("ButtonDownState: unexpected event - $type")
                }
            }
        }
    }

    private inner class Dragging : MouseState(
        name = "Dragging",
        canHandleOutsideOfBounds = true
    ) {
        override fun handleEvent(type: AwtMouseEventType, e: MouseEvent) = when (type) {
            AwtMouseEventType.MOUSE_DRAGGED -> dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
            AwtMouseEventType.MOUSE_RELEASED -> {
                dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                state = HoverState()
            }

            AwtMouseEventType.MOUSE_ENTERED -> {} // ignored just because noone needed it. Can be handled
            AwtMouseEventType.MOUSE_EXITED -> {} // ignored just because noone needed it. Can be handled
            else -> {
                state = HoverState() // safety net
                error("Dragging: unexpected event - $type")
            }
        }
    }

    private fun dispatch(eventSpec: MouseEventSpec, e: AwtMouseEvent) {
        val mouseEvent = AwtEventUtil.translate(e, bounds?.origin ?: Vector.ZERO)
        mouseEventPeer.dispatch(eventSpec, mouseEvent)
    }

    private fun isHitWithinBounds(event: AwtMouseEvent): Boolean {
        return bounds?.contains(Vector(event.x, event.y)) ?: true
    }

    private enum class AwtMouseEventType {
        MOUSE_CLICKED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_ENTERED,
        MOUSE_EXITED,
        MOUSE_DRAGGED,
        MOUSE_MOVED,
        ;
    }

    override fun addEventHandler(
        eventSpec: MouseEventSpec,
        eventHandler: EventHandler<org.jetbrains.letsPlot.commons.event.MouseEvent>
    ): Registration {
        return mouseEventPeer.addEventHandler(eventSpec, eventHandler)
    }
}
