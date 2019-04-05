package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration

/**
 * Read only wrapper for a property
 */
class ReadOnlyProperty<ValueT>(private val myProperty: ReadableProperty<ValueT>) : BaseReadableProperty<ValueT>() {

    val propExpr: String
        get() = "readOnly(" + myProperty.getPropExpr() + ")"

    fun get(): ValueT {
        return myProperty.get()
    }

    fun addHandler(handler: EventHandler<PropertyChangeEvent<ValueT>>): Registration {
        return myProperty.addHandler(handler)
    }
}