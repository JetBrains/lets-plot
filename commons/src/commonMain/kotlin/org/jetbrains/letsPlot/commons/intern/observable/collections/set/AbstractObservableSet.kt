/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections.set

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionListener
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import org.jetbrains.letsPlot.commons.registration.Registration

abstract class AbstractObservableSet<ItemT> : AbstractMutableSet<ItemT>(), ObservableSet<ItemT> {
    private var myListeners: Listeners<CollectionListener<in ItemT>>? = null
    abstract val actualIterator: MutableIterator<ItemT>

    override fun addListener(l: CollectionListener<in ItemT>): Registration {
        if (myListeners == null) {
            myListeners = Listeners()
        }
        return myListeners!!.add(l)
    }

    override fun add(element: ItemT): Boolean {
        if (contains(element)) return false
        doBeforeAdd(element)
        var success = false
        try {
            onItemAdd(element)
            success = doAdd(element)
        } finally {
            doAfterAdd(element, success)
        }
        return success
    }

    private fun doBeforeAdd(item: ItemT) {
        checkAdd(item)
        beforeItemAdded(item)
    }

    private fun doAfterAdd(item: ItemT, success: Boolean) {
        try {
            if (success && myListeners != null) {
                myListeners!!.fire(object : ListenerCaller<CollectionListener<in ItemT>> {
                    override fun call(l: CollectionListener<in ItemT>) {
                        l.onItemAdded(
                            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                                null,
                                item,
                                -1,
                                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.ADD
                            )
                        )
                    }
                })
            }
        } finally {
            afterItemAdded(item, success)
        }
    }

    override fun remove(element: ItemT): Boolean {
        if (!contains(element)) return false
        doBeforeRemove(element)
        var success = false
        try {
            onItemRemove(element)
            success = doRemove(element)
        } finally {
            doAfterRemove(element, success)
        }
        return success
    }

    override fun iterator(): MutableIterator<ItemT> {
        if (size == 0) {
            return mutableSetOf<ItemT>().iterator()
        }
        val iterator = actualIterator
        return object : MutableIterator<ItemT> {
            private var myCanRemove = false
            private var myLastReturned: ItemT? = null

            override fun hasNext(): Boolean {
                return iterator.hasNext()
            }

            override fun next(): ItemT {
                myLastReturned = iterator.next()
                myCanRemove = true
                return myLastReturned!!
            }

            override fun remove() {
                if (!myCanRemove) {
                    throw IllegalStateException()
                }
                myCanRemove = false
                doBeforeRemove(myLastReturned!!)
                var success = false
                try {
                    iterator.remove()
                    success = true
                } finally {
                    doAfterRemove(myLastReturned!!, success)
                }
            }
        }
    }

    private fun doBeforeRemove(item: ItemT) {
        checkRemove(item)
        beforeItemRemoved(item)
    }

    private fun doAfterRemove(item: ItemT, success: Boolean) {
        try {
            if (success && myListeners != null) {
                myListeners!!.fire(object : ListenerCaller<CollectionListener<in ItemT>> {
                    override fun call(l: CollectionListener<in ItemT>) {
                        l.onItemRemoved(
                            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                                item,
                                null,
                                -1,
                                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
                            )
                        )
                    }
                })
            }
        } finally {
            afterItemRemoved(item, success)
        }
    }

    protected abstract fun doAdd(item: ItemT): Boolean
    protected abstract fun doRemove(item: ItemT): Boolean

    protected open fun checkAdd(item: ItemT?) {}
    protected open fun checkRemove(item: ItemT?) {}
    protected open fun beforeItemAdded(item: ItemT?) {}
    protected open fun onItemAdd(item: ItemT?) {}
    protected open fun afterItemAdded(item: ItemT?, success: Boolean) {}
    protected open fun beforeItemRemoved(item: ItemT?) {}
    protected open fun onItemRemove(item: ItemT) {}
    protected open fun afterItemRemoved(item: ItemT?, success: Boolean) {}


    override fun addHandler(handler: EventHandler<org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>>): Registration {
        return addListener(object : org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<ItemT>() {
            override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                handler.onEvent(event)
            }

            override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
                handler.onEvent(event)
            }
        })
    }
}