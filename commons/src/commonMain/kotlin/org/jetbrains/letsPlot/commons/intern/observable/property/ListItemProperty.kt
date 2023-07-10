/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter
import org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerCaller
import org.jetbrains.letsPlot.commons.intern.observable.event.Listeners
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration

/**
 * Property which represents a value in an observable list at particular index
 */
class ListItemProperty<ValueT>(private val myList: ObservableList<ValueT?>, index: Int) :
    BaseReadableProperty<ValueT?>(),
    Property<ValueT?>, Disposable {

    private val myHandlers: Listeners<EventHandler<PropertyChangeEvent<out ValueT?>>> = Listeners()
    private val myReg: Registration
    private var myDisposed = false

    val index = ValueProperty<Int?>(index)

    val isValid: Boolean
        get() = index.get() != null

    init {
        if (index < 0 || index >= myList.size) {
            throw IndexOutOfBoundsException("$index >= size ${myList.size}")
        }
//        this.index.set(index)

        myReg = myList.addListener(object : org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionAdapter<ValueT?>() {
            override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ValueT?>) {
                @Suppress("NAME_SHADOWING")
                val index = this@ListItemProperty.index.get()
                if (index != null) {
                    if (event.index <= index) {
                        this@ListItemProperty.index.set(index + 1)
                    }
                }
            }

            override fun onItemSet(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ValueT?>) {
                if (event.index == this@ListItemProperty.index.get()) {
                    val e = PropertyChangeEvent<ValueT?>(event.oldItem, event.newItem)
                    myHandlers.fire(object : ListenerCaller<EventHandler<PropertyChangeEvent<out ValueT?>>> {
                        override fun call(l: EventHandler<PropertyChangeEvent<out ValueT?>>) {
                            l.onEvent(e)
                        }
                    })
                }
            }

            override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ValueT?>) {
                @Suppress("NAME_SHADOWING")
                val index = this@ListItemProperty.index.get()
                if (index != null) {
                    if (event.index < index) {
                        this@ListItemProperty.index.set(index - 1)
                    } else if (event.index == index) {
                        invalidate()
                        val e = PropertyChangeEvent<ValueT?>(event.oldItem, null)
                        myHandlers.fire(object : ListenerCaller<EventHandler<PropertyChangeEvent<out ValueT?>>> {
                            override fun call(l: EventHandler<PropertyChangeEvent<out ValueT?>>) {
                                l.onEvent(e)
                            }
                        })
                    }
                }
            }
        })
    }

    override fun addHandler(handler: EventHandler<PropertyChangeEvent<out ValueT?>>): Registration {
        return myHandlers.add(handler)
    }

    override fun get(): ValueT? {
        return if (isValid) {
            myList.get(index.get()!!)
        } else {
            null
        }
    }

    override fun set(value: ValueT?) {
        if (isValid) {
            myList.set(index.get()!!, value)
        } else {
            throw IllegalStateException("Property points to an invalid item, can’t set")
        }
    }

    private fun invalidate() {
        index.set(null)
        myReg.dispose()
    }

    override fun dispose() {
        if (myDisposed) {
            throw IllegalStateException("Double dispose")
        }
        if (isValid) {
            myReg.dispose()
        }
        myDisposed = true
    }

//    fun getIndex(): ReadableProperty<Int?> {
//        return index
//    }
}