package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.function.Predicate
import jetbrains.datalore.base.observable.collections.*
import jetbrains.datalore.base.observable.collections.set.ObservableHashSet
import jetbrains.datalore.base.observable.collections.set.ObservableSet
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.*
import jetbrains.datalore.base.registration.Registration
import observable.collections.Collections
import observable.collections.UnmodifiableObservableCollection

object ObservableCollections {
    fun <ItemT> toObservable(l: List<ItemT>): ObservableList<ItemT> {
        val result: ObservableList<ItemT> = ObservableArrayList()
        result.addAll(l)
        return result
    }

    fun <ItemT> toObservable(s: Set<ItemT>): ObservableSet<ItemT> {
        val result: ObservableSet<ItemT> = ObservableHashSet()
        result.addAll(s)
        return result
    }

    fun <ItemT> asWritableProp(coll: ObservableCollection<ItemT>): WritableProperty<ItemT> {
        return object : WritableProperty<ItemT> {
            override fun set(value: ItemT) {
                coll.clear()
                if (value != null) {
                    coll.add(value)
                }
            }
        }
    }

    fun <ItemT> asProperty(list: ObservableList<ItemT?>): Property<List<ItemT?>?> {
        return object : Property<List<ItemT?>?> {
            override val propExpr: String
                get() = "list $list"

            override fun get(): List<ItemT?>? {
                return ArrayList(list) ?: null
            }

            override fun set(value: List<ItemT?>?) {
                list.clear()
                if (value != null) {
                    list.addAll(value)
                }
            }

            override fun addHandler(handler: EventHandler<in PropertyChangeEvent<out List<ItemT?>?>>): Registration {
                return list.addHandler(object : EventHandler<CollectionItemEvent<out ItemT?>> {
                    private var myLastValue: List<ItemT?> = ArrayList(list)

                    override fun onEvent(event: CollectionItemEvent<out ItemT?>) {
                        val newValue = ArrayList(list)
                        handler.onEvent(PropertyChangeEvent(
                                Collections.unmodifiableList(myLastValue), Collections.unmodifiableList(newValue)))
                        myLastValue = newValue
                    }
                })
            }
        }
    }

    fun <ItemT> empty(): ObservableCollection<ItemT> {
        return EmptyList()
    }

    fun <ItemT> emptyList(): ObservableList<ItemT> {
        return EmptyList()
    }

    fun <ItemT> count(
            collection: ObservableCollection<ItemT>,
            predicate: Predicate<in ItemT?>): ReadableProperty<out Int> {

        return object : BaseDerivedProperty<Int>(simpleCount(predicate, collection)) {
            private var myCollectionRegistration: Registration? = null
            private var myCount: Int = 0

            override fun doAddListeners() {
                myCollectionRegistration = collection.addListener(object : CollectionAdapter<ItemT>() {
                    override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
                        if (predicate(event.newItem)) {
                            myCount++
                        }
                        somethingChanged()
                    }

                    override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
                        if (predicate(event.oldItem)) {
                            myCount--
                        }
                        somethingChanged()
                    }
                })
                myCount = simpleCount(predicate, collection)
            }

            override fun doRemoveListeners() {
                myCollectionRegistration!!.remove()
                myCollectionRegistration = null
            }

