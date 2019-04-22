package jetbrains.datalore.base.observable.event

import jetbrains.datalore.base.function.Predicate
import jetbrains.datalore.base.observable.collections.CollectionAdapter
import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.base.registration.Registration

object EventSources {
    /**
     * Event source which always dispatched the same events on subscription. It's useful for testing and
     * composition. In Rx-like libraries a similar thing is called cold observable.
     */
    fun <EventT> of(vararg events: EventT): EventSource<EventT> {
        return object : EventSource<EventT> {
            override fun addHandler(handler: EventHandler<in EventT>): Registration {
                for (e in events) {
                    handler.onEvent(e)
                }
                return Registration.EMPTY
            }
        }
    }

    fun <EventT> empty(): EventSource<EventT> {
        return composite<EventT>()
    }

    fun <EventT> composite(vararg sources: EventSource<EventT>): EventSource<EventT> {
        return CompositeEventSource<EventT>(*sources)
    }


    fun <EventT> composite(sources: Iterable<EventSource<EventT>>): EventSource<EventT> {
        return CompositeEventSource(sources)
    }

    fun <EventT> filter(source: EventSource<EventT>, pred: Predicate<in EventT>): EventSource<EventT> {
        return object : EventSource<EventT> {
            override fun addHandler(handler: EventHandler<in EventT>): Registration {
                return source.addHandler(object : EventHandler<EventT> {
                    override fun onEvent(event: EventT) {
                        if (pred.test(event)) {
                            handler.onEvent(event)
                        }
                    }
                })
            }
        }
    }

    fun <SourceEventT, TargetEventT> map(src: EventSource<SourceEventT>, f: (SourceEventT) -> TargetEventT): EventSource<TargetEventT> {
        return MappingEventSource<SourceEventT, TargetEventT>(src, f)
    }

    fun <EventT, ItemT> selectList(
            list: ObservableList<ItemT>, selector: (ItemT?) -> EventSource<EventT>): EventSource<EventT> {
        return object : EventSource<EventT> {
            override fun addHandler(handler: EventHandler<in EventT>): Registration {
                val itemRegs = ArrayList<Registration>()
                for (item in list) {
                    itemRegs.add(selector(item).addHandler(handler))
                }


                val listReg = list.addListener(object : CollectionAdapter<ItemT>() {
                    override fun onItemAdded(event: CollectionItemEvent<ItemT>) {
                        itemRegs.add(event.index, selector(event.newItem).addHandler(handler))
                    }

                    override fun onItemRemoved(event: CollectionItemEvent<ItemT>) {
                        itemRegs.removeAt(event.index).remove()
                    }
                })

                return object : Registration() {
                    override fun doRemove() {
                        for (r in itemRegs) {
                            r.remove()
                        }

                        listReg.remove()
                    }
                }
            }
        }
    }
}
