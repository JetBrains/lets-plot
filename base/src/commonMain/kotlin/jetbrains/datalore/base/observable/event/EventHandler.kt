package jetbrains.datalore.base.observable.event

/**
 * Handler for events fired by [EventSource]
 */
interface EventHandler<in EventT> {
    fun onEvent(event: EventT)
}