/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.event

import org.jetbrains.letsPlot.commons.registration.Registration

internal class CompositeEventSource<EventT> : EventSource<EventT> {
    private var myHandlers: Listeners<EventHandler<EventT>>? = null
    private val myEventSources = ArrayList<EventSource<EventT>>()
    private val myRegistrations = ArrayList<Registration>()

    constructor(vararg sources: EventSource<EventT>) {
        for (s in sources) {
            add(s)
        }
    }

    constructor(sources: Iterable<EventSource<EventT>>) {
        for (s in sources) {
            add(s)
        }
    }

    fun add(source: EventSource<EventT>) {
        myEventSources.add(source)
    }

    fun remove(source: EventSource<out EventT>) {
        myEventSources.remove(source)
    }

    override fun addHandler(handler: EventHandler<EventT>): Registration {
        if (myHandlers == null) {
            myHandlers = object : Listeners<EventHandler<EventT>>() {
                override fun beforeFirstAdded() {
                    for (src in myEventSources) {
                        addHandlerTo(src)
                    }
                }

                override fun afterLastRemoved() {
                    for (hr in myRegistrations) {
                        hr.remove()
                    }
                    myRegistrations.clear()
                    myHandlers = null
                }
            }
        }
        return myHandlers!!.add(handler)
    }

    private fun <PartEventT : EventT> addHandlerTo(src: EventSource<PartEventT>) {
        myRegistrations.add(src.addHandler(object : EventHandler<PartEventT> {
            override fun onEvent(event: PartEventT) {
                myHandlers!!.fire(object : ListenerCaller<EventHandler<EventT>> {
                    override fun call(l: EventHandler<EventT>) {
                        l.onEvent(event)
                    }
                })
            }
        }))
    }
}