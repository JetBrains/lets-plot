/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.event.dom

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.dom.DomEventUtil.getButton
import jetbrains.datalore.base.event.dom.DomEventUtil.getModifiers
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.js.dom.DomEventListener
import jetbrains.datalore.base.js.dom.DomEventType
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import org.w3c.dom.events.EventTarget

typealias DomMouseEvent = org.w3c.dom.events.MouseEvent

class DomEventMapper(
    private val myEventTarget: EventTarget,
    private val destMouseEventPeer: (MouseEventSpec, MouseEvent) -> Unit
) : Disposable {
    private val regs = CompositeRegistration()
    private var myButtonPressed = false
    private var myDragging = false
    private var myButtonPressCoord: Vector? = null
    private val myDragThreshold = 3.0

    init {
        handle(DomEventType.MOUSE_ENTER) {
            dispatch(MouseEventSpec.MOUSE_ENTERED, it)
        }

        handle(DomEventType.MOUSE_LEAVE) {
            dispatch(MouseEventSpec.MOUSE_LEFT, it)
        }

        handle(DomEventType.CLICK) {
            if (!myDragging) {
                dispatch(MouseEventSpec.MOUSE_CLICKED, it)
            }
            myDragging = false
        }

        handle(DomEventType.DOUBLE_CLICK) {
            dispatch(MouseEventSpec.MOUSE_DOUBLE_CLICKED, it)
        }

        handle(DomEventType.MOUSE_DOWN) {
            myButtonPressed = true
            myButtonPressCoord = Vector(it.x.toInt(), it.y.toInt())
            dispatch(MouseEventSpec.MOUSE_PRESSED, it)
        }

        handle(DomEventType.MOUSE_UP) {
            myButtonPressed = false
            myButtonPressCoord = null
            myDragging = false
            dispatch(MouseEventSpec.MOUSE_RELEASED, it)
        }

        handle(DomEventType.MOUSE_MOVE) {
            if (myDragging) {
                dispatch(MouseEventSpec.MOUSE_DRAGGED, it)
            }
            else if (myButtonPressed && !myDragging) {
                val distance = myButtonPressCoord?.sub(Vector(it.x.toInt(), it.y.toInt()))?.length() ?: 0.0
                if (distance > myDragThreshold) {
                    myDragging = true
                    dispatch(MouseEventSpec.MOUSE_DRAGGED, it)
                } else {
                    // Do not generate move event (just in case - can be changed if needed)
                }
            } else if (!myButtonPressed && !myDragging) {
                dispatch(MouseEventSpec.MOUSE_MOVED, it)
            }
        }
    }

    private fun dispatch(eventSpec: MouseEventSpec, mouseEvent: DomMouseEvent) {
        val translatedEvent = MouseEvent(
            mouseEvent.offsetX.toInt(),
            mouseEvent.offsetY.toInt(),
            getButton(mouseEvent),
            getModifiers(mouseEvent)
        )
        destMouseEventPeer.invoke(eventSpec, translatedEvent)
    }

    private fun handle(eventSpec: DomEventType<DomMouseEvent>, handler: (DomMouseEvent) -> Unit) {
        val listener = DomEventListener<DomMouseEvent> {
            handler(it)
            return@DomEventListener false
        }

        myEventTarget.addEventListener(eventSpec.name, listener)

        regs.add(object : Registration() {
            override fun doRemove() {
                myEventTarget.removeEventListener(eventSpec.name, listener)
            }
        })
    }

    override fun dispose() {
        regs.dispose()
    }
}
