/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.dom

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.js.dom.DomEventType.Companion.CLICK
import jetbrains.datalore.base.js.dom.DomEventType.Companion.DOUBLE_CLICK
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_DOWN
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_ENTER
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_LEAVE
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_MOVE
import jetbrains.datalore.base.js.dom.DomEventType.Companion.MOUSE_UP
import jetbrains.datalore.base.js.dom.on
import jetbrains.datalore.base.registration.Registration
import kotlinx.browser.document
import org.w3c.dom.Element

private const val DRAG_TRIGGER_DISTANCE = 3.0
private const val ENABLE_DEBUG_LOG = false
typealias DomMouseEvent = org.w3c.dom.events.MouseEvent

class DomEventPeer(
    private val myEventTarget: Element,
    private val myTargetBounds: DoubleRectangle? = null,
    private val destMouseEventPeer: (MouseEventSpec, MouseEvent) -> Unit
) {
    private var state: MouseState = HoverState()
        set(value) {
            if (ENABLE_DEBUG_LOG) {
                println("state($${this@DomEventPeer.hashCode()}): ${field::class.simpleName} -> ${value::class.simpleName}")
            }
            field = value
        }

    init {
        handle(CLICK) { state.onMouseEvent(CLICK, it) }
        handle(DOUBLE_CLICK) { state.onMouseEvent(DOUBLE_CLICK, it) }
        handle(MOUSE_ENTER) { state.onMouseEvent(MOUSE_ENTER, it) }
        handle(MOUSE_LEAVE) { state.onMouseEvent(MOUSE_LEAVE, it) }
        handle(MOUSE_DOWN) { state.onMouseEvent(MOUSE_DOWN, it) }
        handle(MOUSE_UP) { state.onMouseEvent(MOUSE_UP, it) }
        handle(MOUSE_MOVE) { state.onMouseEvent(MOUSE_MOVE, it) }
    }

    private fun handle(
        eventSpec: DomEventType<DomMouseEvent>,
        handler: (DomMouseEvent) -> Unit
    ) {
        myEventTarget.on(eventSpec, consumer = {
            if (isHitOnTarget(it)) {
                handler(it)
            }
        })
    }

    private fun isHitOnTarget(event: DomMouseEvent): Boolean {
        val bbox = myTargetBounds ?: return true
        return bbox.contains(DoubleVector(event.offsetX, event.offsetY))
    }

    private fun dispatch(eventSpec: MouseEventSpec, mouseEvent: DomMouseEvent) {
        destMouseEventPeer.invoke(eventSpec, translate(mouseEvent))
    }

    private fun dispatch(eventSpec: DomEventType<DomMouseEvent>, mouseEvent: DomMouseEvent) {
        val mouseEventSpec: MouseEventSpec = when (eventSpec) {
            CLICK -> MOUSE_CLICKED
            DOUBLE_CLICK -> MOUSE_DOUBLE_CLICKED
            MOUSE_ENTER -> MOUSE_ENTERED
            MOUSE_LEAVE -> MOUSE_LEFT
            MOUSE_DOWN -> MOUSE_PRESSED
            MOUSE_UP -> MOUSE_RELEASED
            MOUSE_MOVE -> MOUSE_MOVED
            else -> null.also { println("Unsupported event type: $eventSpec") }
        } ?: return

        dispatch(mouseEventSpec, mouseEvent)
    }

    private fun translate(event: DomMouseEvent): MouseEvent {
        val targetClientOrigin = myEventTarget.getBoundingClientRect().let { DoubleVector(it.x, it.y) }
        val eventClientCoord = DoubleVector(event.clientX.toDouble(), event.clientY.toDouble())
        val targetAbsoluteOrigin = myTargetBounds?.origin ?: DoubleVector.ZERO

        val eventTargetCoord = eventClientCoord.subtract(targetClientOrigin).subtract(targetAbsoluteOrigin)
        return MouseEvent(
            eventTargetCoord.x.toInt(),
            eventTargetCoord.y.toInt(),
            DomEventUtil.getButton(event),
            DomEventUtil.getModifiers(event)
        )
    }

    abstract inner class MouseState {
        abstract fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent)
        fun log(str: String) {
            if (ENABLE_DEBUG_LOG) {
                println("${this::class.simpleName}(${this@DomEventPeer.hashCode()}): $str")
            }
        }
    }

    open inner class HoverState : MouseState() {
        override fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent) {
            log(type.name)
            if (type == MOUSE_DOWN) {
                dispatch(MOUSE_PRESSED, e)
                state = DraggingTrial(dragStartCoord = DoubleVector(e.x, e.y))
                return
            }

            // Not a MOUSE_DOWN -> drag from another facet
            if (e.buttons > 0) {
                state = InvalidDragging()
                return
            }

            dispatch(type, e)
        }
    }

    inner class DraggingTrial(
        private val dragStartCoord: DoubleVector
    ) : MouseState() {

        override fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent) {
            log(type.name)
            when (type) {
                MOUSE_UP -> {
                    dispatch(type, e)
                    state = HoverState()
                }

                MOUSE_MOVE -> {
                    if (DoubleVector(e.x, e.y).subtract(dragStartCoord).length() > DRAG_TRIGGER_DISTANCE) {
                        dispatch(MOUSE_DRAGGED, e)
                        state = Dragging()
                    }
                }
            }
        }
    }

    inner class Dragging : MouseState() {
        // Listen for CLICK, not MOUSE_UP, as DRAG_END event consists of two events - MOUSE_UP and then MOUSE_CLICK.
        // MOUSE_CLICK event, if not handled properly, hits UI elements and triggers action.
        private var myDocumentMouseEventsRegistration = Registration.from(
            document.on(MOUSE_MOVE, ::onDocumentMouseMove),
            document.on(CLICK, ::onDocumentClick)
        )

        // All required events handled by onDocumentMouseMove()
        override fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent) {}

        private fun onDocumentMouseMove(event: DomMouseEvent) {
            dispatch(MOUSE_DRAGGED, event)
        }

        private fun onDocumentClick(e: DomMouseEvent) {
            dispatch(MOUSE_RELEASED, e)
            state = HoverState()
            myDocumentMouseEventsRegistration.dispose()
        }
    }

    inner class InvalidDragging : MouseState() {
        override fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent) {
            log(type.name)

            if (e.buttons == 0.toShort() && type != MOUSE_UP) {
                // Drag released outside the target - restore HoverState
                // Yet don't set HoverState on MOUSE_UP as CLICK event will trigger UI elements
                state = HoverState()
            }
        }
    }
}
