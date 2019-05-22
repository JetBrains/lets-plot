package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.plot.base.event.MouseEventSource
import jetbrains.datalore.visualization.plot.base.event.MouseEventSpec

class TileMouseEventPeer(eventSource: MouseEventSource, private val myRect: Rectangle) : MouseEventSource {

    private val myEventPeer: MouseEventPeer = MouseEventPeer()
    private var myInside: Boolean = false
    private var myLastDragMouseEvent: MouseEvent? = null
    private fun unsupportedEventSpec(eventSpec: MouseEventSpec): Boolean {
        return eventSpec === MouseEventSpec.MOUSE_ENTERED || eventSpec === MouseEventSpec.MOUSE_LEFT
    }

    init {
        myEventPeer.addEventSource(eventSource)
        myInside = false
    }

    private fun onlyInsideEventSpec(eventSpec: MouseEventSpec): Boolean {
        return eventSpec === MouseEventSpec.MOUSE_CLICKED ||
                eventSpec === MouseEventSpec.MOUSE_DOUBLE_CLICKED ||
                eventSpec === MouseEventSpec.MOUSE_PRESSED
    }

    override fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return myEventPeer.addEventHandler(eventSpec, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                val inside = myRect.contains(event.location)

                processEnterLeaveCase(inside, event)
                processDragCase(eventSpec, event)

                if (unsupportedEventSpec(eventSpec)) {
                    return
                }

                if (!myInside && onlyInsideEventSpec(eventSpec)) {
                    return
                }

                val point = event.location.sub(myRect.origin)
                eventHandler.onEvent(MouseEvent(point, event.button!!, event.modifiers))
            }
        })
    }

    private fun processEnterLeaveCase(inside: Boolean, event: MouseEvent) {
        if (myInside != inside) {
            myInside = inside
            myEventPeer.dispatch(if (inside) MouseEventSpec.MOUSE_ENTERED else MouseEventSpec.MOUSE_LEFT, event)
        }
    }

    private fun processDragCase(eventSpec: MouseEventSpec, event: MouseEvent) {
        if (eventSpec === MouseEventSpec.MOUSE_DRAGGED) {
            myLastDragMouseEvent = event
        }

        if (eventSpec === MouseEventSpec.MOUSE_MOVED) {
            if (myLastDragMouseEvent != null) {
                myEventPeer.dispatch(MouseEventSpec.MOUSE_RELEASED, myLastDragMouseEvent!!)
            }
            myLastDragMouseEvent = null
        }
    }
}
