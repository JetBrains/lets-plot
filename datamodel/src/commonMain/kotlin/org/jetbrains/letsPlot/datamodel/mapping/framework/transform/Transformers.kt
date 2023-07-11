/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.transform

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionListener
import org.jetbrains.letsPlot.commons.intern.observable.collections.ObservableCollection
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.property.PropertyChangeEvent
import org.jetbrains.letsPlot.commons.intern.observable.property.ReadableProperty
import org.jetbrains.letsPlot.commons.registration.Registration

object Transformers {

//    fun <SourceT> fromList(from: ObservableList<out SourceT>): ListTransformer<SourceT> {
//        return object : ListTransformer<SourceT>() {
//
//            fun transform(to: ObservableList<SourceT>): TerminalTransformation<ObservableList<SourceT>> {
//                val registration = from.addListener(object : CollectionListener<SourceT> {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        to.add(event.index, event.newItem)
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out SourceT>) {
//                        to.set(event.index, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        to.removeAt(event.index)
//                    }
//                })
//
//                for (aFrom in from) {
//                    to.add(aFrom)
//                }
//
//                return object : TerminalTransformation<ObservableList<SourceT>>() {
//                    override val target: ObservableList<SourceT>
//                        get() = to
//
//                    override fun doDispose() {
//                        registration.dispose()
//                    }
//                }
//            }
//        }
//    }
//
//    fun <ItemT> from(source: ItemT): InitiatedTransformer<ItemT> {
//        return object : InitiatedTransformer<ItemT>() {
//
//            fun transform(): TerminalTransformation<ItemT> {
//                return object : TerminalTransformation<ItemT>() {
//                    override val target: ItemT
//                        get() = source
//                }
//            }
//        }
//    }
//
//
//    fun <ItemT> identity(): Transformer<ItemT, ItemT> {
//        return coerce()
//    }
//
//    fun <TargetT, SourceT : TargetT> coerce(): Transformer<SourceT, TargetT> {
//        return object : BaseTransformer<SourceT, TargetT>() {
//            fun transform(from: SourceT): Transformation<SourceT, TargetT> {
//                return object : Transformation<SourceT, TargetT>() {
//                    override val source: SourceT
//                        get() = from
//
//                    override val target: TargetT
//                        get() = from
//                }
//            }
//
//            fun transform(from: SourceT, to: TargetT): Transformation<SourceT, TargetT> {
//                throw UnsupportedOperationException()
//            }
//        }
//    }
//
//    fun <ItemT> coerceList(): Transformer<ObservableList<ItemT>, ObservableList<out ItemT>> {
//        return object : BaseTransformer<ObservableList<ItemT>, ObservableList<out ItemT>>() {
//            fun transform(
//                    from: ObservableList<ItemT>): Transformation<ObservableList<ItemT>, ObservableList<out ItemT>> {
//                return object : Transformation<ObservableList<ItemT>, ObservableList<out ItemT>>() {
//                    override val source: ObservableList<ItemT>
//                        get() = from
//
//                    override val target: ObservableList<out ItemT>
//                        get() = from
//                }
//
//            }
//
//            fun transform(
//                    from: ObservableList<ItemT>, to: ObservableList<out ItemT>): Transformation<ObservableList<ItemT>, ObservableList<out ItemT>> {
//                throw UnsupportedOperationException()
//            }
//        }
//    }
//
//    fun <SourceT, TargetT> fromFun(f: Function<SourceT, TargetT>): Transformer<SourceT, TargetT> {
//        return object : BaseTransformer<SourceT, TargetT>() {
//            fun transform(from: SourceT): Transformation<SourceT, TargetT> {
//                val target = f.apply(from)
//                return object : Transformation<SourceT, TargetT>() {
//                    override val source: SourceT
//                        get() = from
//
//                    override val target: TargetT
//                        get() = target
//                }
//            }
//
//            fun transform(from: SourceT, to: TargetT): Transformation<SourceT, TargetT> {
//                throw UnsupportedOperationException()
//            }
//        }
//    }
//
//    fun <SourceT, TargetT> listMap(
//            transformer: Transformer<SourceT, TargetT>): Transformer<ObservableList<SourceT>, ObservableList<TargetT>> {
//        return object : BaseTransformer<ObservableList<SourceT>, ObservableList<TargetT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<SourceT>, to: ObservableList<TargetT> = ObservableArrayList()): Transformation<ObservableList<SourceT>, ObservableList<TargetT>> {
//                val itemRegistrations = ArrayList<Registration>()
//
//                val listener = object : CollectionListener<SourceT> {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        val transformation = transformer.transform(event.newItem)
//                        to.add(event.index, transformation.target)
//                        itemRegistrations.add(event.index, Registration.from(transformation))
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out SourceT>) {
//                        val transformation = transformer.transform(event.newItem)
//                        to[event.index] = transformation.target
//                        itemRegistrations.set(event.index, Registration.from(transformation))
//                                .remove()
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        to.removeAt(event.index)
//                        itemRegistrations.removeAt(event.index).remove()
//                    }
//                }
//
//
//                for (i in from.indices) {
//                    listener.onItemAdded(CollectionItemEvent(null, from[i], i, EventType.ADD))
//                }
//
//                val reg = from.addListener(listener)
//                return SimpleTransformation(from, to, object : Registration() {
//                    override fun doRemove() {
//                        for (r in itemRegistrations) {
//                            r.remove()
//                        }
//                        reg.remove()
//                    }
//                })
//            }
//        }
//    }
//
//    fun <SourceT, TargetT> listMap(f: Function<SourceT, TargetT>): Transformer<ObservableList<SourceT>, ObservableList<TargetT>> {
//        return listMap(fromFun(f))
//    }

    fun <SpecItemT, ItemT : SpecItemT, ValueT : Comparable<ValueT>, CollectionT : ObservableCollection<ItemT>> sortBy(
        propSpec: (SpecItemT) -> ReadableProperty<out ValueT>
    ): Transformer<CollectionT, ObservableList<ItemT>> {
        return sortBy(propSpec, Order.ASCENDING)
    }

    fun <SpecItemT, ItemT : SpecItemT, ValueT : Comparable<ValueT>, CollectionT : ObservableCollection<ItemT>> sortBy(
        propSpec: (SpecItemT) -> ReadableProperty<out ValueT>, order: Order
    ): Transformer<CollectionT, ObservableList<ItemT>> {
        return sortBy(propSpec, object : Comparator<ValueT> {
            override fun compare(a: ValueT, b: ValueT): Int {
                return if (order === Order.DESCENDING) {
                    -a.compareTo(b)
                } else a.compareTo(b)
            }
        })
    }

    fun <SpecItemT, ItemT : SpecItemT, ValueT, CollectionT : ObservableCollection<ItemT>> sortBy(
        propSpec: (SpecItemT) -> ReadableProperty<out ValueT>, cmp: Comparator<ValueT>
    ): Transformer<CollectionT, ObservableList<ItemT>> {
        val comparator = object : Comparator<ItemT> {
            override fun compare(a: ItemT, b: ItemT): Int {
                val p1 = propSpec(a)
                val p2 = propSpec(b)

                val v1 = p1.get()
                val v2 = p2.get()

                return if (v1 == null || v2 == null) {
                    compareNulls(v1, v2)
                } else cmp.compare(v1, v2)

            }
        }

        return object : BaseTransformer<CollectionT, ObservableList<ItemT>>() {

            override fun transform(
                from: CollectionT
            ):
                    Transformation<CollectionT, ObservableList<ItemT>> {
                return transform(from, ObservableArrayList())
            }

            override fun transform(
                from: CollectionT,
                to: ObservableList<ItemT>
            ):
                    Transformation<CollectionT, ObservableList<ItemT>> {

                return object : Transformation<CollectionT, ObservableList<ItemT>>() {
                    private var myCollectionReg: Registration
                    private var myCollectionListener: CollectionListener<ItemT>
                    private val myListeners = HashMap<ItemT, Registration>()


                    override val source: CollectionT
                        get() = from

                    override val target: ObservableList<ItemT>
                        get() = to

                    init {
                        myCollectionListener = object : org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<ItemT>() {
                            override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                                val item = event.newItem!!
                                watch(item, to)

                                val pos = to.binarySearch(item, comparator)
                                val insertIndex = if (pos >= 0) pos + 1 else -(pos + 1)
                                to.add(insertIndex, item)
                            }

                            override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                                val item = event.oldItem

                                val sortedIndex = to.indexOf(item)
                                if (sortedIndex == -1) {
                                    throw IllegalStateException()
                                }

                                to.removeAt(sortedIndex)
                                unwatch(item)
                            }
                        }
                        myCollectionReg = from.addListener(myCollectionListener)


                        for (item in from) {
                            watch(item, to)
                            to.add(item)
                        }
//                        Collections.sort(to, comparator)
                        to.sortWith(comparator)
                    }

                    override fun doDispose() {
                        myCollectionReg.remove()
                        for (item in from) {
                            unwatch(item)
                        }
                    }

                    private fun watch(item: ItemT?, to: ObservableList<ItemT>) {
                        val property = propSpec(item!!)
                        myListeners[item] = property.addHandler(object : EventHandler<PropertyChangeEvent<out ValueT>> {
                            override fun onEvent(event: PropertyChangeEvent<out ValueT>) {
                                var needMove = false
                                val sortedIndex = to.indexOf(item)
                                if (sortedIndex > 0) {
                                    val before = to[sortedIndex - 1]
                                    if (comparator.compare(before, item) > 0) {
                                        needMove = true
                                    }
                                }
                                if (sortedIndex < to.size - 1) {
                                    val after = to[sortedIndex + 1]
                                    if (comparator.compare(item, after) > 0) {
                                        needMove = true
                                    }
                                }
                                if (needMove) {
                                    myCollectionListener.onItemSet(
                                        org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                                            item,
                                            item,
                                            -1,
                                            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.SET
                                        )
                                    )
                                }
                            }
                        })
                    }

                    private fun unwatch(item: ItemT?) {
                        myListeners.remove(item)?.remove()
                    }
                }
            }
        }
    }

