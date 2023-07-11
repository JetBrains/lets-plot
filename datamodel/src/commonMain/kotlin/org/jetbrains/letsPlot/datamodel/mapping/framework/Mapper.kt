/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableArrayList
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.collections.set.ObservableHashSet
import org.jetbrains.letsPlot.commons.intern.observable.collections.set.ObservableSet
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.datamodel.mapping.framework.composite.HasParent

/**
 * Mapper is an object encapsulating a mapping (usually UI related) from source to target.
 *
 *
 * Responsibilities of a Mapper:
 * - create and configure view
 * - create and configure [Synchronizer]s
 * - configure listeners and handlers on the view
 *
 *
 * Mapper can be in one of the five states:
 * - not attached
 * - attaching synchronizers
 * - attaching children
 * - attached
 * - detached
 *
 *
 * not attached -> attaching synchronizers
 * - Mapper is not attached
 * - onBeforeAttach()
 *
 *
 * attaching synchronizers -> attaching children
 * - registerSynchronizers()
 *
 *
 * attaching children -> attached
 * - attaching children
 * - Mapper is attached
 * - onAttach()
 *
 *
 * attached -> detached
 * - attached
 * - onDetach()
 * - detached
 *
 * @param <SourceT> - source object
 * @param <TargetT> - target object. Usually it's some kind of view
 */
abstract class Mapper<SourceT, TargetT>

/**
 * Construct a mapper with SourceT source and TargetT target
 * NB: DO NOT create disposable resources in constructors. Use either registerSynchronizers or onAttach method.
 */
