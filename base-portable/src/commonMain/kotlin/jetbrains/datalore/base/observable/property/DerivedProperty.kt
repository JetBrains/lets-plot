/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.EventSource
import jetbrains.datalore.base.registration.Registration

/**
 * Simplified version of [BaseDerivedProperty] which can depend on generic observable objects.
 */
abstract class DerivedProperty<ValueT>(initialValue: ValueT, vararg deps: EventSource<*>) :
    BaseDerivedProperty<ValueT>(initialValue) {

    private val myDeps: Array<EventSource<*>> = Array(deps.size) { i -> deps[i] }
    private var myRegistrations: Array<Registration>? = null

    override fun doAddListeners() {
        myRegistrations = Array(myDeps.size) { i -> register(myDeps[i]) }
    }

    private fun <EventT> register(dep: EventSource<EventT>): Registration {
        return dep.addHandler(object : EventHandler<EventT> {
            override fun onEvent(event: EventT) {
                somethingChanged()
            }
        })
    }

    override fun doRemoveListeners() {
        for (r in myRegistrations!!) {
            r.remove()
        }
        myRegistrations = null
    }
}