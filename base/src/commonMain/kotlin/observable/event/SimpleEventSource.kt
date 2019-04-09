package jetbrains.datalore.base.observable.event

import jetbrains.datalore.base.registration.Registration

class SimpleEventSource<EventT> : EventSource<EventT> {
    private val myListeners = Listeners<EventHandler<in EventT>>()

    fun fire(event: EventT) {
        myListeners.fire(object : ListenerCaller<EventHandler<in EventT>> {
            override fun call(l: EventHandler<in EventT>) {
                l.onEvent(event)
            }
        })
    }

    override fun addHandler(handler: EventHandler<in EventT>): Registration {
        return myListeners.add(handler)
    }
}