//    fun <SpecItemT, ItemT : SpecItemT, ValueT, CollectionT : ObservableCollection<ItemT>> sortByConstant(
//            propSpec: Function<SpecItemT, out ValueT>, cmp: Comparator<ValueT>): Transformer<CollectionT, ObservableList<ItemT>> {
//        val comparator = object : Comparator<ItemT> {
//            override fun compare(i1: ItemT, i2: ItemT): Int {
//                val v1 = propSpec.apply(i1)
//                val v2 = propSpec.apply(i2)
//
//                return if (v1 == null || v2 == null) {
//                    compareNulls(v1, v2)
//                } else cmp.compare(v1, v2)
//
//            }
//        }
//
//        return object : BaseTransformer<CollectionT, ObservableList<ItemT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: CollectionT, to: ObservableList<ItemT> = ObservableArrayList()): Transformation<CollectionT, ObservableList<ItemT>> {
//                return object : Transformation<CollectionT, ObservableList<ItemT>>() {
//                    private var myCollectionReg: Registration? = null
//
//
//                    override val source: CollectionT
//                        get() = from
//
//                    override val target: ObservableList<ItemT>
//                        get() = to
//
//                    init {
//                        myCollectionReg = from.addListener(object : CollectionAdapter<ItemT>() {
//                            override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                                val item = event.newItem
//
//                                val pos = Collections.binarySearch(to, item, comparator)
//                                val insertIndex = if (pos >= 0) pos + 1 else -(pos + 1)
//                                to.add(insertIndex, item)
//                            }
//
//                            override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                                val item = event.oldItem
//
//                                val sortedIndex = to.indexOf(item)
//                                if (sortedIndex == -1) {
//                                    throw IllegalStateException()
//                                }
//
//                                to.removeAt(sortedIndex)
//                            }
//                        })
//
//
//                        for (item in from) {
//                            to.add(item)
//                        }
//                        Collections.sort(to, comparator)
//                    }
//
//                    override fun doDispose() {
//                        myCollectionReg!!.remove()
//                        target.clear()
//                    }
//                }
//            }
//        }//tree list has much better asymptotics of insert
//    }

    private fun compareNulls(o1: Any?, o2: Any?): Int {
        if (o1 === o2) return 0
        return if (o1 == null) {
            -1
        } else {
            1
        }
    }


