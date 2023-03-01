/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.dom

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.dom.*
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.EventPeer
import kotlinx.browser.document
import org.w3c.dom.Element

private const val DRAG_TRIGGER_DISTANCE = 3.0

class DomEventPeer(
    private val myEventTarget: Element,
    private val myTargetBounds: Rectangle
) : EventPeer<MouseEventSpec, MouseEvent>(MouseEventSpec::class) {
    private var myButtonPressCoord: DoubleVector? = null
    private var myIsDragging = false
    private var myLastDragEndEventTimestamp: Number = 0
    private var myDocumentMouseEventsRegistration: Registration = Registration.EMPTY
        set(value) {
            field.dispose()
            field = value
        }

    init {
        handle(DomEventType.MOUSE_ENTER) { dispatch(MouseEventSpec.MOUSE_ENTERED, translate(it)) }
        handle(DomEventType.MOUSE_LEAVE) { dispatch(MouseEventSpec.MOUSE_LEFT, translate(it)) }

        handle(DomEventType.CLICK) {
            if (isPartOfDragEndEvent(it)) return@handle
            dispatch(MouseEventSpec.MOUSE_CLICKED, translate(it))
        }

        handle(DomEventType.DOUBLE_CLICK) {
            if (isPartOfDragEndEvent(it)) return@handle
            dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, translate(it))
        }

        handle(DomEventType.MOUSE_DOWN) {
            // Prevent text selection outside of Canvas element in Safari
            it.preventDefault()
            myButtonPressCoord = DoubleVector(it.x, it.y)
            dispatch(MouseEventSpec.MOUSE_PRESSED, translate(it))
        }

        handle(DomEventType.MOUSE_UP) {
            // handled by document event handler
            if (myIsDragging) return@handle

            myButtonPressCoord = null
            dispatch(MouseEventSpec.MOUSE_RELEASED, translate(it))
        }

        handle(DomEventType.MOUSE_MOVE) {
            // handled by document event handler
            if (myIsDragging) return@handle

            if (myButtonPressCoord == null) {
                dispatch(MouseEventSpec.MOUSE_MOVED, translate(it))
            } else {
                val distance = myButtonPressCoord?.subtract(DoubleVector(it.x, it.y))?.length() ?: 0.0
                if (distance > DRAG_TRIGGER_DISTANCE) {
                    myIsDragging = true
                    myDocumentMouseEventsRegistration = Registration.from(
                        document.on(DomEventType.MOUSE_MOVE, ::onDocumentMouseMove),
                        document.on(DomEventType.MOUSE_UP, ::onDocumentMouseUp)
                    )
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, translate(it))
                }
            }
        }
    }

    private fun onDocumentMouseUp(it: org.w3c.dom.events.MouseEvent) {
        if (myIsDragging) {
            myLastDragEndEventTimestamp = it.timeStamp
        }

        myIsDragging = false
        myButtonPressCoord = null
        myDocumentMouseEventsRegistration = Registration.EMPTY
        dispatch(MouseEventSpec.MOUSE_RELEASED, translate(it))
    }

    private fun onDocumentMouseMove(event: org.w3c.dom.events.MouseEvent) {
        if (myIsDragging) {
            dispatch(MouseEventSpec.MOUSE_DRAGGED, translate(event))
        } else {
            // Move without dragging - should not be here. Could be a focus magic. Unsubscribe from document events.
            myDocumentMouseEventsRegistration = Registration.EMPTY
        }
    }

    private fun handle(
        eventSpec: DomEventType<org.w3c.dom.events.MouseEvent>,
        handler: (org.w3c.dom.events.MouseEvent) -> Unit
    ) {
        myEventTarget.on(eventSpec, consumer = {
            if (isHitOnTarget(it)) {
                handler(it)
            }
        })
    }

    override fun onSpecAdded(spec: MouseEventSpec) {}

    override fun onSpecRemoved(spec: MouseEventSpec) {}

    /*
        Check if `event` is a part of drag end event to suppress MOUSE_CLICK and MOUSE_DOUBLECLICK events.
        This happens when user pans a map and releases LMB over a map button (e.g. center). It triggers button action.
     */
    private fun isPartOfDragEndEvent(event: DomMouseEvent) = myLastDragEndEventTimestamp == event.timeStamp

    private fun isHitOnTarget(event: DomMouseEvent): Boolean {
        val v = Vector(event.offsetX.toInt(), event.offsetY.toInt())
        return myTargetBounds.contains(v)
    }

    private fun translate(event: DomMouseEvent): MouseEvent {
        val targetRect = myEventTarget.getBoundingClientRect()
        return MouseEvent(
            event.clientX - targetRect.x.toInt() - myTargetBounds.origin.x,
            event.clientY - targetRect.y.toInt() - myTargetBounds.origin.y,
            DomEventUtil.getButton(event),
            DomEventUtil.getModifiers(event)
        )
    }
}