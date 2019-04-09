package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.Registration

/**
 * Base class for creation of derived properties, i.e. properties whose values are calculated based on other values
 */
abstract class BaseDerivedProperty<ValueT>
protected constructor(
        private var myValue: ValueT) :
        BaseReadableProperty<ValueT>() {

    private var myHandlers: Listeners<EventHandler<in PropertyChangeEvent<ValueT>>>? = null

//    private var myValue: ValueT

//    init {
//        myValue = doGet()
//    }

    /**
     * Start listening to the objects which our value depend on
     */
    protected abstract fun doAddListeners()

    /**
     * Stop listening to the objects which our value depend on
     */
    protected abstract fun doRemoveListeners()

    /**
     * Calculates dependent value
     */
    protected abstract fun doGet(): ValueT

    override fun get(): ValueT {
        return if (myHandlers != null) {
            myValue
        } else {
            doGet()
        }
    }

    protected fun somethingChanged() {
        val newValue = doGet()
        if (myValue == newValue) return

        val event = PropertyChangeEvent(myValue, newValue)
        myValue = newValue

        if (myHandlers != null) {
            myHandlers!!.fire(object : ListenerCaller<EventHandler<in PropertyChangeEvent<ValueT>>> {
                override fun call(l: EventHandler<in PropertyChangeEvent<ValueT>>) {
                    l.onEvent(event)
                }
            })
        }
    }

    override fun addHandler(handler: EventHandler<in PropertyChangeEvent<ValueT>>): Registration {
        if (myHandlers == null) {
            myHandlers = object : Listeners<EventHandler<in PropertyChangeEvent<ValueT>>>() {
                override fun beforeFirstAdded() {
                    myValue = doGet()
                    doAddListeners()
                }

                override fun afterLastRemoved() {
                    doRemoveListeners()
                    myHandlers = null
                }
            }
        }
        return myHandlers!!.add(handler)
    }
}