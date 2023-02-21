/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.core

import jetbrains.datalore.base.function.Consumer
import jetbrains.datalore.base.function.Runnable
import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.EventSource
import jetbrains.datalore.base.observable.property.*
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.mapper.transform.Transformer

/**
 * Utility class for synchronizer creation
 */
object Synchronizers {
    private val EMPTY: Synchronizer = object : Synchronizer {
        override fun attach(ctx: SynchronizerContext) {}

        override fun detach() {}
    }

    fun <SourceT, TargetT> forSimpleRole(
        mapper: Mapper<*, *>,
        source: List<SourceT>,
        target: MutableList<TargetT>,
        factory: MapperFactory<SourceT, TargetT>
    ): SimpleRoleSynchronizer<SourceT, TargetT> {
        return SimpleRoleSynchronizer(mapper, source, target, factory)
    }

    fun <SourceT, MappedT, TargetItemT, TargetT : TargetItemT> forObservableRole(
        mapper: Mapper<*, *>,
        source: SourceT,
        transformer: Transformer<SourceT, ObservableList<MappedT>>,
        target: MutableList<TargetItemT>,
        factory: MapperFactory<MappedT, TargetT>
    ): RoleSynchronizer<MappedT, TargetT> {
        return TransformingObservableCollectionRoleSynchronizer(mapper, source, transformer, target, factory)
    }

    fun <SourceT, TargetItemT, TargetT : TargetItemT> forObservableRole(
        mapper: Mapper<*, *>,
        source: ObservableList<out SourceT>,
        target: MutableList<TargetItemT>,
        factory: MapperFactory<SourceT, TargetT>
    ): RoleSynchronizer<SourceT, TargetT> {
        return forObservableRole(mapper, source, target, factory, null)
    }

    fun <SourceT, TargetItemT, TargetT : TargetItemT> forObservableRole(
        mapper: Mapper<*, *>,
        source: ObservableList<out SourceT>,
        target: MutableList<TargetItemT>,
        factory: MapperFactory<SourceT, TargetT>,
        errorMapperFactory: MapperFactory<SourceT, TargetT>?
    ): RoleSynchronizer<SourceT, TargetT> {
        return ObservableCollectionRoleSynchronizer(mapper, source, target, factory, errorMapperFactory)
    }

//    fun <SourceT, TargetT, KindTargetT : TargetT> forConstantRole(
//            mapper: Mapper<*, *>,
//            source: SourceT,
//            target: MutableList<TargetT>,
//            factory: MapperFactory<SourceT, KindTargetT>): RoleSynchronizer<SourceT, KindTargetT> {
//        val result = object : BaseCollectionRoleSynchronizer<SourceT, KindTargetT>(mapper) {
//            protected fun onAttach() {
//                super.onAttach()
//                val mapper = createMapper(source)
//                getModifiableMappers().add(0, mapper)
//                target.add(getModifiableMappers().get(0).getTarget())
//                processMapper(mapper)
//            }
//        }
//        result.addMapperFactory(factory)
//        return result
//    }

//    fun <SourceT, TargetT> forConstantRole(
//            mapper: Mapper<*, *>,
//            source: SourceT,
//            target: WritableProperty<TargetT>,
//            factory: MapperFactory<SourceT, TargetT>): RoleSynchronizer<SourceT, TargetT> {
//        return SingleChildRoleSynchronizer(mapper, Properties.constant(source), target, factory)
//    }

    fun <SourceT, TargetT> forSingleRole(
        mapper: Mapper<*, *>,
        source: ReadableProperty<out SourceT?>,
        target: WritableProperty<in TargetT?>,
        factory: MapperFactory<SourceT, TargetT>
    ): RoleSynchronizer<SourceT, TargetT> {

        return SingleChildRoleSynchronizer(mapper, source, target, factory)
    }

    fun <ValueT> forPropsOneWay(
        source: ReadableProperty<out ValueT>,
        target: WritableProperty<in ValueT?>
    ): Synchronizer {
        return object : RegistrationSynchronizer() {
            override fun doAttach(ctx: SynchronizerContext): Registration {
                target.set(source.get())
                return source.addHandler(object : EventHandler<PropertyChangeEvent<out ValueT>> {
                    override fun onEvent(event: PropertyChangeEvent<out ValueT>) {
                        target.set(event.newValue)
                    }
                })
            }
        }
    }

    fun <ValueT> forPropsTwoWay(source: Property<ValueT>, target: Property<ValueT>): Synchronizer {
        return object : Synchronizer {
            private var myOldValue: ValueT? = null
            private var myRegistration: Registration? = null

            override fun attach(ctx: SynchronizerContext) {
                myOldValue = source.get()
                myRegistration = PropertyBinding.bindTwoWay(source, target)
            }

            override fun detach() {
                myRegistration!!.remove()
                @Suppress("UNCHECKED_CAST")
                target.set(myOldValue as ValueT)
            }
        }
    }

