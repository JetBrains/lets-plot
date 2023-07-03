/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.Registration

/**
 * Base class for creation of derived properties,
 * i.e. properties whose values are calculated based on other values
 */
abstract class BaseDerivedProperty<ValueT>(private var myValue: ValueT) : BaseReadableProperty<ValueT>() {

    private var myHandlers: Listeners<EventHandler<PropertyChangeEvent<out ValueT>>>? = null

    /**
     * Start listening to the objects which our value depend on
     */
    protected abstract fun doAddListeners()

    /**
     * Stop listening to the objects which our value depend on
     */
    protected abstract fun doRemoveListeners()

    /**
     * Calculates dependent value
     */
    protected abstract fun doGet(): ValueT

    override fun get(): ValueT {
        return if (myHandlers != null) {
            myValue
        } else {
            doGet()
        }
    }

    protected fun somethingChanged() {
        val newValue = doGet()
        if (myValue == newValue) return

        val event = PropertyChangeEvent(myValue, newValue)
        myValue = newValue

        if (myHandlers != null) {
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
                override fun beforeFirstAdded() {
                    myValue = doGet()
                    doAddListeners()
                }

                override fun afterLastRemoved() {
                    doRemoveListeners()
                    myHandlers = null
                }
            }
        }
        return myHandlers!!.add(handler)
    }
}