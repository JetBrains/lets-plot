package jetbrains.datalore.base.observable.event


fun <EventT> handler(onEvent: (EventT) -> Unit): EventHandler<EventT> {
   return object : EventHandler<EventT> {
        override fun onEvent(event: EventT) {
            onEvent(event)
        }
    }
}

