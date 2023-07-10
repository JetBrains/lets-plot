/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.event

import jetbrains.datalore.base.registration.Registration

class SimpleEventSource<EventT> : EventSource<EventT> {
    private val myListeners = Listeners<EventHandler<EventT>>()

    fun fire(event: EventT) {
        myListeners.fire(object : ListenerCaller<EventHandler<EventT>> {
            override fun call(l: EventHandler<EventT>) {
                l.onEvent(event)
            }
        })
    }

    override fun addHandler(handler: EventHandler<EventT>): Registration {
        return myListeners.add(handler)
    }
}