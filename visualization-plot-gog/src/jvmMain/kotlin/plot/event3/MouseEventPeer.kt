package jetbrains.datalore.visualization.plot.gog.plot.event3

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.plot.gog.core.event3.MouseEventSource
import java.util.EnumMap
import kotlin.collections.ArrayList

class MouseEventPeer : MouseEventSource {
    private val myEventHandlers = EnumMap<MouseEventSource.MouseEventSpec, Listeners<EventHandler<MouseEvent>>>(MouseEventSource.MouseEventSpec::class.java)
    private val myEventSources = ArrayList<MouseEventSource>()
    private val mySourceRegistrations = EnumMap<MouseEventSource.MouseEventSpec, CompositeRegistration>(MouseEventSource.MouseEventSpec::class.java)

    override fun addEventHandler(eventSpec: MouseEventSource.MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        if (!myEventHandlers.containsKey(eventSpec)) {
            myEventHandlers.put(eventSpec, Listeners())
            onAddSpec(eventSpec)
        }
        val addReg = myEventHandlers.get(eventSpec)?.add(eventHandler)
        return object : Registration() {
            override fun doRemove() {
                addReg?.remove()
                if (myEventHandlers.get(eventSpec)!!.isEmpty) {
                    myEventHandlers.remove(eventSpec)
                    onRemoveSpec(eventSpec)
                }
            }
        }
    }

    fun dispatch(eventSpec: MouseEventSource.MouseEventSpec, mouseEvent: MouseEvent) {
        if (myEventHandlers.containsKey(eventSpec)) {
            myEventHandlers.get(eventSpec)?.fire(object : ListenerCaller<EventHandler<MouseEvent>> {
                override fun call(l: EventHandler<MouseEvent>) {
                    l.onEvent(mouseEvent)
                }
            })
        }
    }

    fun addEventSource(eventSource: MouseEventSource) {
        myEventHandlers.keys.forEach { eventSpec -> startHandleSpecInSource(eventSource, eventSpec) }
        myEventSources.add(eventSource)
    }

    private fun onAddSpec(eventSpec: MouseEventSource.MouseEventSpec) {
        myEventSources.forEach { eventSource -> startHandleSpecInSource(eventSource, eventSpec) }
    }

    private fun startHandleSpecInSource(eventSource: MouseEventSource, eventSpec: MouseEventSource.MouseEventSpec) {
        val registration = eventSource.addEventHandler(eventSpec, object : EventHandler<MouseEvent> {
            override fun onEvent(event: MouseEvent) {
                dispatch(eventSpec, event);
            }
        })

        if (!mySourceRegistrations.containsKey(eventSpec)) {
            mySourceRegistrations.put(eventSpec, CompositeRegistration())
        }
        mySourceRegistrations.get(eventSpec)?.add(registration)
    }

    private fun onRemoveSpec(eventSpec: MouseEventSource.MouseEventSpec) {
        if (mySourceRegistrations.containsKey(eventSpec)) {
            mySourceRegistrations.remove(eventSpec)?.dispose()
        }
    }
}
