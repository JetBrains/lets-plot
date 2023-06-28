/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventHandler
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec

internal class SvgEventPeer {
    private var myEventHandlers: MutableMap<SvgEventSpec, Listeners<SvgEventHandler<*>>>? = null
    private var myListeners: Listeners<EventHandler<PropertyChangeEvent<out Set<SvgEventSpec>>>>? = null

    fun handlersSet(): ReadableProperty<Set<SvgEventSpec>> {
        return object : ReadableProperty<Set<SvgEventSpec>> {
            override val propExpr: String
                get() = "$this.handlersProp"

            override fun get(): Set<SvgEventSpec> {
                return handlersKeySet()
            }

            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out Set<SvgEventSpec>>>): Registration {
                if (myListeners == null) {
                    myListeners = Listeners()
                }
                val addReg = myListeners!!.add(handler)
                return object : Registration() {
                    override fun doRemove() {
                        addReg.remove()
                        if (myListeners!!.isEmpty) {
                            myListeners = null
                        }
                    }
                }
            }
        }
    }

    private fun handlersKeySet(): Set<SvgEventSpec> {
        return if (myEventHandlers == null) emptySet() else myEventHandlers!!.keys
    }

    fun <EventT : Event> addEventHandler(spec: SvgEventSpec, handler: SvgEventHandler<EventT>): Registration {
        if (myEventHandlers == null) {
            myEventHandlers = HashMap()
        }
        val eventHandlers = myEventHandlers!!
        if (!eventHandlers.containsKey(spec)) {
            eventHandlers[spec] = Listeners()
        }

        val oldHandlersSet = eventHandlers.keys

        val specListeners = eventHandlers[spec]!!
        val addReg = specListeners.add(handler)
        val disposeReg = object : Registration() {
            override fun doRemove() {
                addReg.remove()
                if (specListeners.isEmpty) {
                    eventHandlers.remove(spec)
                }
            }
        }

        myListeners?.fire(object : ListenerCaller<EventHandler<PropertyChangeEvent<out Set<SvgEventSpec>>>> {
            override fun call(l: EventHandler<PropertyChangeEvent<out Set<SvgEventSpec>>>) {
                l.onEvent(PropertyChangeEvent(oldHandlersSet, handlersKeySet()))
            }
        })

        return disposeReg
    }

    fun <EventT : Event> dispatch(spec: SvgEventSpec, event: EventT, target: SvgNode) {
        if (myEventHandlers != null && myEventHandlers!!.containsKey(spec)) {
            myEventHandlers!![spec]!!.fire(object : ListenerCaller<SvgEventHandler<*>> {
                override fun call(l: SvgEventHandler<*>) {
                    if (event.isConsumed) return
                    @Suppress("UNCHECKED_CAST")
                    val svgEventHandler = l as SvgEventHandler<EventT>
                    svgEventHandler.handle(target, event)
                }
            })
        }
    }
}