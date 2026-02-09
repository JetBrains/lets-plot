package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import kotlin.math.roundToInt

class ChildMouseEventSource(
    private val parentSource: MouseEventSource,
    private val getViewportBounds: () -> DoubleRectangle
) : MouseEventSource, Disposable {

    private val mouseEventPeer = MouseEventPeer()
    private val upstreamRegistrations = CompositeRegistration()

    private var state: MouseState = HoverState()
        set(value) {
            if (ENABLE_DEBUG_LOG) {
                println("state(${this@ChildMouseEventSource.hashCode().toString(32)}): ${field.name} -> ${value.name}")
            }
            field = value
        }

    init {
        // Subscribe to all events from the original source
        val specs = listOf(
            MouseEventSpec.MOUSE_PRESSED,
            MouseEventSpec.MOUSE_RELEASED,
            MouseEventSpec.MOUSE_CLICKED,
            MouseEventSpec.MOUSE_DOUBLE_CLICKED,
            MouseEventSpec.MOUSE_MOVED,
            MouseEventSpec.MOUSE_DRAGGED,
            MouseEventSpec.MOUSE_ENTERED,
            MouseEventSpec.MOUSE_LEFT,
            MouseEventSpec.MOUSE_WHEEL_ROTATED
        )

        specs.forEach { spec ->
            upstreamRegistrations.add(
                parentSource.addEventHandler(spec, object : EventHandler<MouseEvent> {
                    override fun onEvent(event: MouseEvent) {
                        state.onMouseEvent(spec, event)
                    }
                })
            )
        }
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return mouseEventPeer.addEventHandler(eventSpec, eventHandler)
    }

    override fun dispose() {
        upstreamRegistrations.dispose()
    }

    private abstract inner class MouseState(
        val name: String,
        private val canHandleOutsideOfBounds: Boolean
    ) {
        fun onMouseEvent(type: MouseEventSpec, e: MouseEvent) {
            if (canHandleOutsideOfBounds || isHitWithinBounds(e)) {
                log(type.name)
                handleEvent(type, e)
            }
        }

        abstract fun handleEvent(type: MouseEventSpec, e: MouseEvent)

        fun log(str: String) {
            if (ENABLE_DEBUG_LOG) {
                println("$name(${this@ChildMouseEventSource.hashCode().toString(32)}): $str")
            }
        }
    }

    private inner class HoverState : MouseState("HoverState", canHandleOutsideOfBounds = false) {
        override fun handleEvent(type: MouseEventSpec, e: MouseEvent) {
            when (type) {
                MouseEventSpec.MOUSE_PRESSED -> {
                    dispatch(MouseEventSpec.MOUSE_PRESSED, e)
                    state = ButtonDownState()
                }
                MouseEventSpec.MOUSE_MOVED -> dispatch(MouseEventSpec.MOUSE_MOVED, e)
                MouseEventSpec.MOUSE_ENTERED -> dispatch(MouseEventSpec.MOUSE_ENTERED, e)
                MouseEventSpec.MOUSE_LEFT -> dispatch(MouseEventSpec.MOUSE_LEFT, e)
                MouseEventSpec.MOUSE_WHEEL_ROTATED -> dispatch(MouseEventSpec.MOUSE_WHEEL_ROTATED, e)

                // Ignore others
                MouseEventSpec.MOUSE_DRAGGED -> {}
                MouseEventSpec.MOUSE_RELEASED -> {}
                MouseEventSpec.MOUSE_CLICKED -> {}
                MouseEventSpec.MOUSE_DOUBLE_CLICKED -> {}
            }
        }
    }

    private inner class ButtonDownState : MouseState("ButtonDownState", canHandleOutsideOfBounds = false) {
        override fun handleEvent(type: MouseEventSpec, e: MouseEvent) {
            when (type) {
                MouseEventSpec.MOUSE_RELEASED -> {
                    // Do NOT transition to HoverState yet.
                    // Wait for the subsequent MOUSE_CLICKED event.
                    dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                }
                MouseEventSpec.MOUSE_CLICKED -> {
                    dispatch(MouseEventSpec.MOUSE_CLICKED, e)
                    state = HoverState()
                }
                MouseEventSpec.MOUSE_DOUBLE_CLICKED -> {
                    dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, e)
                    state = HoverState()
                }
                MouseEventSpec.MOUSE_DRAGGED -> {
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
                    state = DraggingState()
                }
                else -> {
                    // Safety net: if we get MOVED or ENTERED while thinking button is down,
                    // something is wrong (or we missed the click), so reset to Hover.
                    state = HoverState()
                }
            }
        }
    }

    private inner class DraggingState : MouseState("DraggingState", canHandleOutsideOfBounds = true) {
        override fun handleEvent(type: MouseEventSpec, e: MouseEvent) {
            when (type) {
                MouseEventSpec.MOUSE_DRAGGED -> dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
                MouseEventSpec.MOUSE_RELEASED -> {
                    dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                    state = HoverState()
                }
                // Ignore while dragging
                MouseEventSpec.MOUSE_ENTERED -> {}
                MouseEventSpec.MOUSE_LEFT -> {}
                MouseEventSpec.MOUSE_MOVED -> {}
                else -> state = HoverState()
            }
        }
    }

    private fun isHitWithinBounds(event: MouseEvent): Boolean {
        return getViewportBounds().contains(Vector(event.x, event.y))
    }

    private fun dispatch(eventSpec: MouseEventSpec, e: MouseEvent) {
        val viewportBounds = getViewportBounds()

        val left = viewportBounds.left.roundToInt()
        val top = viewportBounds.top.roundToInt()

        val translatedEvent = if (left == 0 && top == 0) {
            e
        } else {
            MouseEvent(
                e.x - left,
                e.y - top,
                e.button,
                e.modifiers
            )
        }

        mouseEventPeer.dispatch(eventSpec, translatedEvent)

        if (translatedEvent.preventDefault) {
            e.preventDefault = true
        }
    }

    companion object {
        private const val ENABLE_DEBUG_LOG = false
    }
}

fun MouseEventSource.child(bounds: () -> DoubleRectangle): ChildMouseEventSource {
    return ChildMouseEventSource(this, bounds)
}
