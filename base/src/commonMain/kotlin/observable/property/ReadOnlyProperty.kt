package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration

/**
 * Read only wrapper for a property
 */
class ReadOnlyProperty<ValueT>(private val myProperty: ReadableProperty<ValueT>) : BaseReadableProperty<ValueT>() {

    override val propExpr: String
        get() = "readOnly(" + myProperty.propExpr + ")"

    override fun get(): ValueT {
        return myProperty.get()
    }

    override fun addHandler(handler: EventHandler<in PropertyChangeEvent<ValueT>>): Registration {
        return myProperty.addHandler(handler)
    }
}