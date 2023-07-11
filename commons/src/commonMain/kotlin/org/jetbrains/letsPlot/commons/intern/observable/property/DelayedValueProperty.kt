/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import org.jetbrains.letsPlot.commons.registration.Registration
import kotlin.jvm.JvmOverloads

class DelayedValueProperty<ValueT>
@JvmOverloads
constructor(
    private var myValue: ValueT? = null
) :
    BaseReadableProperty<ValueT?>(),
    Property<ValueT?> {

    private var myHandlers: Listeners<EventHandler<PropertyChangeEvent<out ValueT?>>>? = null
    private var myPendingEvent: PropertyChangeEvent<out ValueT?>? = null

    override val propExpr: String
        get() = "delayedProperty()"

    override fun get(): ValueT? {
        return myValue
    }

    override fun set(value: ValueT?) {
        if (value == myValue) return
        val oldValue = myValue
        myValue = value

        if (myPendingEvent != null) {
            throw IllegalStateException()
        }
        myPendingEvent =
            PropertyChangeEvent(oldValue, myValue)
    }

    fun flush() {
        if (myHandlers != null) {
            myHandlers!!.fire(object : ListenerCaller<EventHandler<PropertyChangeEvent<out ValueT?>>> {
                override fun call(l: EventHandler<PropertyChangeEvent<out ValueT?>>) {
                    l.onEvent(myPendingEvent!!)
                }
            })
        }
        myPendingEvent = null
    }

    override fun addHandler(handler: EventHandler<PropertyChangeEvent<out ValueT?>>): Registration {
        if (myHandlers == null) {
            myHandlers = object : Listeners<EventHandler<PropertyChangeEvent<out ValueT?>>>() {
                override fun afterLastRemoved() {
                    myHandlers = null
                }
            }
        }
        return myHandlers!!.add(handler)
    }
}