/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.platf.dom

import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.letsPlot.commons.event.*
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.distance
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.platf.w3c.dom.events.DomEventType
import org.jetbrains.letsPlot.platf.w3c.dom.on
import org.w3c.dom.Element
import org.w3c.dom.events.WheelEvent
import kotlin.math.roundToInt

typealias DomMouseEvent = org.w3c.dom.events.MouseEvent
typealias DomWheelEvent = WheelEvent

private const val ENABLE_DEBUG_LOG = false

class DomMouseEventMapper(
    private val eventTarget: Element,

    // The area where the events are handled.
    // The area is relative to the top-left corner of the event target.
    private val eventArea: DoubleRectangle = DoubleRectangle.XYWH(
        x = 0,
        y = 0,
        width = eventTarget.clientWidth.toDouble(),
        height = eventTarget.clientHeight.toDouble()
    )
) : MouseEventSource, Disposable {

    private val regs = CompositeRegistration()
    private val mouseEventPeer = MouseEventPeer()

    private var state: MouseState = MouseOutsideState()
        set(value) {
            if (ENABLE_DEBUG_LOG) {
                println(
                    "state($${
                        this@DomMouseEventMapper.hashCode().toString(36)
                    }): ${field::class.simpleName} -> ${value::class.simpleName}"
                )
            }
            field = value
        }

    init {
        if (ENABLE_DEBUG_LOG) {
            println("DomMouseEventMapper(${this.hashCode().toString(36)}): subarea=$eventArea")
        }

        fun addHandler(eventSpec: DomEventType<out DomMouseEvent>) {
            eventTarget
                .on(eventSpec, consumer = { mouseEvent -> state.onMouseEvent(eventSpec, mouseEvent) })
                .also(regs::add)
        }

        addHandler(DomEventType.CLICK)
        addHandler(DomEventType.DOUBLE_CLICK)
        addHandler(DomEventType.MOUSE_ENTER)
        addHandler(DomEventType.MOUSE_LEAVE)
        addHandler(DomEventType.MOUSE_DOWN)
        addHandler(DomEventType.MOUSE_UP)
        addHandler(DomEventType.MOUSE_MOVE)
        addHandler(DomEventType.MOUSE_WHEEL)
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return mouseEventPeer.addEventHandler(eventSpec, eventHandler)
    }

    private fun dispatch(eventSpec: MouseEventSpec, domMouseEvent: DomMouseEvent) {
        // DON'T USE preventDefault() - there is a Chrome bug that prevents dragging outside the canvas:
        // https://issues.chromium.org/issues/41195706

        // To test the Chrome bug do the following:
        //  - launch the demo to generate the demo html file
        //  - open the demo directory
        //  - run http server in the demo directory
        //  - create the iframe.html file with the content <html><body><iframe src="http://10.251.0.253:8181/demo.html"/></body></html>
        //  - open iframe.html in Chrome
        // Now dragging outside the iframe can be tested.
        //domMouseEvent.preventDefault() // Fix for Safari to prevent selection when user drags outside a canvas

        val coord = toEventTargetOffsetCoord(domMouseEvent).subtract(eventArea.origin)
        val x = coord.x.roundToInt()//domMouseEvent.regionX.roundToInt()
        val y = coord.y.roundToInt()//domMouseEvent.regionY.roundToInt()
        val button = DomEventUtil.getButton(domMouseEvent)
        val modifiers = DomEventUtil.getModifiers(domMouseEvent)

        val mouseEvent = when (domMouseEvent) {
            is WheelEvent -> MouseWheelEvent(x, y, button, modifiers, domMouseEvent.deltaY)
            else -> MouseEvent(x, y, button, modifiers)
        }

        if (ENABLE_DEBUG_LOG) {
            println("DomMouseEventMapper(${this.hashCode().toString(36)}): dispatching $eventSpec at ($x, $y)")
        }
        mouseEventPeer.dispatch(eventSpec, mouseEvent)

        if (mouseEvent.preventDefault) {
            domMouseEvent.preventDefault()
        }
    }

    private fun inEventArea(e: DomMouseEvent): Boolean {
        return toEventTargetOffsetCoord(e) in eventArea
    }

    // Convert event coordinates to the local coordinate system of the event target
    // (i.e. the top-left corner of the event target is (0, 0))
    // Can't use offsetX/Y because it's relative to the element under the mouse pointer, not the event target.
    // This means that with gggrid even when the mouse is over the most right cell, offsetX is still
    // in range (0, cell.width), not (cell.left, cell.right)
    private fun toEventTargetOffsetCoord(e: DomMouseEvent): DoubleVector {
        val offsetX = e.pageX - window.pageXOffset - eventTarget.getBoundingClientRect().left
        val offsetY = e.pageY - window.pageYOffset - eventTarget.getBoundingClientRect().top

        return DoubleVector(offsetX, offsetY)
    }

    private abstract inner class MouseState {
        fun onMouseEvent(type: DomEventType<out DomMouseEvent>, e: DomMouseEvent) {
            val (x, y) = toEventTargetOffsetCoord(e)
            log("${type.name} at ($x, $y)")
            handleEvent(type, e)
        }

        abstract fun handleEvent(type: DomEventType<out DomMouseEvent>, e: DomMouseEvent)

        fun log(str: String) {
            if (ENABLE_DEBUG_LOG) {
                println("${this::class.simpleName}(${this@DomMouseEventMapper.hashCode().toString(36)}): $str")
            }
        }
    }

    private inner class MouseOutsideState : MouseState() {
        override fun handleEvent(type: DomEventType<out DomMouseEvent>, e: DomMouseEvent) {
            if (!inEventArea(e)) return

            // mouseover with already pressed button -> drag from another element -> not hover for this element
            if (e.buttons > 0) return

            when (type) {
                DomEventType.MOUSE_ENTER, DomEventType.MOUSE_MOVE -> {
                    dispatch(MouseEventSpec.MOUSE_ENTERED, e)
                    state = MouseHoverState()
                }
                // Ignore buttons/leave events
            }
        }
    }

    private inner class MouseHoverState : MouseState() {
        override fun handleEvent(type: DomEventType<out DomMouseEvent>, e: DomMouseEvent) {
            if (!inEventArea(e)) {
                dispatch(MouseEventSpec.MOUSE_LEFT, e)
                state = MouseOutsideState()
                return
            }

            when (type) {
                DomEventType.MOUSE_DOWN -> {
                    dispatch(MouseEventSpec.MOUSE_PRESSED, e)
                    state = MousePressedState(pressEvent = e)
                }

                DomEventType.MOUSE_LEAVE -> {
                    dispatch(MouseEventSpec.MOUSE_LEFT, e)
                    state = MouseOutsideState()
                }

                DomEventType.MOUSE_MOVE -> dispatch(MouseEventSpec.MOUSE_MOVED, e)
                DomEventType.MOUSE_WHEEL -> dispatch(MouseEventSpec.MOUSE_WHEEL_ROTATED, e as DomWheelEvent)
                DomEventType.MOUSE_ENTER -> {} // should be handled by OutsideState
                DomEventType.MOUSE_UP, DomEventType.CLICK, DomEventType.DOUBLE_CLICK -> {} // clicks handled by MouseClickState. Ignore clicks here to prevent ghost clicks on drag end.
            }
        }
    }

    private inner class MousePressedState(
        private val pressEvent: DomMouseEvent,
        private val draggingTriggerDistance: Double = 3.0
    ) : MouseState() {

        override fun handleEvent(type: DomEventType<out DomMouseEvent>, e: DomMouseEvent) {
            when (type) {
                // After MOUSE_UP may be CLICK or CLICK+DOUBLE_CLICK event.
                DomEventType.MOUSE_UP -> {
                    dispatch(MouseEventSpec.MOUSE_RELEASED, e)
                    state = MouseClickState()
                }

                DomEventType.MOUSE_MOVE -> {
                    if (distance(e.x, e.y, pressEvent.x, pressEvent.y) > draggingTriggerDistance) {
                        dispatch(MouseEventSpec.MOUSE_DRAGGED, pressEvent)
                        dispatch(MouseEventSpec.MOUSE_DRAGGED, e)
                        state = MouseDragState()
                    }
                }
            }
        }
    }

    /**
     * State when the mouse button is released. It can be a click or click + double click.
     * We can't guess which event it will be, so we keep the state as MouseClickState until move events.
     */
    private inner class MouseClickState : MouseState() {
        override fun handleEvent(type: DomEventType<out DomMouseEvent>, e: DomMouseEvent) {
            if (!inEventArea(e)) {
                dispatch(MouseEventSpec.MOUSE_LEFT, e)
                state = MouseOutsideState()
                return
            }

            when (type) {
                DomEventType.CLICK -> dispatch(MouseEventSpec.MOUSE_CLICKED, e)
                DomEventType.DOUBLE_CLICK -> dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, e)

                DomEventType.MOUSE_MOVE -> {
                    dispatch(MouseEventSpec.MOUSE_MOVED, e)
                    state = MouseHoverState()
                }

                DomEventType.MOUSE_DOWN -> {
                    dispatch(MouseEventSpec.MOUSE_PRESSED, e)
                    state = MousePressedState(pressEvent = e)
                }

                DomEventType.MOUSE_LEAVE -> {
                    dispatch(MouseEventSpec.MOUSE_LEFT, e)
                    state = MouseOutsideState()
                }
            }
        }
    }

    private inner class MouseDragState : MouseState() {
        private var myDocumentMouseEventsRegistration = Registration.from(
            document.on(DomEventType.MOUSE_MOVE, ::onDocumentMouseMove),
            document.on(DomEventType.MOUSE_UP, ::onDocumentMouseUp)
        )

        private fun onDocumentMouseMove(event: DomMouseEvent) {
            dispatch(MouseEventSpec.MOUSE_DRAGGED, event)
        }

        private fun onDocumentMouseUp(e: DomMouseEvent) {
            dispatch(MouseEventSpec.MOUSE_RELEASED, e)
            state = MouseHoverState()
            myDocumentMouseEventsRegistration.dispose()
        }

        override fun handleEvent(type: DomEventType<out DomMouseEvent>, e: DomMouseEvent) {
            // All required events handled by onDocumentMouseMove()
        }
    }

    override fun dispose() {
        regs.dispose()
    }
}
