package jetbrains.datalore.base.observable.event

/**
 * Interface which should be implemented by events which you fire via [EventListeners]
 * @param <ListenerT>
</ListenerT> */
interface ListenerEvent<ListenerT> {
    fun dispatch(l: ListenerT)
}