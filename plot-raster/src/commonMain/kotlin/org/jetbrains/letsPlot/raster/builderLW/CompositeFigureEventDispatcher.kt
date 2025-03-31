/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.builderLW

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.event.MouseWheelEvent
import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.raster.view.CanvasEventDispatcher

class CompositeFigureEventDispatcher() : CanvasEventDispatcher {
    private val dispatchers = LinkedHashMap<Rectangle, CanvasEventDispatcher>()

    fun addEventDispatcher(bounds: Rectangle, eventDispatcher: CanvasEventDispatcher) {
        dispatchers[bounds] = eventDispatcher
    }

    override fun dispatchMouseEvent(kind: MouseEventSpec, e: MouseEvent) {
        val loc = Vector(e.x, e.y)
        val (figureBounds, dispatcher) = dispatchers.entries.firstOrNull { (bounds, _) -> loc in bounds } ?: return
        val xInFigure = loc.x - figureBounds.origin.x
        val yInFigure = loc.y - figureBounds.origin.y

        val eventInFigure = when (e) {
            is MouseWheelEvent -> MouseWheelEvent(xInFigure, yInFigure, e.button, e.modifiers, e.scrollAmount)
            else -> MouseEvent(xInFigure, yInFigure, e.button, e.modifiers)
        }

        dispatcher.dispatchMouseEvent(kind, eventInFigure)
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        // Looks like composite figures in Compose don't need this method because they are all placed in the same
        // container and this container is a dispatcher for all of them.
        return Registration.EMPTY
    }
}