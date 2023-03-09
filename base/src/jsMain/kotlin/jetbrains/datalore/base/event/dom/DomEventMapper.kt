/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.event.dom

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil.getButton
import jetbrains.datalore.base.event.dom.DomEventUtil.getModifiers
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.js.dom.on
import jetbrains.datalore.base.registration.Registration
import kotlinx.browser.document
import org.w3c.dom.Element

typealias DomMouseEvent = org.w3c.dom.events.MouseEvent

private const val ENABLE_DEBUG_LOG = false

class DomEventMapper(
    private val myEventTarget: Element,
    private val myTargetBounds: DoubleRectangle? = null,
    private val destMouseEventPeer: (MouseEventSpec, MouseEvent) -> Unit
) {
    private var state: MouseState = HoverState()
        set(value) {
            if (ENABLE_DEBUG_LOG) {
                println("state($${this@DomEventMapper.hashCode()}): ${field::class.simpleName} -> ${value::class.simpleName}")
            }
            field = value
        }

    init {
        handle(DomEventType.CLICK) { state.onMouseEvent(DomEventType.CLICK, it) }
        handle(DomEventType.DOUBLE_CLICK) { state.onMouseEvent(DomEventType.DOUBLE_CLICK, it) }
        handle(DomEventType.MOUSE_ENTER) { state.onMouseEvent(DomEventType.MOUSE_ENTER, it) }
        handle(DomEventType.MOUSE_LEAVE) { state.onMouseEvent(DomEventType.MOUSE_LEAVE, it) }
        handle(DomEventType.MOUSE_DOWN) { state.onMouseEvent(DomEventType.MOUSE_DOWN, it) }
        handle(DomEventType.MOUSE_UP) { state.onMouseEvent(DomEventType.MOUSE_UP, it) }
        handle(DomEventType.MOUSE_MOVE) { state.onMouseEvent(DomEventType.MOUSE_MOVE, it) }
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

    private fun translate(event: DomMouseEvent): MouseEvent {
        val targetClientOrigin = myEventTarget.getBoundingClientRect().let { DoubleVector(it.x, it.y) }
        val eventClientCoord = DoubleVector(event.clientX.toDouble(), event.clientY.toDouble())
        val targetAbsoluteOrigin = myTargetBounds?.origin ?: DoubleVector.ZERO

        val eventTargetCoord = eventClientCoord.subtract(targetClientOrigin).subtract(targetAbsoluteOrigin)
        return MouseEvent(
            eventTargetCoord.x.toInt(),
            eventTargetCoord.y.toInt(),
            getButton(event),
            getModifiers(event)
        )
    }

    abstract inner class MouseState {
        abstract fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent)
        fun log(str: String) {
            if (ENABLE_DEBUG_LOG) {
                println("${this::class.simpleName}(${this@DomEventMapper.hashCode()}): $str")
            }
        }
    }

    open inner class HoverState : MouseState() {
        override fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent) {
            log(type.name)
            if (type == DomEventType.MOUSE_DOWN) {
                dispatch(MouseEventSpec.MOUSE_PRESSED, e)
                state = ButtonDownState(eventCoord = DoubleVector(e.x, e.y))
                return
            }

            // Any event with already pressed button -> drag from another element, supress mouse events handling
            if (e.buttons > 0) {
                state = InvalidDragging()
                return
            }

            when (type) {
                DomEventType.CLICK -> dispatch(MouseEventSpec.MOUSE_CLICKED, e)
                DomEventType.DOUBLE_CLICK -> dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, e)
                DomEventType.MOUSE_MOVE -> dispatch(MouseEventSpec.MOUSE_MOVED, e)
                DomEventType.MOUSE_LEAVE -> dispatch(MouseEventSpec.MOUSE_LEFT, e)
                DomEventType.MOUSE_ENTER -> dispatch(MouseEventSpec.MOUSE_ENTERED, e)
            }
        }
    }

    inner class ButtonDownState(
        private val eventCoord: DoubleVector,
        private val draggingTriggerDistance: Double = 3.0
    ) : MouseState() {

        override fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent) {
            log(type.name)
            when (type) {
                DomEventType.MOUSE_UP -> {
                    dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                    state = HoverState()
                }

                DomEventType.MOUSE_MOVE -> {
                    if (DoubleVector(e.x, e.y).subtract(eventCoord).length() > draggingTriggerDistance) {
                        dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
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
            document.on(DomEventType.MOUSE_MOVE, ::onDocumentMouseMove),
            document.on(DomEventType.CLICK, ::onDocumentClick)
        )

        // All required events handled by onDocumentMouseMove()
        override fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent) {}

        private fun onDocumentMouseMove(event: DomMouseEvent) {
            dispatch(MouseEventSpec.MOUSE_DRAGGED, event)
        }

        private fun onDocumentClick(e: DomMouseEvent) {
            dispatch(MouseEventSpec.MOUSE_RELEASED, e)
            state = HoverState()
            myDocumentMouseEventsRegistration.dispose()
        }
    }

    inner class InvalidDragging : MouseState() {
        override fun onMouseEvent(type: DomEventType<DomMouseEvent>, e: DomMouseEvent) {
            log(type.name)

            if (e.buttons == 0.toShort() && type != DomEventType.MOUSE_UP) {
                // Drag released outside the target - restore HoverState
                // Yet don't set HoverState on MOUSE_UP as CLICK event will trigger UI elements
                state = HoverState()
            }
        }
    }
}