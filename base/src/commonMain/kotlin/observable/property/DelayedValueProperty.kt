package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.Registration
import kotlin.jvm.JvmOverloads

class DelayedValueProperty<ValueT>
@JvmOverloads
constructor(
        private var myValue: ValueT? = null) :
        BaseReadableProperty<ValueT?>(),
        Property<ValueT?> {

    private var myHandlers: Listeners<EventHandler<in PropertyChangeEvent<ValueT?>>>? = null
    private var myPendingEvent: PropertyChangeEvent<ValueT?>? = null

    override val propExpr: String
        get() = "delayedProperty()"

    override fun get(): ValueT? {
        return myValue
    }

    override fun set(value: ValueT?) {
        if (value == myValue) return
        val oldValue = myValue
        myValue = value

        if (myPendingEvent != null) {
            throw IllegalStateException()
        }
        myPendingEvent = PropertyChangeEvent(oldValue, myValue)
    }

    fun flush() {
        if (myHandlers != null) {
            myHandlers!!.fire(object : ListenerCaller<EventHandler<in PropertyChangeEvent<ValueT?>>> {
                override fun call(l: EventHandler<in PropertyChangeEvent<ValueT?>>) {
                    l.onEvent(myPendingEvent!!)
                }
            })
        }
        myPendingEvent = null
    }

    override fun addHandler(handler: EventHandler<in PropertyChangeEvent<ValueT?>>): Registration {
        if (myHandlers == null) {
            myHandlers = object : Listeners<EventHandler<in PropertyChangeEvent<ValueT?>>>() {
                override fun afterLastRemoved() {
                    myHandlers = null
                }
            }
        }
        return myHandlers!!.add(handler)
    }
}