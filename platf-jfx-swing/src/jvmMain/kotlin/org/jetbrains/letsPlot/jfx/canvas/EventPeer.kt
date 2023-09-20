/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.canvas

import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import kotlin.reflect.KClass

abstract class EventPeer<SpecT : Enum<SpecT>, EventT>
protected constructor(
    @Suppress("UNUSED_PARAMETER") specClass: KClass<SpecT> // originally `specClass` was used to create EnumMap
) {
    private val myEventHandlers: MutableMap<SpecT, Listeners<EventHandler<EventT>>> = HashMap()

    fun addEventHandler(eventSpec: SpecT, handler: EventHandler<EventT>): Registration {
        if (!myEventHandlers.containsKey(eventSpec)) {
            myEventHandlers[eventSpec] = Listeners()
            onSpecAdded(eventSpec)
        }

        val addReg = myEventHandlers[eventSpec]!!.add(handler)
        return Registration.from(object : Disposable {
            override fun dispose() {
                addReg.remove()
                if (myEventHandlers[eventSpec]!!.isEmpty) {
                    myEventHandlers.remove(eventSpec)
                    onSpecRemoved(eventSpec)
                }
            }
        })
    }

    fun dispatch(eventSpec: SpecT, event: EventT) {
        myEventHandlers[eventSpec]?.fire(object : ListenerCaller<EventHandler<EventT>> {
            override fun call(l: EventHandler<EventT>) {
                l.onEvent(event)
            }
        })
    }

    protected abstract fun onSpecAdded(spec: SpecT)
    protected abstract fun onSpecRemoved(spec: SpecT)
}