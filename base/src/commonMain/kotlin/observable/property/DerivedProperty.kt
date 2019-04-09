package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.EventSource
import jetbrains.datalore.base.registration.Registration
import observable.collections.Collections

/**
 * Simplified version of [BaseDerivedProperty] which can depend on generic observable objects.
 */
abstract class DerivedProperty<ValueT> constructor(initialValue: ValueT, vararg deps: EventSource<*>) :
        BaseDerivedProperty<ValueT>(initialValue) {
//abstract class DerivedProperty<ValueT> constructor(vararg deps: EventSource<*>) :
//        BaseDerivedProperty<ValueT>() {

    private val myDeps: Array<EventSource<*>> = Collections.arrayCopy(deps)
    private var myRegistrations: Array<Registration>? = null

//    protected constructor(vararg deps: EventSource<*>) : this(null, *deps) {}

    override fun doAddListeners() {
        myRegistrations = Array(myDeps.size) { i -> register(myDeps[i]) }
    }

    private fun <EventT> register(dep: EventSource<EventT>): Registration {
        return dep.addHandler(object : EventHandler<EventT> {
            override fun onEvent(event: EventT) {
                somethingChanged()
            }
        })
    }

    override fun doRemoveListeners() {
        for (r in myRegistrations!!) {
            r.remove()
        }
        myRegistrations = null
    }
}