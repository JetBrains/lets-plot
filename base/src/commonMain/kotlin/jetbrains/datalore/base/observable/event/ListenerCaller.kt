package jetbrains.datalore.base.observable.event

/**
 * Object which calls listeners inside of [Listeners]
 */
interface ListenerCaller<ListenerT> {
    fun call(l: ListenerT)
}