protected constructor(val source: SourceT, val target: TargetT) : HasParent<Mapper<*, *>> {
    var mappingContext: MappingContext? = null
        private set
    private var myState = State.NOT_ATTACHED

    private var myParts: Array<Any?> = EMPTY_PARTS
    override var parent: Mapper<*, *>? = null

    /**
     * @return Whether this mapper should be findable in [MappingContext]
     */
    open val isFindable: Boolean
        get() = true

    val isAttached: Boolean
        get() = mappingContext != null

    /**
     * Lifecycle method to register [Synchronizer]s in this mapper
     */
    protected open fun registerSynchronizers(conf: SynchronizersConfiguration) {}

    private fun instantiateSynchronizers() {
        registerSynchronizers(object : SynchronizersConfiguration {
            override fun add(sync: Synchronizer) {
                addPart(sync)
            }
        })
    }

    fun <S> getDescendantMapper(source: S): Mapper<in S, *>? {
        return mappingContext!!.getMapper(this, source)
    }

    fun attachRoot(ctx: MappingContext = MappingContext()) {
        if (mappingContext != null) {
            throw IllegalStateException()
        }
        if (parent != null) {
            throw IllegalStateException()
        }
        attach(ctx)
    }

    fun detachRoot() {
        if (mappingContext == null) {
            throw IllegalStateException()
        }
        if (parent != null) {
            throw IllegalStateException("Dispose can be called only on the root mapper")
        }
        detach()
    }

    fun attach(ctx: MappingContext) {
        if (mappingContext != null) {
            throw IllegalStateException("Mapper is already attached")
        }
        if (myState != State.NOT_ATTACHED) {
            throw IllegalStateException("Mapper can't be reused because it was already detached")
        }

        onBeforeAttach(ctx)

        myState = State.ATTACHING_SYNCHRONIZERS
        mappingContext = ctx

        instantiateSynchronizers()

        mappingContext!!.register(this)

        for (part in myParts) {
            if (part is Synchronizer) {
                part.attach(object : SynchronizerContext {
                    override val mappingContext: MappingContext
                        get() = this@Mapper.mappingContext!!

                    override val mapper: Mapper<*, *>
                        get() = this@Mapper
                })
            }
        }

        myState = State.ATTACHING_CHILDREN
        for (part in myParts) {
            if (part is ChildContainer<*>) {
                for (m in part) {
                    m.attach(ctx)
                }
            }
        }

        myState = State.ATTACHED

        onAttach(ctx)
    }

    fun detach() {
        if (mappingContext == null) {
            throw IllegalStateException()
        }

        onDetach()

        for (part in myParts) {
            if (part is Synchronizer) {
                part.detach()
            }
            if (part is ChildContainer<*>) {
                for (m in part) {
                    m.detach()
                }
            }
        }

        mappingContext!!.unregister(this)

        mappingContext = null
        myState = State.DETACHED
        myParts = EMPTY_PARTS
    }

    protected open fun onBeforeAttach(ctx: MappingContext?) {}

    protected open fun onAttach(ctx: MappingContext) {}

    protected open fun onDetach() {}

    private fun addPart(o: Any) {
        val newParts = arrayOfNulls<Any>(myParts.size + 1)
        myParts.copyInto(newParts)
        newParts[newParts.size - 1] = o
        myParts = newParts
    }

    private fun removePart(o: Any) {
        val index = myParts.indexOf(o)
        val newParts = arrayOfNulls<Any>(myParts.size - 1)
        myParts.copyInto(newParts, 0, 0, index)
        myParts.copyInto(newParts, index, index + 1)
        myParts = newParts
    }

    fun synchronizers(): Iterable<Synchronizer> {
        return object : Iterable<Synchronizer> {
            override fun iterator(): Iterator<Synchronizer> {
                return object : PartsIterator<Synchronizer>(myParts.size) {
                    override val nextItem: Synchronizer
                        get() = myParts[currIndex] as Synchronizer

                    override fun toNext(index: Int): Int {
                        var i = index
                        while (i < myParts.size) {
                            if (myParts[i] is Synchronizer) {
                                break
                            }
                            i++
                        }
                        return i
                    }
                }
            }
        }
    }

    fun children(): Iterable<Mapper<*, *>> {
        return object : Iterable<Mapper<*, *>> {
            override fun iterator(): Iterator<Mapper<*, *>> {
                return object : PartsIterator<Mapper<*, *>>(myParts.size) {
                    private var myChildContainerIterator: Iterator<Mapper<*, *>>? = null

                    override val nextItem: Mapper<*, *>
                        get() = myChildContainerIterator!!.next()

                    override fun toNext(index: Int): Int {
                        var i = index
                        if (myChildContainerIterator != null && myChildContainerIterator!!.hasNext()) {
                            return i
                        }
                        while (i < myParts.size) {
                            if (myParts[i] is ChildContainer<*>) {
                                myChildContainerIterator = (myParts[i] as ChildContainer<*>).iterator()
                                break
                            }
                            i++
                        }
                        return i
                    }
                }
            }
        }
    }

    fun <MapperT : Mapper<*, *>> createChildList(): ObservableList<MapperT> {
        return ChildList()
    }

    fun <MapperT : Mapper<*, *>> createChildSet(): ObservableSet<MapperT> {
        return ChildSet()
    }

    fun <MapperT : Mapper<*, *>> createChildProperty(): Property<MapperT?> {
        return ChildProperty()
    }

    private fun addChild(child: Mapper<*, *>) {
        if (myState != State.ATTACHING_SYNCHRONIZERS && myState != State.ATTACHING_CHILDREN && myState != State.ATTACHED) {
            throw IllegalStateException("State =  $myState")
        }

        child.parent = this
        if (myState != State.ATTACHING_SYNCHRONIZERS) {
            child.attach(mappingContext!!)
        }
    }

    private fun removeChild(child: Mapper<*, *>) {
        child.detach()
        child.parent = null
    }

    private fun checkCanAdd(item: Mapper<*, *>) {
        if (item.parent != null) {
            throw IllegalArgumentException()
        }
    }

    private fun checkCanRemove(item: Mapper<*, *>) {
        if (item.parent !== this) {
            throw IllegalArgumentException()
        }
    }

    private inner class ChildProperty<MapperT : Mapper<*, *>> : ValueProperty<MapperT?>(null), ChildContainer<MapperT> {
        override fun set(value: MapperT?) {
            if (get() == null && value != null) {
                addPart(this)
            }
            if (get() != null && value == null) {
                removePart(this)
            }

            val oldValue = get()
            if (oldValue != null) {
                checkCanRemove(oldValue)
                removeChild(oldValue)
            }
            super.set(value)
            if (value != null) {
                checkCanAdd(value)
                addChild(value)
            }
        }

        override fun iterator(): Iterator<MapperT> {
            val value = get() ?: return emptyList<MapperT>().iterator()
            return listOf(value).iterator()
        }
    }

    private inner class ChildList<MapperT : Mapper<*, *>> : ObservableArrayList<MapperT>(), ChildContainer<MapperT> {
        override fun checkAdd(index: Int, item: MapperT) {
            checkCanAdd(item)

            super.checkAdd(index, item)
        }

        override fun checkSet(index: Int, oldItem: MapperT, newItem: MapperT) {
            checkCanRemove(oldItem)
            checkCanAdd(newItem)

            super.checkSet(index, oldItem, newItem)
        }

        override fun checkRemove(index: Int, item: MapperT) {
            checkCanRemove(item)

            super.checkRemove(index, item)
        }

        override fun beforeItemAdded(index: Int, item: MapperT) {
            if (isEmpty()) {
                addPart(this)
            }
            super.beforeItemAdded(index, item)
        }

        override fun afterItemAdded(index: Int, item: MapperT, success: Boolean) {
            super.afterItemAdded(index, item, success)
            addChild(item)
        }

        override fun beforeItemSet(index: Int, oldItem: MapperT, newItem: MapperT) {
            removeChild(oldItem)
            addChild(newItem)
            super.beforeItemSet(index, oldItem, newItem)
        }

        override fun beforeItemRemoved(index: Int, item: MapperT) {
            removeChild(item)
            super.beforeItemRemoved(index, item)
        }

        override fun afterItemRemoved(index: Int, item: MapperT, success: Boolean) {
            if (isEmpty()) {
                removePart(this)
            }
            super.afterItemRemoved(index, item, success)
        }
    }

    private inner class ChildSet<MapperT : Mapper<*, *>> : ObservableHashSet<MapperT>(), ChildContainer<MapperT> {
        override fun checkAdd(item: MapperT?) {
            checkCanAdd(item!!)

            super.checkAdd(item)
        }

        override fun checkRemove(item: MapperT?) {
            checkCanRemove(item!!)

            super.checkRemove(item)
        }

        override fun beforeItemAdded(item: MapperT?) {
            if (isEmpty()) {
                addPart(this)
            }
            super.beforeItemAdded(item)
        }

        override fun afterItemAdded(item: MapperT?, success: Boolean) {
            super.afterItemAdded(item, success)
            addChild(item!!)
        }

        override fun beforeItemRemoved(item: MapperT?) {
            removeChild(item!!)
            super.beforeItemRemoved(item)
        }

        override fun afterItemRemoved(item: MapperT?, success: Boolean) {
            if (isEmpty()) {
                removePart(this)
            }
            super.afterItemRemoved(item, success)
        }
    }

    private interface ChildContainer<MapperT : Mapper<*, *>> : Iterable<MapperT>

    interface SynchronizersConfiguration {
        fun add(sync: Synchronizer)
    }

    private abstract /*inner*/ class PartsIterator<ItemT>(val size: Int) : Iterator<ItemT> {
        private var currIndexInitialized: Boolean = false
        internal var currIndex: Int = -1
            private set

        protected abstract val nextItem: ItemT
        protected abstract fun toNext(index: Int): Int

        override fun hasNext(): Boolean {
            if (!currIndexInitialized) {
                currIndexInitialized = true
                currIndex = toNext(0)
            }
//            return currIndex < myParts.size
            return currIndex < size
        }

        override fun next(): ItemT {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            val next = nextItem
            currIndex = toNext(currIndex + 1)
            return next
        }
    }

    private enum class State {
        NOT_ATTACHED,
        ATTACHING_SYNCHRONIZERS,
        ATTACHING_CHILDREN,
        ATTACHED,
        DETACHED
    }

    companion object {
        private val EMPTY_PARTS = arrayOfNulls<Any>(0)
    }
}