            override fun doGet(): Int {
                return if (myCollectionRegistration == null) {
                    simpleCount(predicate, collection)
                } else {
                    myCount
                }
            }
        }
    }

    private fun <ItemT> simpleCount(predicate: Predicate<in ItemT>, collection: Collection<ItemT>): Int {
        var count = 0
        for (i in collection) {
            if (predicate(i)) {
                count++
            }
        }
        return count
    }

    fun <ItemT> all(
            collection: ObservableCollection<out ItemT>,
            predicate: Predicate<in ItemT?>):

            ReadableProperty<out Boolean?> {

        val prop = count(collection, predicate)
        return Properties.map(prop) { value -> value == collection.size }
    }

    fun <ItemT> any(
            collection: ObservableCollection<ItemT>,
            predicate: Predicate<in ItemT?>):
            ReadableProperty<out Boolean?> {

        val prop = count(collection, predicate)
        return Properties.map(prop) { value -> value > 0 }
    }

    fun <ValueT, ItemT> selectCollection(
            p: ReadableProperty<ValueT>,
            s: (ValueT) -> ObservableCollection<ItemT?>):
            ObservableCollection<ItemT?> {

        return UnmodifiableObservableCollection(SelectorDerivedCollection(p, s))
    }

    fun <ValueT, ItemT> selectList(
            p: ReadableProperty<out ValueT>,
            s: (ValueT) -> ObservableList<ItemT?>):

            ObservableList<ItemT?> {

        return UnmodifiableObservableList(SelectorDerivedList(p, s))
    }

    private class SelectorDerivedCollection<ValueT, ItemT>
    internal constructor(
            source: ReadableProperty<ValueT>,
            `fun`: (ValueT) -> ObservableCollection<ItemT?>) :
            SelectedCollection<ValueT, ItemT?, ObservableCollection<ItemT?>>(source, `fun`) {

        override fun empty(): ObservableCollection<ItemT?> {
            return ObservableCollections.empty()
        }

        override fun follow(source: ObservableCollection<ItemT?>): Registration {
            clear()
            for (i in source) {
                add(i)
            }

            return source.addListener(object : CollectionAdapter<ItemT?>() {
                override fun onItemAdded(event: CollectionItemEvent<out ItemT?>) {
                    add(event.newItem)
                }

                override fun onItemRemoved(event: CollectionItemEvent<out ItemT?>) {
                    remove(event.oldItem)
                }
            })
        }

        override operator fun contains(element: ItemT?): Boolean {
            return if (isFollowing) {
                super.contains(element)
            } else {
                select().contains(element)
            }
        }

        override operator fun iterator(): MutableIterator<ItemT?> {
            return if (isFollowing) {
                super.iterator()
            } else {
                select().iterator()
            }
        }
    }

    private class SelectorDerivedList<ValueT, ItemT>
    internal constructor(
            source: ReadableProperty<out ValueT>,
            `fun`: (ValueT) -> ObservableList<ItemT?>) :

            SelectedCollection<ValueT, ItemT?, ObservableList<ItemT?>>(source, `fun`) {

        override fun empty(): ObservableList<ItemT?> {
            return ObservableCollections.emptyList()
        }

        override fun follow(source: ObservableList<ItemT?>): Registration {
            clear()
            for (i in 0 until source.size) {
                add(i, source.get(i))
            }

            return source.addListener(object : CollectionAdapter<ItemT?>() {
                override fun onItemAdded(event: CollectionItemEvent<out ItemT?>) {
                    add(event.index, event.newItem)
                }

                override fun onItemRemoved(event: CollectionItemEvent<out ItemT?>) {
                    removeAt(event.index)
                }
            })
        }

        override operator fun get(index: Int): ItemT? {
            return if (isFollowing) {
                super.get(index)
            } else {
                select().get(index)
            }
        }

        override operator fun iterator(): MutableIterator<ItemT?> {
            return if (isFollowing) {
                super.iterator()
            } else {
                select().iterator()
            }
        }
    }

    private class EmptyList<ItemT> : AbstractObservableList<ItemT>() {
        override val size = 0

        override operator fun get(index: Int): ItemT {
            throw DataloreIndexOutOfBoundsException(index)
        }

        override fun doAdd(index: Int, item: ItemT) {
            throw UnsupportedOperationException()
        }

        override fun doRemove(index: Int) {
            throw UnsupportedOperationException()
        }

        override fun addListener(l: CollectionListener<in ItemT>): Registration {
            return Registration.EMPTY
        }
    }
}