//    fun <ItemT, CollectionT : ObservableCollection<out ItemT>> listFilter(filterBy: Function<ItemT, ReadableProperty<Boolean>>): Transformer<CollectionT, ObservableList<ItemT>> {
//        return object : BaseFilterTransformer<ItemT, CollectionT, ObservableList<ItemT>>(filterBy) {
//            protected fun add(item: ItemT, from: CollectionT, to: ObservableList<ItemT>) {
//                val fromItr = from.iterator()
//                var index = 0
//                var foundItem = false
//                for (curTo in to) {
//                    while (fromItr.hasNext()) {
//                        val curFrom = fromItr.next()
//                        if (curFrom === curTo) {
//                            break
//                        }
//                        if (curFrom === item) {
//                            foundItem = true
//                            break
//                        }
//                    }
//                    if (foundItem) {
//                        break
//                    }
//                    index++
//                }
//                if (!fromItr.hasNext() && !foundItem) {
//                    throw IllegalStateException("item $item has not been found in $from")
//                }
//                to.add(index, item)
//            }
//
//            protected fun createTo(): ObservableList<ItemT> {
//                return ObservableArrayList()
//            }
//        }
//    }
//
//    fun <ItemT, CollectionT : ObservableCollection<ItemT>> filter(filterBy: Function<ItemT, ReadableProperty<Boolean>>): Transformer<CollectionT, ObservableCollection<ItemT>> {
//        return object : BaseFilterTransformer<ItemT, CollectionT, ObservableCollection<ItemT>>(filterBy) {
//            protected fun add(item: ItemT, from: CollectionT, to: ObservableCollection<ItemT>) {
//                to.add(item)
//            }
//
//            protected fun createTo(): ObservableCollection<ItemT> {
//                return ObservableHashSet()
//            }
//        }
//    }
//
//    fun <ItemT, CollectionT : ObservableCollection<ItemT>> filterByConstant(filterBy: Function<ItemT, Boolean>): Transformer<CollectionT, ObservableCollection<ItemT>> {
//
//        return object : BaseTransformer<CollectionT, ObservableCollection<ItemT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: CollectionT, to: ObservableCollection<ItemT> = ObservableArrayList()): Transformation<CollectionT, ObservableCollection<ItemT>> {
//                return object : Transformation<CollectionT, ObservableCollection<ItemT>>() {
//                    private var myReg: Registration? = null
//
//                    override val source: CollectionT
//                        get() = from
//
//                    override val target: ObservableCollection<ItemT>
//                        get() = to
//
//                    init {
//                        for (item in from) {
//                            if (filterBy.apply(item)) {
//                                to.add(item)
//                            }
//                        }
//
//                        myReg = from.addListener(object : CollectionAdapter<ItemT>() {
//                            override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                                if (filterBy.apply(event.newItem)) {
//                                    to.add(event.newItem)
//                                }
//                            }
//
//                            override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                                if (filterBy.apply(event.oldItem)) {
//                                    to.remove(event.oldItem)
//                                }
//                            }
//                        })
//                    }
//
//                    override fun doDispose() {
//                        myReg!!.remove()
//                        target.clear()
//                        super.doDispose()
//                    }
//                }
//            }
//        }
//    }
//
//
//    fun <SourceT, TargetT> oneToOne(
//            converter: Function<SourceT, TargetT>, checker: Function<TargetT, SourceT>): Transformer<ObservableCollection<SourceT>, ObservableCollection<TargetT>> {
//        return object : BaseTransformer<ObservableCollection<SourceT>, ObservableCollection<TargetT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableCollection<SourceT>, to: ObservableCollection<TargetT> = ObservableHashSet()): Transformation<ObservableCollection<SourceT>, ObservableCollection<TargetT>> {
//                return object : Transformation<ObservableCollection<SourceT>, ObservableCollection<TargetT>>() {
//                    private var myCollectionRegistration: Registration? = null
//
//                    override val source: ObservableCollection<SourceT>
//                        get() = from
//
//                    override val target: ObservableCollection<TargetT>
//                        get() = to
//
//                    init {
//                        for (item in from) {
//                            add(item)
//                        }
//
//                        myCollectionRegistration = from.addListener(object : CollectionAdapter<SourceT>() {
//                            override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                                add(event.newItem)
//                            }
//
//                            override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                                val item = event.oldItem
//                                if (!exists(item)) return
//                                val i = to.iterator()
//                                while (i.hasNext()) {
//                                    val t = i.next()
//                                    if (checker.apply(t) == item) {
//                                        i.remove()
//                                        return
//                                    }
//                                }
//                            }
//                        })
//                    }
//
//                    override fun doDispose() {
//                        myCollectionRegistration!!.remove()
//                    }
//
//                    private fun exists(item: SourceT?): Boolean {
//                        for (t in to) {
//                            if (checker.apply(t) == item) return true
//                        }
//                        return false
//                    }
//
//                    private fun add(item: SourceT?) {
//                        if (!exists(item)) {
//                            to.add(converter.apply(item))
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    fun <ItemT> firstN(
//            value: ReadableProperty<Int>): Transformer<ObservableList<ItemT>, ObservableList<ItemT>> {
//        return object : BaseTransformer<ObservableList<ItemT>, ObservableList<ItemT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<ItemT>, to: ObservableList<ItemT> = ObservableArrayList()): Transformation<ObservableList<ItemT>, ObservableList<ItemT>> {
//                val fromReg = from.addListener(object : CollectionListener<ItemT> {
//                    override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                        val n = value.get()
//                        if (event.index >= n) return
//                        if (to.size == n) {
//                            to.removeAt(n - 1)
//                        }
//                        to.add(event.index, event.newItem)
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out ItemT>) {
//                        if (event.index >= value.get()) return
//                        to.set(event.index, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                        val n = value.get()
//                        if (event.index >= n) return
//                        to.removeAt(event.index)
//                        if (from.size >= n) {
//                            to.add(from[n - 1])
//                        }
//                    }
//                })
//
//                val propReg = value.addHandler(object : EventHandler<PropertyChangeEvent<Int>> {
//                    override fun onEvent(event: PropertyChangeEvent<Int>) {
//                        val n = event.newValue!!
//                        if (event.newValue > event.oldValue) {
//                            val maxItem = Math.min(n, from.size)
//                            for (i in event.oldValue until maxItem) {
//                                to.add(from[i])
//                            }
//                        } else {
//                            if (to.size > n) {
//                                for (i in to.size - 1 downTo n) {
//                                    to.removeAt(i)
//                                }
//                            }
//                        }
//                    }
//                })
//
//                val n = value.get()
//                val maxItem = Math.min(n, from.size)
//                for (i in 0 until maxItem) {
//                    to.add(from[i])
//                }
//
//                return SimpleTransformation(from, to, CompositeRegistration(fromReg, propReg))
//            }
//        }
//    }
//
//    fun <ItemT> skipN(
//            value: ReadableProperty<Int>): Transformer<ObservableList<ItemT>, ObservableList<ItemT>> {
//        return object : BaseTransformer<ObservableList<ItemT>, ObservableList<ItemT>>() {
//            private var n: Int = 0
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<ItemT>, to: ObservableList<ItemT> = ObservableArrayList()): Transformation<ObservableList<ItemT>, ObservableList<ItemT>> {
//                val fromReg = from.addListener(object : CollectionListener<ItemT> {
//                    override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                        if (from.size <= n) return
//                        val addedIndex = event.index
//                        if (addedIndex < n) {
//                            to.add(0, from[n])
//                        } else {
//                            to.add(addedIndex - n, event.newItem)
//                        }
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out ItemT>) {
//                        val index = event.index
//                        if (index < n) return
//                        to.set(index - n, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                        if (from.size < n) return
//                        val removedIndex = event.index
//                        if (removedIndex < n) {
//                            to.removeAt(0)
//                        } else {
//                            to.removeAt(removedIndex - n)
//                        }
//                    }
//                })
//
//                val propReg = value.addHandler(object : EventHandler<PropertyChangeEvent<Int>> {
//                    override fun onEvent(event: PropertyChangeEvent<Int>) {
//                        n = event.newValue!!
//                        if (n < event.oldValue) {
//                            var i = n
//                            while (i < from.size && i < event.oldValue) {
//                                to.add(0, from[i])
//                                i++
//                            }
//                        } else {
//                            var i = event.oldValue!!
//                            while (i < from.size && i < n) {
//                                to.removeAt(0)
//                                i++
//                            }
//                        }
//                    }
//                })
//
//                n = value.get()
//                for (i in n until from.size) {
//                    to.add(from[i])
//                }
//
//                return SimpleTransformation(from, to, CompositeRegistration(fromReg, propReg))
//            }
//        }
//    }
//
//    fun <ItemT> flattenList(): Transformer<ObservableList<ObservableList<out ItemT>>, ObservableList<ItemT>> {
//        return flattenList(Functions.identity<ObservableList<out ItemT>>())
//    }
//
//    fun <SourceT, TargetT> flattenList(
//            f: Function<SourceT, out ObservableList<out TargetT>>): Transformer<ObservableList<SourceT>, ObservableList<TargetT>> {
//        return Transformers.flattenList(f, Transformers.identity())
//    }
//
//    fun <SourceT, SelectedT, ResultT> flattenList(
//            f: Function<SourceT, out SelectedT>,
//            t: Transformer<SelectedT, out ObservableList<out ResultT>>
//    ): Transformer<ObservableList<SourceT>, ObservableList<ResultT>> {
//        return object : BaseTransformer<ObservableList<SourceT>, ObservableList<ResultT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<SourceT>, to: ObservableList<ResultT> = ObservableArrayList()): Transformation<ObservableList<SourceT>, ObservableList<ResultT>> {
//                val registrations = IdentityHashMap<SourceT, Registration>()
//                val sizes = IdentityHashMap<SourceT, Int>()
//
//                val sourceListener = object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        val selected = f.apply(event.newItem)
//                        val transform = t.transform(selected)
//                        val target = transform.target
//
//                        var startIndex = getStartResultIndex(event.newItem, from, sizes)
//                        sizes.put(event.newItem, target.size)
//
//                        val reg = watch<out ResultT>(event.newItem, target)
//
//                        registrations.put(event.newItem, object : Registration() {
//                            override fun doRemove() {
//                                reg.remove()
//                                transform.dispose()
//                            }
//                        })
//
//                        for (r in target) {
//                            to.add(startIndex++, r)
//                        }
//                    }
//
//                    private fun <ItemT : ResultT> watch(container: SourceT?, list: ObservableList<ItemT>): Registration {
//                        return list.addListener(object : CollectionListener<ItemT> {
//                            override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                                val startIndex = getStartResultIndex(container, from, sizes)
//                                sizes.put(container, sizes.get(container) + 1)
//                                to.add(startIndex + event.index, event.newItem)
//                            }
//
//                            override fun onItemSet(event: CollectionItemEvent<out ItemT>) {
//                                val startIndex = getStartResultIndex(container, from, sizes)
//                                to.set(startIndex + event.index, event.newItem)
//                            }
//
//                            override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                                sizes.put(container, sizes.get(container) - 1)
//                                to.remove(event.oldItem)
//                            }
//                        })
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        val selected = f.apply(event.oldItem)
//                        val transformation = t.transform(selected)
//
//                        sizes.remove(event.oldItem)
//                        registrations.remove(event.oldItem).remove()
//
//                        to.removeAll(transformation.target)
//                        transformation.dispose()
//                    }
//                }
//
//                val sourceRegistration = from.addListener(sourceListener)
//                var index = 0
//                for (s in from) {
//                    sourceListener.onItemAdded(CollectionItemEvent(null, s, index++, EventType.ADD))
//                }
//
//                return SimpleTransformation(from, to, object : Registration() {
//                    override fun doRemove() {
//                        for (s in from) {
//                            registrations.remove(s).remove()
//                        }
//                        sourceRegistration.remove()
//                    }
//                })
//            }
//
//            private fun getStartResultIndex(event: SourceT?, sourceList: ObservableList<SourceT>, sizes: Map<SourceT, Int>): Int {
//                var resultIndex = 0
//                val iterator = sourceList.iterator()
//                var current = iterator.next()
//                while (current !== event) {
//                    resultIndex += sizes[current]
//                    current = iterator.next()
//                }
//                return resultIndex
//            }
//        }
//    }
//
//    fun <ValueT, PropertyT : ReadableProperty<ValueT>> flattenPropertyList(): Transformer<ObservableList<PropertyT>, ObservableList<ValueT>> {
//        return object : BaseTransformer<ObservableList<PropertyT>, ObservableList<ValueT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<PropertyT>, to: ObservableList<ValueT> = ObservableArrayList()): Transformation<ObservableList<PropertyT>, ObservableList<ValueT>> {
//                val propRegistrations = ArrayList<Registration>()
//                val listener = object : CollectionAdapter<PropertyT>() {
//                    override fun onItemAdded(listEvent: CollectionItemEvent<out PropertyT>) {
//                        propRegistrations.add(listEvent.index, listEvent.newItem!!.addHandler(
//                                object : EventHandler<PropertyChangeEvent<ValueT>> {
//                                    override fun onEvent(propEvent: PropertyChangeEvent<ValueT>) {
//                                        val index = from.indexOf(listEvent.newItem)
//                                        to.set(index, propEvent.newValue)
//                                    }
//                                })
//                        )
//                        to.add(listEvent.index, listEvent.newItem.get())
//                    }
//
//                    override fun onItemRemoved(listEvent: CollectionItemEvent<out PropertyT>) {
//                        propRegistrations.removeAt(listEvent.index).remove()
//                        to.removeAt(listEvent.index)
//                    }
//                }
//
//                for (i in from.indices) {
//                    listener.onItemAdded(CollectionItemEvent(null, from[i], i, EventType.ADD))
//                }
//
//                val reg = from.addListener(listener)
//                return SimpleTransformation(from, to, object : Registration() {
//                    override fun doRemove() {
//                        reg.remove()
//                        for (r in propRegistrations) {
//                            r.remove()
//                        }
//                        propRegistrations.clear()
//                    }
//                })
//            }
//        }
//    }
//
//    /**
//     * Select only those with the highest priority. Null items are not allowed.
//     * Warning: target collection is not protected from outside writes.
//     *
//     * @param getPriority The greater is number, the higher is priority. Null priority
//     * is not allowed. The same priority must always be returned for the same element.
//     */
//    fun <ItemT> highestPriority(
//            getPriority: Function<ItemT, Int>?): Transformer<ObservableCollection<ItemT>, ObservableCollection<ItemT>> {
//        if (getPriority == null) {
//            throw IllegalArgumentException("Null getPriority is not allowed")
//        }
//        return object : BaseTransformer<ObservableCollection<ItemT>, ObservableCollection<ItemT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableCollection<ItemT>, to: ObservableCollection<ItemT> = ObservableHashSet()): Transformation<ObservableCollection<ItemT>, ObservableCollection<ItemT>> {
//                abstract class FromCollectionAdapter : CollectionAdapter<ItemT>() {
//                    internal abstract fun initToCollection()
//                }
//
//                val listener = object : FromCollectionAdapter() {
//                    private var myHighestPriority: Int = 0
//
//                    override fun initToCollection() {
//                        myHighestPriority = Integer.MIN_VALUE
//                        for (item in from) {
//                            insertToToCollection(item)
//                        }
//                    }
//
//                    private fun insertToToCollection(item: ItemT?) {
//                        if (item == null) {
//                            throw IllegalArgumentException("Null items are not allowed")
//                        }
//                        val newItemPriority = getPriority.apply(item)
//                        if (newItemPriority == null) {
//                            throw IllegalArgumentException("Null priorities are not allowed, item=$item")
//                        } else if (newItemPriority > myHighestPriority) {
//                            to.clear()
//                            to.add(item)
//                            myHighestPriority = newItemPriority
//                        } else if (newItemPriority == myHighestPriority) {
//                            to.add(item)
//                        }
//                    }
//
//                    override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                        insertToToCollection(event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                        val oldItem = event.oldItem
//                        val oldItemPriority = getPriority.apply(oldItem)
//                        if (oldItemPriority == null) {
//                            throw IllegalStateException("Old item priority unexpectedly got null, item=" + oldItem!!)
//                        } else if (oldItemPriority > myHighestPriority) {
//                            // Can happen only in case of getPriority or concurrency issue
//                            throw IllegalStateException("Abnormal state: found missed high-priority item " + oldItem
//                                    + ", oldItemPriority=" + oldItemPriority + ", myHighestPriority=" + myHighestPriority)
//                        } else if (oldItemPriority == myHighestPriority) {
//                            to.remove(oldItem)
//                            if (to.isEmpty()) {
//                                initToCollection()
//                            }
//                        }
//                    }
//                }
//
//                listener.initToCollection()
//
//                return SimpleTransformation(from, to, from.addListener(listener))
//            }
//        }
//    }
//
//    fun <SourceT, TargetT> flatten(
//            f: Function<SourceT, ObservableCollection<TargetT>>): Transformer<ObservableCollection<SourceT>, ObservableCollection<TargetT>> {
//        return Transformers.flatten(f, Transformers.identity())
//    }
//
//    fun <SourceT, SelectedT, ResultT> flatten(
//            f: Function<SourceT, SelectedT>, t: Transformer<SelectedT, out ObservableCollection<ResultT>>): Transformer<ObservableCollection<SourceT>, ObservableCollection<ResultT>> {
//        return object : BaseTransformer<ObservableCollection<SourceT>, ObservableCollection<ResultT>>() {
//            fun transform(
//                    source: ObservableCollection<SourceT>): Transformation<ObservableCollection<SourceT>, ObservableCollection<ResultT>> {
//                return transform(source, ObservableHashSet())
//            }
//
//            fun transform(
//                    from: ObservableCollection<SourceT>, to: ObservableCollection<ResultT>): Transformation<ObservableCollection<SourceT>, ObservableCollection<ResultT>> {
//                val nestedListener = object : CollectionAdapter<ResultT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out ResultT>) {
//                        to.add(event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out ResultT>) {
//                        to.remove(event.oldItem)
//                    }
//                }
//
//                val registrations = HashMap<SourceT, Registration>()
//                val sourceListener = object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        val subcollection = f.apply(event.newItem)
//                        val transform = t.transform(subcollection)
//                        val target = transform.target
//                        to.addAll(target)
//                        val reg = target.addListener(nestedListener)
//                        registrations[event.newItem] = object : Registration() {
//                            override fun doRemove() {
//                                reg.remove()
//                                transform.dispose()
//                            }
//                        }
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        val selected = f.apply(event.oldItem)
//                        val transformation = t.transform(selected)
//                        to.removeAll(transformation.target)
//                        transformation.dispose()
//                        registrations.remove(event.oldItem).remove()
//                    }
//                }
//                val sourceRegistration = from.addListener(sourceListener)
//                for (s in from) {
//                    sourceListener.onItemAdded(CollectionItemEvent(null, s, -1, EventType.ADD))
//                }
//
//                return SimpleTransformation(from, to, object : Registration() {
//                    override fun doRemove() {
//                        for (s in from) {
//                            registrations.remove(s).remove()
//                        }
//                        sourceRegistration.remove()
//                    }
//                })
//            }
//        }
//    }

    fun <ItemT> identityList(): Transformer<ObservableList<ItemT>, ObservableList<ItemT>> {
        return object : BaseTransformer<ObservableList<ItemT>, ObservableList<ItemT>>() {

            override fun transform(
                from: ObservableList<ItemT>
            ): Transformation<ObservableList<ItemT>, ObservableList<ItemT>> {
                return transform(from, ObservableArrayList())
            }

            override fun transform(
                from: ObservableList<ItemT>,
                to: ObservableList<ItemT>
            ): Transformation<ObservableList<ItemT>, ObservableList<ItemT>> {

                val registration = from.addListener(object : org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<ItemT>() {
                    override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                        @Suppress("UNCHECKED_CAST")
                        to.add(event.index, event.newItem as ItemT)
                    }

                    override fun onItemSet(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                        @Suppress("UNCHECKED_CAST")
                        to.set(event.index, event.newItem as ItemT)
                    }

                    override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                        to.removeAt(event.index)
                    }
                })
                to.addAll(from)

                return SimpleTransformation(from, to, registration)
            }
        }
    }

