/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.input

import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSpec.*
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.handler
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.livemap.core.ecs.AbstractSystem
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsContext

class MouseInputSystem(componentManager: EcsComponentManager) : AbstractSystem<EcsContext>(componentManager) {

    private val myRegs = CompositeRegistration()
    private var myLocation: Vector? = null
    private var myDragStartLocation: Vector? = null
    private var myDragCurrentLocation: Vector? = null
    private var myDragDelta: Vector? = null
    private var myPressEvent: InputMouseEvent? = null
    private var myClickEvent: InputMouseEvent? = null
    private var myDoubleClickEvent: InputMouseEvent? = null

    override fun init(context: EcsContext) {
        myRegs.add(context.eventSource.addEventHandler(MOUSE_DOUBLE_CLICKED, handler(this::onMouseDoubleClicked)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_PRESSED, handler(this::onMousePressed)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_RELEASED, handler(this::onMouseReleased)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_DRAGGED, handler(this::onMouseDragged)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_MOVED, handler( this::onMouseMoved )))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_CLICKED, handler( this::onMouseClicked )))
    }

    override fun update(context: EcsContext, dt: Double) {
        myDragCurrentLocation?.let {
            if ( it != myDragStartLocation) {
                myDragDelta = it.sub(myDragStartLocation!!) // (dragCurrent != null) => dragStart can't be null
                myDragStartLocation = it
            }
        }

        for (entity in getEntities(MouseInputComponent::class)) {
            entity.getComponent<MouseInputComponent>().apply {
                dragDistance = myDragDelta
                press = myPressEvent
                click = myClickEvent
                doubleClick = myDoubleClickEvent
            }
        }

        myPressEvent = null
        myClickEvent = null
        myDoubleClickEvent = null
        myDragDelta = null
    }

    override fun destroy() {
        myRegs.dispose()
    }

    private fun onMouseClicked(mouseEvent: MouseEvent) {
        if (mouseEvent.button === Button.LEFT) {
            myClickEvent = InputMouseEvent(mouseEvent.location)
            myDragCurrentLocation = null
            myDragStartLocation = null
        }
    }

    private fun onMousePressed(mouseEvent: MouseEvent) {
        if (mouseEvent.button === Button.LEFT) {
            myPressEvent = InputMouseEvent(mouseEvent.location)
            myDragStartLocation = mouseEvent.location
        }
    }

    private fun onMouseReleased(mouseEvent: MouseEvent) {
        if (mouseEvent.button === Button.LEFT) {
            myDragCurrentLocation = null
            myDragStartLocation = null
        }
    }

    private fun onMouseDragged(mouseEvent: MouseEvent) {
        if (myDragStartLocation != null) {
            myDragCurrentLocation = mouseEvent.location
        }
    }

    private fun onMouseDoubleClicked(mouseEvent: MouseEvent) {
        if (mouseEvent.button === Button.LEFT) {
            myDoubleClickEvent = InputMouseEvent(mouseEvent.location)
        }
    }

    private fun onMouseMoved(mouseEvent: MouseEvent) {
        myLocation = mouseEvent.location
    }
}