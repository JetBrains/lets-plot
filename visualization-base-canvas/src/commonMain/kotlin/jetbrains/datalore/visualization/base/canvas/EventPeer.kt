package jetbrains.datalore.visualization.base.canvas

import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import kotlin.reflect.KClass


abstract class EventPeer<SpecT : Enum<SpecT>, EventT>
protected constructor(specClass: KClass<SpecT>) { // originally `specClass` was used to create EnumMap
    private val myEventHandlers: MutableMap<SpecT, Listeners<EventHandler<EventT>>> = HashMap()

    fun addEventHandler(eventSpec: SpecT, handler: EventHandler<EventT>): Registration {
        if (!myEventHandlers.containsKey(eventSpec)) {
            myEventHandlers[eventSpec] = Listeners()
            onSpecAdded(eventSpec)
        }

        val addReg = myEventHandlers[eventSpec]!!.add(handler)
        return Registration.from(object : Disposable {
            override fun dispose() {
                addReg.remove()
                if (myEventHandlers[eventSpec]!!.isEmpty) {
                    myEventHandlers.remove(eventSpec)
                    onSpecRemoved(eventSpec)
                }
            }
        })
    }

    protected fun dispatch(eventSpec: SpecT, event: EventT) {
        myEventHandlers[eventSpec]?.fire(object : ListenerCaller<EventHandler<EventT>> {
            override fun call(l: EventHandler<EventT>) {
                l.onEvent(event)
            }
        })
    }

    protected abstract fun onSpecAdded(spec: SpecT)
    protected abstract fun onSpecRemoved(spec: SpecT)
}