//    fun <ItemT> identityCollection(): Transformer<ObservableCollection<ItemT>, ObservableCollection<ItemT>> {
//        return object : BaseTransformer<ObservableCollection<ItemT>, ObservableCollection<ItemT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableCollection<ItemT>, to: ObservableCollection<ItemT> = ObservableHashSet()): Transformation<ObservableCollection<ItemT>, ObservableCollection<ItemT>> {
//                val registration = from.addListener(object : CollectionAdapter<ItemT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                        to.add(event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                        to.remove(event.oldItem)
//                    }
//                })
//                to.addAll(from)
//
//                return SimpleTransformation(from, to, registration)
//            }
//        }
//    }
//
//    fun <TargetT, SourceT : TargetT, ItemT : TargetT> addFirst(item: ItemT): Transformer<ObservableList<SourceT>, ObservableList<TargetT>> {
//        return object : BaseTransformer<ObservableList<SourceT>, ObservableList<TargetT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<SourceT>, to: ObservableList<TargetT> = ObservableArrayList()): Transformation<ObservableList<SourceT>, ObservableList<TargetT>> {
//                val fromListener = object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        val pos = event.index
//                        to.add(pos + 1, event.newItem)
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out SourceT>) {
//                        val pos = event.index
//                        to.set(pos + 1, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        val pos = event.index
//                        to.removeAt(pos + 1)
//                    }
//                }
//
//                to.add(item)
//                to.addAll(from)
//
//                return SimpleTransformation(from, to, from.addListener(fromListener))
//            }
//        }
//    }
//
//    fun <TargetT, SourceT : TargetT, ItemT : TargetT> merge(items: ObservableList<ItemT>): Transformer<ObservableList<SourceT>, ObservableList<TargetT>> {
//        return object : BaseTransformer<ObservableList<SourceT>, ObservableList<TargetT>>() {
//            private var fromSize: Int = 0
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<SourceT>, to: ObservableList<TargetT> = ObservableArrayList()): Transformation<ObservableList<SourceT>, ObservableList<TargetT>> {
//                val fromListener = object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        val pos = event.index
//                        to.add(pos, event.newItem)
//                        fromSize += 1
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out SourceT>) {
//                        val pos = event.index
//                        to.set(pos, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        val pos = event.index
//                        to.removeAt(pos)
//                        fromSize -= 1
//                    }
//                }
//
//                val itemsListener = object : CollectionAdapter<ItemT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                        val pos = event.index
//                        to.add(pos + fromSize, event.newItem)
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out ItemT>) {
//                        val pos = event.index
//                        to.set(pos + fromSize, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                        val pos = event.index
//                        to.removeAt(fromSize + pos)
//                    }
//                }
//
//                to.addAll(from)
//                fromSize = from.size
//                to.addAll(items)
//
//                return SimpleTransformation(from, to,
//                        CompositeRegistration(from.addListener(fromListener), items.addListener(itemsListener)))
//            }
//        }
//    }
//
//    fun <TargetT, SourceT : TargetT, ItemT : TargetT> addFirstWithCondition(
//            item: ItemT, condition: ReadableProperty<Boolean>): Transformer<ObservableList<SourceT>, ObservableList<TargetT>> {
//        return Transformers.addFirstWithCondition(Functions.constantSupplier(item), condition)
//    }
//
//    fun <TargetT, SourceT : TargetT, ItemT : TargetT> addFirstWithCondition(
//            item: Supplier<ItemT>, condition: ReadableProperty<Boolean>): Transformer<ObservableList<SourceT>, ObservableList<TargetT>> {
//        val memoizedItem = Functions.memorize(item)
//        return object : BaseTransformer<ObservableList<SourceT>, ObservableList<TargetT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<SourceT>, to: ObservableList<TargetT> = ObservableArrayList()): Transformation<ObservableList<SourceT>, ObservableList<TargetT>> {
//                val fromListener = object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        var pos = event.index
//                        if (condition.get()) {
//                            pos += 1
//                        }
//                        to.add(pos, event.newItem)
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out SourceT>) {
//                        var pos = event.index
//                        if (condition.get()) {
//                            pos += 1
//                        }
//                        to.set(pos, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        var pos = event.index
//                        if (condition.get()) {
//                            pos += 1
//                        }
//                        to.removeAt(pos)
//                    }
//                }
//
//                val conditionHandler = object : EventHandler<PropertyChangeEvent<Boolean>> {
//                    override fun onEvent(event: PropertyChangeEvent<Boolean>) {
//                        if (event.newValue!!) {
//                            to.add(0, memoizedItem.get())
//                        } else {
//                            to.removeAt(0)
//                        }
//                    }
//                }
//
//                if (condition.get()) {
//                    to.add(memoizedItem.get())
//                }
//                to.addAll(from)
//
//                return SimpleTransformation(from, to,
//                        CompositeRegistration(from.addListener(fromListener), condition.addHandler(conditionHandler)))
//            }
//        }
//    }
//
//
//    fun <TargetT, SourceT : TargetT, ItemT : TargetT> addWithCondition(
//            item: ItemT, condition: ReadableProperty<Boolean>): Transformer<ObservableCollection<SourceT>, ObservableCollection<TargetT>> {
//        return Transformers.addWithCondition(Functions.constantSupplier(item), condition)
//    }
//
//    fun <TargetT, SourceT : TargetT, ItemT : TargetT> addWithCondition(
//            item: Supplier<ItemT>, condition: ReadableProperty<Boolean>): Transformer<ObservableCollection<SourceT>, ObservableCollection<TargetT>> {
//        val memoizedItem = Functions.memorize(item)
//        return object : BaseTransformer<ObservableCollection<SourceT>, ObservableCollection<TargetT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableCollection<SourceT>, to: ObservableCollection<TargetT> = ObservableHashSet()): Transformation<ObservableCollection<SourceT>, ObservableCollection<TargetT>> {
//                val fromListener = object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        to.add(event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        to.remove(event.oldItem)
//                    }
//                }
//
//                val conditionHandler = object : EventHandler<PropertyChangeEvent<Boolean>> {
//                    override fun onEvent(event: PropertyChangeEvent<Boolean>) {
//                        if (event.newValue!!) {
//                            to.add(memoizedItem.get())
//                        } else {
//                            to.remove(memoizedItem.get())
//                        }
//                    }
//                }
//
//                if (condition.get()) {
//                    to.add(memoizedItem.get())
//                }
//                to.addAll(from)
//
//                return SimpleTransformation(from, to,
//                        CompositeRegistration(from.addListener(fromListener), condition.addHandler(conditionHandler)))
//            }
//        }
//    }
//
//    fun <ItemT> withPlaceHoldersIfEmpty(
//            placeholder: Supplier<ItemT>): Transformer<ObservableList<ItemT>, List<ItemT>> {
//        return object : BaseTransformer<ObservableList<ItemT>, List<ItemT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<ItemT>, to: MutableList<ItemT> = ArrayList()): Transformation<ObservableList<ItemT>, List<ItemT>> {
//                val fromRegistration = from.addListener(object : CollectionAdapter<ItemT>() {
//                    private var myPlaceholder: ItemT? = null
//
//                    init {
//                        if (from.isEmpty()) {
//                            to.add(myPlaceholder = placeholder.get())
//                        }
//                    }
//
//                    override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
//                        if (myPlaceholder != null) {
//                            to.remove(myPlaceholder)
//                            myPlaceholder = null
//                        }
//                        to.add(event.index, event.newItem)
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out ItemT>) {
//                        if (myPlaceholder != null) {
//                            throw IllegalStateException()
//                        }
//                        to.set(event.index, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
//                        to.removeAt(event.index)
//
//                        if (to.isEmpty()) {
//                            if (myPlaceholder != null) {
//                                throw IllegalStateException()
//                            }
//                            to.add(myPlaceholder = placeholder.get())
//                        }
//                    }
//                })
//
//                return SimpleTransformation(from, to, fromRegistration)
//            }
//        }
//    }
//
//    fun <ItemT> propertyToList(): Transformer<ReadableProperty<ItemT>, ObservableList<ItemT>> {
//        return object : BaseTransformer<ReadableProperty<ItemT>, ObservableList<ItemT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ReadableProperty<ItemT>, to: ObservableList<ItemT> = ObservableArrayList()): Transformation<ReadableProperty<ItemT>, ObservableList<ItemT>> {
//                if (!to.isEmpty()) {
//                    throw IllegalStateException()
//                }
//
//                val sync = object : Runnable {
//                    override fun run() {
//                        if (!to.isEmpty() && from.get() === to[0]) return
//                        to.clear()
//                        if (from.get() != null) {
//                            to.add(from.get())
//                        }
//                    }
//                }
//                sync.run()
//
//                return SimpleTransformation(from, to, from.addHandler(object : EventHandler<PropertyChangeEvent<ItemT>> {
//                    override fun onEvent(event: PropertyChangeEvent<ItemT>) {
//                        sync.run()
//                    }
//                }))
//            }
//        }
//    }
//
//
//    fun <ItemT> propertyToCollection(): Transformer<ReadableProperty<ItemT>, ObservableCollection<ItemT>> {
//        return object : BaseTransformer<ReadableProperty<ItemT>, ObservableCollection<ItemT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ReadableProperty<ItemT>, to: ObservableCollection<ItemT> = ObservableHashSet()): Transformation<ReadableProperty<ItemT>, ObservableCollection<ItemT>> {
//                if (!to.isEmpty()) {
//                    throw IllegalStateException()
//                }
//
//                val sync = object : Runnable {
//                    override fun run() {
//                        if (!to.isEmpty() && from.get() === to.iterator().next()) return
//                        to.clear()
//                        if (from.get() != null) {
//                            to.add(from.get())
//                        }
//                    }
//                }
//                sync.run()
//
//                return SimpleTransformation(from, to, from.addHandler(object : EventHandler<PropertyChangeEvent<ItemT>> {
//                    override fun onEvent(event: PropertyChangeEvent<ItemT>) {
//                        sync.run()
//                    }
//                }))
//            }
//        }
//    }
//
//    fun <TargetT, SourceT> select(
//            function: Function<SourceT, TargetT>): Transformer<ObservableCollection<SourceT>, ObservableCollection<TargetT>> {
//        return object : BaseTransformer<ObservableCollection<SourceT>, ObservableCollection<TargetT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableCollection<SourceT>, to: ObservableCollection<TargetT> = ObservableHashSet()): Transformation<ObservableCollection<SourceT>, ObservableCollection<TargetT>> {
//                val fromListener = object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        to.add(function.apply(event.newItem))
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        to.remove(function.apply(event.oldItem))
//                    }
//                }
//
//                for (source in from) {
//                    to.add(function.apply(source))
//                }
//
//                return SimpleTransformation(from, to, from.addListener(fromListener))
//            }
//        }
//    }
//
//    fun <TargetT, SourceT> selectList(function: Function<SourceT, TargetT>): Transformer<ObservableList<SourceT>, ObservableList<TargetT>> {
//        return object : BaseTransformer<ObservableList<SourceT>, ObservableList<TargetT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<SourceT>, to: ObservableList<TargetT> = ObservableArrayList()): Transformation<ObservableList<SourceT>, ObservableList<TargetT>> {
//                val fromListener = object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        to.add(event.index, function.apply(event.newItem))
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out SourceT>) {
//                        to[event.index] = function.apply(event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        to.removeAt(event.index)
//                    }
//                }
//
//                for (source in from) {
//                    to.add(function.apply(source))
//                }
//
//                return SimpleTransformation(from, to, from.addListener(fromListener))
//            }
//        }
//    }
//
//    fun <SourceT> reverse(): Transformer<ObservableList<SourceT>, ObservableList<SourceT>> {
//        return object : BaseTransformer<ObservableList<SourceT>, ObservableList<SourceT>>() {
//
//            @JvmOverloads
//            fun transform(
//                    from: ObservableList<SourceT>, to: ObservableList<SourceT> = ObservableArrayList()): Transformation<ObservableList<SourceT>, ObservableList<SourceT>> {
//                if (!to.isEmpty()) {
//                    throw IllegalStateException("'to' list should be empty: $to")
//                }
//                val fromRegistration = from.addListener(object : CollectionAdapter<SourceT>() {
//                    override fun onItemAdded(event: CollectionItemEvent<out SourceT>) {
//                        val index = from.size - event.index - 1
//                        to.add(index, event.newItem)
//                    }
//
//                    override fun onItemSet(event: CollectionItemEvent<out SourceT>) {
//                        val index = to.size - event.index - 1
//                        to.set(index, event.newItem)
//                    }
//
//                    override fun onItemRemoved(event: CollectionItemEvent<out SourceT>) {
//                        val index = to.size - event.index - 1
//                        to.removeAt(index)
//                    }
//                })
//                val i = from.listIterator(from.size)
//                while (i.hasPrevious()) {
//                    to.add(i.previous())
//                }
//                return SimpleTransformation(from, to, fromRegistration)
//            }
//        }
//    }

}
