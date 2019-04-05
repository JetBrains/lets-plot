package jetbrains.datalore.base.observable.event

import jetbrains.datalore.base.registration.Registration

/**
 * Source of events of type EventT
 */
interface EventSource<EventT> {
    fun addHandler(handler: EventHandler<EventT>): Registration
}