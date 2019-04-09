package jetbrains.datalore.base.observable.event

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.registration.Registration

internal class MappingEventSource<SourceEventT, TargetEventT>(private val mySourceEventSource: EventSource<SourceEventT>, private val myFunction: Function<SourceEventT, TargetEventT>) : EventSource<TargetEventT> {

    override fun addHandler(handler: EventHandler<in TargetEventT>): Registration {
        return mySourceEventSource.addHandler(object : EventHandler<SourceEventT> {
            override fun onEvent(event: SourceEventT) {
                handler.onEvent(myFunction.apply(event))
            }
        })
    }
}