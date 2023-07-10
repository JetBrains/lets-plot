/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import org.jetbrains.letsPlot.commons.registration.Registration

/**
 * A simple implementation of Read/Write property which stores the value in a field
 */
open class ValueProperty<ValueT>(private var myValue: ValueT) :
    BaseReadableProperty<ValueT>(),
    Property<ValueT> {

    private var myHandlers: Listeners<EventHandler<PropertyChangeEvent<out ValueT>>>? = null

    override val propExpr: String
        get() = "valueProperty()"

    override fun get(): ValueT {
        return myValue
    }

    override fun set(value: ValueT) {
        if (value == myValue) return
        val oldValue = myValue
        myValue = value

        fireEvents(oldValue, myValue)
    }

    private fun fireEvents(oldValue: ValueT, newValue: ValueT) {
        if (myHandlers != null) {
            val event =
                PropertyChangeEvent(oldValue, newValue)
            myHandlers!!.fire(object : ListenerCaller<EventHandler<PropertyChangeEvent<out ValueT>>> {
                override fun call(l: EventHandler<PropertyChangeEvent<out ValueT>>) {
                    l.onEvent(event)
                }
            })
        }
    }

    override fun addHandler(handler: EventHandler<PropertyChangeEvent<out ValueT>>): Registration {
        if (myHandlers == null) {
            myHandlers = object : Listeners<EventHandler<PropertyChangeEvent<out ValueT>>>() {
                override fun afterLastRemoved() {
                    myHandlers = null
                }
            }
        }

        return myHandlers!!.add(handler)
    }
}