    /**
     * Creates a synchronizer which invokes the specified runnable on changes to the property
     */
//    fun <ValueT> forProperty(property: ReadableProperty<ValueT>, sync: Runnable): Synchronizer {
//        return forEventSource(property, sync)
//    }

    /**
     * Creates a synchronizer which invokes the specified runnable on changes to the collection
     */
//    fun <ElementT> forCollection(
//            collection: ObservableCollection<ElementT>, sync: Runnable): Synchronizer {
//        return object : RegistrationSynchronizer() {
//            protected fun doAttach(ctx: SynchronizerContext): Registration {
//                val r = collection.addListener(object : CollectionAdapter<ElementT>() {
//                    fun onItemAdded(event: CollectionItemEvent<out ElementT>) {
//                        sync.run()
//                    }
//
//                    fun onItemRemoved(event: CollectionItemEvent<out ElementT>) {
//                        sync.run()
//                    }
//                })
//                sync.run()
//                return r
//            }
//        }
//    }
//
//    fun <ItemT> forCollectionItems(collection: ObservableCollection<out ItemT>,
//                                   itemWatcher: Function<ItemT, Registration>): Synchronizer {
//        return forCollectionItems(collection, itemWatcher, object : Runnable {
//            override fun run() {
//
//            }
//        })
//    }
//
//    fun <ItemT> forCollectionItems(collection: ObservableCollection<out ItemT>,
//                                   itemWatcher: Function<ItemT, Registration>, sync: Runnable): Synchronizer {
//        val watcher = CollectionItemsWatcher(itemWatcher, sync)
//        return forRegistration(object : Supplier<Registration> {
//            override fun get(): Registration {
//                return watcher.watch(collection)
//            }
//        })
//    }
//
//    fun forRegistration(reg: Supplier<Registration>): Synchronizer {
//        return object : RegistrationSynchronizer() {
//            protected fun doAttach(ctx: SynchronizerContext): Registration {
//                return reg.get()
//            }
//        }
//    }

    fun forRegistration(r: Registration): Synchronizer {
        return object : Synchronizer {
            override fun attach(ctx: SynchronizerContext) {}

            override fun detach() {
                r.remove()
            }
        }
    }

    fun forDisposable(disposable: Disposable): Synchronizer {
        return object : Synchronizer {
            override fun attach(ctx: SynchronizerContext) {}

            override fun detach() {
                disposable.dispose()
            }
        }
    }

    fun forDisposables(vararg disposables: Disposable): Synchronizer {
        return object : Synchronizer {
            override fun attach(ctx: SynchronizerContext) {}

            override fun detach() {
                for (disposable in disposables) {
                    disposable.dispose()
                }
            }
        }
    }

//    /**
//     * Compose a list of synchronizer into one. Synchronizers are attached
//     * in the order in which they are passed and detached in the reverse order
//     */
//    fun composite(vararg syncs: Synchronizer): Synchronizer {
//        return object : Synchronizer {
//            override fun attach(ctx: SynchronizerContext) {
//                for (s in syncs) {
//                    s.attach(ctx)
//                }
//            }
//
//            override fun detach() {
//                for (i in syncs.indices.reversed()) {
//                    syncs[i].detach()
//                }
//            }
//        }
//    }

    /**
     * Creates a synchronizer which invokes the specified runnable on an event from the passed [EventSource]
     */
    fun forEventSource(src: EventSource<*>, r: Runnable): Synchronizer {
        return object : RegistrationSynchronizer() {
            override fun doAttach(ctx: SynchronizerContext): Registration {
                r.run()
                return src.addHandler(object : EventHandler<Any?> {
                    override fun onEvent(event: Any?) {
                        r.run()
                    }
                })
            }
        }
    }

    /**
     * Creates a synchronizer which invokes a handler with an event as a parameter when such an event happens on
     * the passed [EventSource]
     *
     *
     * NB: It isn't called on attach
     */
    fun <EventT> forEventSource(src: EventSource<EventT>, h: Consumer<EventT>): Synchronizer {
        return object : RegistrationSynchronizer() {
            override fun doAttach(ctx: SynchronizerContext): Registration {
                return src.addHandler(object : EventHandler<EventT> {
                    override fun onEvent(event: EventT) {
                        h(event)
                    }
                })
            }
        }
    }

//    fun measuringSynchronizer(name: String, sync: Synchronizer): Synchronizer {
//        return object : Synchronizer {
//            override fun attach(ctx: SynchronizerContext) {
//                val start = System.currentTimeMillis()
//                sync.attach(ctx)
//                log("attach", start)
//            }
//
//            override fun detach() {
//                val start = System.currentTimeMillis()
//                sync.detach()
//                log("detach", start)
//            }
//
//            private fun log(event: String, start: Long) {
//                LOG.info(name + ": " + event + " in " + (System.currentTimeMillis() - start) + " ms")
//            }
//        }
//    }

    fun empty(): Synchronizer {
        return EMPTY
    }
}
