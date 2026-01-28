package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.Registration
import kotlin.math.roundToInt

class ViewportMouseEventSource(
    private val originalSource: MouseEventSource,
    private val getViewportBounds: () -> DoubleRectangle
) : MouseEventSource {

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return originalSource.addEventHandler(eventSpec, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                // 1. Check if the event is within the defined area (Limiting Area)
                // We use the event's raw coordinates against the viewport bounds
                val originalVector = Vector(event.x, event.y)
                val viewportBounds = getViewportBounds()

                if (!viewportBounds.contains(originalVector)) {
                    return
                }

                // 2. Adjust Top-Left Corner (Translation)
                // We round to Int because MouseEvent uses Ints, but Scene/Nodes use Doubles
                val left = viewportBounds.left.roundToInt()
                val top = viewportBounds.top.roundToInt()

                val newLocation = Vector(event.x - left, event.y - top)

                val newEvent = MouseEvent(newLocation, event.button, event.modifiers)

                // 3. Dispatch
                eventHandler.onEvent(newEvent)

                // 4. Back-propagate preventDefault
                if (newEvent.preventDefault) {
                    event.preventDefault = true
                }
            }
        })
    }
}
fun MouseEventSource.withViewport(mapper: () -> DoubleRectangle): MouseEventSource {
    return ViewportMouseEventSource(this, mapper)
}