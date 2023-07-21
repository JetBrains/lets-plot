/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.input

import org.jetbrains.letsPlot.commons.intern.concurrent.Lock
import org.jetbrains.letsPlot.commons.intern.concurrent.execute
import org.jetbrains.letsPlot.commons.event.Button
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.event.MouseEventSpec.*
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.livemap.core.ecs.AbstractSystem
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponentManager
import org.jetbrains.letsPlot.livemap.core.ecs.EcsContext

class MouseInputSystem(
    componentManager: EcsComponentManager,
) : AbstractSystem<EcsContext>(componentManager) {

    private data class InputEvent(
        val moveEvent: InputMouseEvent? = null,
        val pressEvent: InputMouseEvent? = null,
        val clickEvent: InputMouseEvent? = null,
        val doubleClickEvent: InputMouseEvent? = null,
        val dragState: DragState? = null,
    )

    private val myRegs = CompositeRegistration()
    private var myDragState: DragState? = null
    private val rawMouseEventsQueueLock = Lock()
    private val rawMouseEventsQueue = mutableListOf<Pair<MouseEventSpec, MouseEvent>>()

    private fun enqueue(mouseEventSpec: MouseEventSpec) = object : EventHandler<MouseEvent> {
        override fun onEvent(event: MouseEvent) {
            rawMouseEventsQueueLock.execute {
                rawMouseEventsQueue.add(mouseEventSpec to event)
            }
        }
    }

    override fun init(context: EcsContext) {
        myRegs.add(context.eventSource.addEventHandler(MOUSE_DOUBLE_CLICKED, enqueue(MOUSE_DOUBLE_CLICKED)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_PRESSED, enqueue(MOUSE_PRESSED)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_RELEASED, enqueue(MOUSE_RELEASED)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_DRAGGED, enqueue(MOUSE_DRAGGED)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_MOVED, enqueue(MOUSE_MOVED)))
        myRegs.add(context.eventSource.addEventHandler(MOUSE_CLICKED, enqueue(MOUSE_CLICKED)))
    }

    override fun update(context: EcsContext, dt: Double) {
        val events = rawMouseEventsQueueLock.execute {
            val copy = rawMouseEventsQueue.toList()
            rawMouseEventsQueue.clear()
            copy
        }

        // Dragging started on previous frame - now it's an event continuation
        if (myDragState?.started == true) {
            myDragState = myDragState?.copy(
                started = false,
                dragging = true
            )
        }

        if (myDragState?.stopped == true) {
            myDragState = null
        }

        val inputEvent = handleEvent(events) ?: InputEvent(dragState = myDragState)

        for (entity in getEntities(MouseInputComponent::class)) {
            entity.getComponent<MouseInputComponent>().apply {
                moveEvent = inputEvent.moveEvent
                pressEvent = inputEvent.pressEvent
                clickEvent = inputEvent.clickEvent
                doubleClickEvent = inputEvent.doubleClickEvent
                dragState = inputEvent.dragState
            }
        }
    }

    override fun destroy() {
        myRegs.dispose()
    }

    private fun handleEvent(events: List<Pair<MouseEventSpec, MouseEvent>>): InputEvent? {
        if (events.isEmpty()) {
            return  null
        }

        var pressEvent: InputMouseEvent? = null
        var moveEvent: InputMouseEvent? = null
        var clickEvent: InputMouseEvent? = null
        var doubleClickEvent: InputMouseEvent? = null

        events.forEach { (mouseEventSpec, event) ->
            when (mouseEventSpec) {
                MOUSE_MOVED -> moveEvent = InputMouseEvent(event.location)
                MOUSE_DRAGGED -> updateDragState(event.location, isStopEvent = false)
                MOUSE_RELEASED -> if (event.button == Button.LEFT) updateDragState(event.location, isStopEvent = true)
                else -> Unit
            }

            if (event.button == Button.LEFT) {
                when (mouseEventSpec) {
                    MOUSE_CLICKED -> clickEvent = InputMouseEvent(event.location)
                    MOUSE_DOUBLE_CLICKED -> doubleClickEvent = InputMouseEvent(event.location)
                    MOUSE_PRESSED -> pressEvent = InputMouseEvent(event.location)
                    else -> Unit
                }
            }
        }

        return InputEvent(
            moveEvent = moveEvent,
            pressEvent = pressEvent,
            clickEvent = clickEvent,
            doubleClickEvent = doubleClickEvent,
            dragState = myDragState,
        )
    }

    private fun updateDragState(location: Vector, isStopEvent: Boolean) {
        val newOrigin = myDragState?.origin ?: location
        myDragState = myDragState?.let {
            when {
                it.started -> when {
                    !isStopEvent -> DragState(origin = newOrigin, location = location, started = true)
                    isStopEvent -> DragState(origin = newOrigin, location = location, started = true, stopped = true)
                    else -> throw IllegalStateException()
                }
                it.dragging -> when {
                    !isStopEvent -> DragState(origin = newOrigin, location = location, dragging = true)
                    isStopEvent -> DragState(origin = newOrigin, location = location, stopped = true)
                    else -> throw IllegalStateException()
                }
                it.stopped -> when {
                    !isStopEvent -> DragState(origin = newOrigin, location = location, stopped = true)
                    isStopEvent -> DragState(origin = newOrigin, location = location, stopped = true)
                    else -> throw IllegalStateException()
                }
                else -> throw IllegalStateException()
            }
        } ?: run {
            when {
                isStopEvent -> null // click event - do not start drag
                !isStopEvent -> DragState(origin = location, location = location, started = true)
                else -> null
            }
        }
    }
}
