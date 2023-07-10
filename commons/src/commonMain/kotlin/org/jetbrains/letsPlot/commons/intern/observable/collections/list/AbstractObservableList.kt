/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections.list

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionListener
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import jetbrains.datalore.base.registration.Registration

abstract class AbstractObservableList<ItemT> : AbstractMutableList<ItemT>(),
    ObservableList<ItemT> {
    private var myListeners: Listeners<CollectionListener<in ItemT>>? = null

    protected open fun checkAdd(index: Int, item: ItemT) {
        if (index < 0 || index > size) {
            throw IndexOutOfBoundsException("Add: index=$index, size=$size")
        }
    }

    protected open fun checkSet(index: Int, oldItem: ItemT, newItem: ItemT) {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Set: index=$index, size=$size")
        }
    }

    protected open fun checkRemove(index: Int, item: ItemT) {
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Remove: index=$index, size=$size")
        }
    }

    override fun add(index: Int, element: ItemT) {
        checkAdd(index, element)
        beforeItemAdded(index, element)
        var success = false
        try {
            doAdd(index, element)
            success = true
            onItemAdd(index, element)
            if (myListeners != null) {
                val event = org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                    null,
                    element,
                    index,
                    org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.ADD
                )
                myListeners!!.fire(object : ListenerCaller<CollectionListener<in ItemT>> {
                    override fun call(l: CollectionListener<in ItemT>) {
                        l.onItemAdded(event)
                    }
                })
            }
        } finally {
            afterItemAdded(index, element, success)
        }
    }

    protected abstract fun doAdd(index: Int, item: ItemT)

    protected open fun beforeItemAdded(index: Int, item: ItemT) {}

    protected open fun onItemAdd(index: Int, item: ItemT) {}

    protected open fun afterItemAdded(index: Int, item: ItemT, success: Boolean) {}

    override operator fun set(index: Int, element: ItemT): ItemT {
        val old = get(index)
        checkSet(index, old, element)
        beforeItemSet(index, old, element)
        var success = false
        try {
            doSet(index, element)
            success = true
            onItemSet(index, old, element)
            if (myListeners != null) {
                val event = org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                    old,
                    element,
                    index,
                    org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.SET
                )
                myListeners!!.fire(object : ListenerCaller<CollectionListener<in ItemT>> {
                    override fun call(l: CollectionListener<in ItemT>) {
                        l.onItemSet(event)
                    }
                })
            }
        } finally {
            afterItemSet(index, old, element, success)
        }
        return old
    }

    protected open fun doSet(index: Int, item: ItemT) {
        doRemove(index)
        doAdd(index, item)
    }

    protected open fun beforeItemSet(index: Int, oldItem: ItemT, newItem: ItemT) {}

    protected open fun onItemSet(index: Int, oldItem: ItemT, newItem: ItemT) {}

    protected open fun afterItemSet(index: Int, oldItem: ItemT, newItem: ItemT, success: Boolean) {}

    override fun removeAt(index: Int): ItemT {
        val item = get(index)
        checkRemove(index, item)
        beforeItemRemoved(index, item)
        var success = false
        try {
            doRemove(index)
            success = true
            onItemRemove(index, item)
            if (myListeners != null) {
                val event = org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                    item,
                    null,
                    index,
                    org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
                )
                myListeners!!.fire(object : ListenerCaller<CollectionListener<in ItemT>> {
                    override fun call(l: CollectionListener<in ItemT>) {
                        l.onItemRemoved(event)
                    }
                })
            }
        } finally {
            afterItemRemoved(index, item, success)
        }
        return item
    }

    protected abstract fun doRemove(index: Int)

    protected open fun beforeItemRemoved(index: Int, item: ItemT) {}

    protected open fun onItemRemove(index: Int, item: ItemT) {}

    protected open fun afterItemRemoved(index: Int, item: ItemT, success: Boolean) {}

    override fun addListener(l: CollectionListener<in ItemT>): Registration {
        if (myListeners == null) {
            myListeners = object : Listeners<CollectionListener<in ItemT>>() {
                override fun beforeFirstAdded() {
                    onListenersAdded()
                }

                override fun afterLastRemoved() {
                    myListeners = null
                    onListenersRemoved()
                }
            }
        }

        return myListeners!!.add(l)
    }

    override fun addHandler(handler: EventHandler<org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>>): Registration {
        val listener = object :
            CollectionListener<ItemT> {
            override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                handler.onEvent(event)
            }

            override fun onItemSet(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                handler.onEvent(event)
            }

            override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                handler.onEvent(event)
            }
        }
        return addListener(listener)
    }

    protected open fun onListenersAdded() {}

    protected open fun onListenersRemoved() {}
}