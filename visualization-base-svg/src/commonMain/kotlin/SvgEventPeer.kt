package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.svg.event.SvgEventHandler
import jetbrains.datalore.visualization.base.svg.event.SvgEventSpec

import java.util.EnumMap
import java.util.EnumSet

internal class SvgEventPeer {
    private var myEventHandlers: MutableMap<SvgEventSpec, Listeners<SvgEventHandler<*>>>? = null
    private var myListeners: Listeners<EventHandler<PropertyChangeEvent<Set<SvgEventSpec>>>>? = null

    fun handlersSet(): ReadableProperty<Set<SvgEventSpec>> {
        return object : ReadableProperty<Set<SvgEventSpec>>() {
            val propExpr: String
                get() = "$this.handlersProp"

            fun get(): Set<SvgEventSpec> {
                return handlersKeySet()
            }

            fun addHandler(handler: EventHandler<PropertyChangeEvent<Set<SvgEventSpec>>>): Registration {
                if (myListeners == null) {
                    myListeners = Listeners()
                }
                val addReg = myListeners!!.add(handler)
                return object : Registration() {
                    protected fun doRemove() {
                        addReg.remove()
                        if (myListeners!!.isEmpty()) {
                            myListeners = null
                        }
                    }
                }
            }
        }
    }

    private fun handlersKeySet(): Set<SvgEventSpec> {
        return if (myEventHandlers == null) EnumSet.noneOf(SvgEventSpec::class.java) else myEventHandlers!!.keys
    }

    fun <EventT : Event> addEventHandler(spec: SvgEventSpec, handler: SvgEventHandler<EventT>): Registration {
        if (myEventHandlers == null) {
            myEventHandlers = EnumMap(SvgEventSpec::class.java)
        }
        if (!myEventHandlers!!.containsKey(spec)) {
            myEventHandlers!![spec] = Listeners()
        }

        val oldHandlersSet = myEventHandlers!!.keys

        val addReg = myEventHandlers!![spec].add(handler)
        val disposeReg = object : Registration() {
            protected fun doRemove() {
                addReg.remove()
                if (myEventHandlers!![spec].isEmpty()) {
                    myEventHandlers!!.remove(spec)
                }
            }
        }

        if (myListeners != null) {
            myListeners!!.fire(object : ListenerCaller<EventHandler<PropertyChangeEvent<Set<SvgEventSpec>>>>() {
                fun call(l: EventHandler<PropertyChangeEvent<Set<SvgEventSpec>>>) {
                    l.onEvent(PropertyChangeEvent(oldHandlersSet, handlersKeySet()))
                }
            })
        }

        return disposeReg
    }

    fun <EventT : Event> dispatch(spec: SvgEventSpec, event: EventT, target: SvgNode) {
        if (myEventHandlers != null && myEventHandlers!!.containsKey(spec)) {
            myEventHandlers!![spec].fire(object : ListenerCaller<SvgEventHandler<*>>() {
                fun call(l: SvgEventHandler<*>) {
                    if (event.isConsumed()) return
                    val svgEventHandler = l as SvgEventHandler<EventT>
                    svgEventHandler.handle(target, event)
                }
            })
        }
    }
}