/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.event

/**
 * Event Handler which logs all events for test purposes
 */
class LoggingEventHandler<EventT> : EventHandler<EventT> {
    private val myEvents = ArrayList<EventT>()

    val events: List<EventT>
        get() = myEvents

    override fun onEvent(event: EventT) {
        myEvents.add(event)
    }
}