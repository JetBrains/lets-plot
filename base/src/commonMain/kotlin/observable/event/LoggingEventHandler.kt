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