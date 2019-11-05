/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.observable.collections.ObservableCollection
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration

internal abstract class SelectedCollection<ValueT, ItemT, CollectionT : ObservableCollection<*>>
protected constructor(
        private val mySource: ReadableProperty<out ValueT>,
        private val mySelector: (ValueT) -> CollectionT) :

        ObservableArrayList<ItemT>(),
        EventHandler<PropertyChangeEvent<out ValueT>> {

    private var mySourcePropertyRegistration = Registration.EMPTY
    private var mySourceListRegistration = Registration.EMPTY

    protected var isFollowing = false
        private set

    override val size: Int
        get() = if (isFollowing) {
            super.size
        } else {
            select().size
        }

    protected abstract fun follow(source: CollectionT): Registration

    protected abstract fun empty(): CollectionT

    protected fun select(): CollectionT {
        val sourceVal = mySource.get()
        if (sourceVal != null) {
            val res = mySelector(sourceVal)
            if (res != null) return res
        }

        return empty()
    }

    override fun onEvent(event: PropertyChangeEvent<out ValueT>) {
        if (isFollowing) {
            mySourceListRegistration.remove()
            mySourceListRegistration = follow(select())
        }
    }

    override fun onListenersAdded() {
        val handler = this
        mySourcePropertyRegistration = mySource.addHandler(handler)
        isFollowing = true
        mySourceListRegistration = follow(select())
    }

    override fun onListenersRemoved() {
        mySourcePropertyRegistration.remove()
        isFollowing = false
        mySourceListRegistration.remove()
    }
}