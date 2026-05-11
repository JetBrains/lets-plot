/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.canvas

import org.jetbrains.letsPlot.awt.canvas.AwtMouseEventMapper.AwtMouseEventType.*
import org.jetbrains.letsPlot.awt.util.AwtEventUtil
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventPeer
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import java.awt.Component
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseEvent as AwtMouseEvent

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
            override fun mouseClicked(e: AwtMouseEvent) = state.onMouseEvent(MOUSE_CLICKED, e)
            override fun mousePressed(e: AwtMouseEvent) = state.onMouseEvent(MOUSE_PRESSED, e)
            override fun mouseReleased(e: AwtMouseEvent) = state.onMouseEvent(MOUSE_RELEASED, e)
            override fun mouseEntered(e: AwtMouseEvent) = state.onMouseEvent(MOUSE_ENTERED, e)
            override fun mouseExited(e: AwtMouseEvent) = state.onMouseEvent(MOUSE_EXITED, e)
        })
        eventSource.addMouseMotionListener(object : MouseMotionListener {
            override fun mouseDragged(e: AwtMouseEvent) = state.onMouseEvent(MOUSE_DRAGGED, e)
            override fun mouseMoved(e: AwtMouseEvent) = state.onMouseEvent(MOUSE_MOVED, e)
        })
        eventSource.addMouseWheelListener { e ->
            val mouseEvent = AwtEventUtil.translate(e, bounds?.origin ?: Vector.ZERO)
            mouseEventPeer.dispatch(MouseEventSpec.MOUSE_WHEEL_ROTATED, mouseEvent)
        }
    }

    private abstract inner class MouseState(
        private val canHandleOutsideOfBounds: Boolean
    ) {
        val name: String = checkNotNull(this::class.simpleName)

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

    private inner class HoverState : MouseState(canHandleOutsideOfBounds = false) {
        override fun handleEvent(type: AwtMouseEventType, e: AwtMouseEvent) {
            when (type) {
                MOUSE_PRESSED -> {
                    dispatch(MouseEventSpec.MOUSE_PRESSED, e)
                    // A press begins a gesture sequence: the next event may be DRAGGED, RELEASED,
                    // or even transient ENTERED/EXITED from AWT retargeting before any drag starts.
                    state = ButtonDownState()
                }

                MOUSE_MOVED -> {
                    dispatch(MouseEventSpec.MOUSE_MOVED, e)
                }

                MOUSE_EXITED -> {
                    dispatch(MouseEventSpec.MOUSE_LEFT, e)
                }

                MOUSE_ENTERED -> {
                    dispatch(MouseEventSpec.MOUSE_ENTERED, e)
                }

                MOUSE_WHEEL_ROTATED -> {
                    dispatch(MouseEventSpec.MOUSE_WHEEL_ROTATED, e)
                }

                MOUSE_DRAGGED,
                MOUSE_RELEASED,
                MOUSE_CLICKED -> {} // ignore trailing events from another facet
            }
        }
    }

    // Once a press starts inside the target, the rest of the gesture may legally continue
    // outside component bounds or through ENTERED/EXITED noise caused by retargeting/rebuilds.
    private inner class ButtonDownState : MouseState(canHandleOutsideOfBounds = true) {
        override fun handleEvent(type: AwtMouseEventType, e: AwtMouseEvent) {
            when (type) {
                MOUSE_RELEASED -> {
                    dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                    // RELEASED does not guarantee the gesture was a click yet: AWT commonly delivers
                    // CLICKED afterwards as a separate event, so switch into a short-lived post-release state.
                    state = AwaitingClickState()
                }

                MOUSE_CLICKED -> {
                    dispatchClickEvent(e)
                    state = HoverState()
                }

                MOUSE_DRAGGED -> {
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
                    // The first DRAGGED confirms that this gesture is no longer a plain click path.
                    // From here on we only care about continuing drag updates and the final release.
                    state = DraggingState()
                }

                MOUSE_ENTERED,
                MOUSE_EXITED -> {} // ignore retargeting noise while the press gesture is still active

                MOUSE_MOVED,
                MOUSE_PRESSED,
                MOUSE_WHEEL_ROTATED -> {
                    // ignore stale or conflicting press state and reset
                    state = HoverState()
                }
            }
        }
    }

    // AWT may emit CLICKED after RELEASED, but other noise may arrive first. We keep this
    // dedicated post-release state so that a missing or delayed CLICKED does not leave the mapper
    // pretending the button is still down.
    private inner class AwaitingClickState : MouseState(canHandleOutsideOfBounds = true) {
        override fun handleEvent(type: AwtMouseEventType, e: AwtMouseEvent) {
            when (type) {
                MOUSE_CLICKED -> {
                    dispatchClickEvent(e)
                    // CLICKED completes the simple press-release path we stayed alive for.
                    state = HoverState()
                }

                MOUSE_PRESSED -> {
                    dispatch(MouseEventSpec.MOUSE_PRESSED, e)
                    // If the next real input is already a new press, the previous CLICKED is either
                    // absent or no longer useful, and this press must start a fresh gesture.
                    state = ButtonDownState()
                }

                MOUSE_RELEASED,
                MOUSE_ENTERED,
                MOUSE_EXITED,
                MOUSE_DRAGGED,
                MOUSE_MOVED,
                MOUSE_WHEEL_ROTATED -> {
                    state = HoverState() // ignore post-release noise once waiting for CLICKED is no longer useful
                }
            }
        }
    }

    private inner class DraggingState : MouseState(canHandleOutsideOfBounds = true) {
        override fun handleEvent(type: AwtMouseEventType, e: AwtMouseEvent) {
            when (type) {
                MOUSE_DRAGGED -> {
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
                }

                MOUSE_RELEASED -> {
                    dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                    state = HoverState()
                }

                MOUSE_ENTERED,
                MOUSE_EXITED -> {} // ignore boundary noise while dragging

                MOUSE_CLICKED,
                MOUSE_PRESSED,
                MOUSE_MOVED,
                MOUSE_WHEEL_ROTATED -> {
                    // ignore stale or conflicting drag state and reset
                    state = HoverState()
                }
            }
        }
    }

    private fun dispatchClickEvent(e: AwtMouseEvent) {
        when (e.clickCount) {
            1 -> dispatch(MouseEventSpec.MOUSE_CLICKED, e)
            2 -> dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, e)
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
        MOUSE_WHEEL_ROTATED,
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return mouseEventPeer.addEventHandler(eventSpec, eventHandler)
    }

    companion object {
        private const val ENABLE_DEBUG_LOG = false
    }
}
