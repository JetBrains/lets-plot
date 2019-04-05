package jetbrains.datalore.base.observable.event

class EventListeners<ListenerT, EventT : ListenerEvent<ListenerT>> : Listeners<ListenerT>() {
    fun fire(event: EventT) {
        fire(object : ListenerCaller<ListenerT> {
            override fun call(item: ListenerT) {
                event.dispatch(item)
            }
        })
    }
}