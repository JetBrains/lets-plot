package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.Registration
import kotlin.jvm.JvmOverloads

/**
 * A simple implementation of Read/Write property which stores the value in a field
 */
open class ValueProperty<ValueT>
@JvmOverloads
constructor(
        private var myValue: ValueT? = null) :
        BaseReadableProperty<ValueT?>(),
        Property<ValueT?> {

    private var myHandlers: Listeners<EventHandler<PropertyChangeEvent<ValueT?>>>? = null

    override val propExpr: String
        get() = "valueProperty()"

    override fun get(): ValueT? {
        return myValue
    }

    override fun set(value: ValueT?) {
        if (value == myValue) return
        val oldValue = myValue
        myValue = value

        fireEvents(oldValue, myValue)
    }

    protected fun fireEvents(oldValue: ValueT?, newValue: ValueT?) {
        if (myHandlers != null) {
            val event = PropertyChangeEvent<ValueT?>(oldValue, newValue)
            myHandlers!!.fire(object : ListenerCaller<EventHandler<PropertyChangeEvent<ValueT?>>> {
                override fun call(l: EventHandler<PropertyChangeEvent<ValueT?>>) {
                    l.onEvent(event)
                }
            })
        }
    }

    override fun addHandler(handler: EventHandler<PropertyChangeEvent<ValueT?>>): Registration {
        if (myHandlers == null) {
            myHandlers = object : Listeners<EventHandler<PropertyChangeEvent<ValueT?>>>() {
                override fun afterLastRemoved() {
                    myHandlers = null
                }
            }
        }

        return myHandlers!!.add(handler)
    }
}