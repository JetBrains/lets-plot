/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.CollectionListener
import jetbrains.datalore.base.observable.collections.ObservableCollection
import jetbrains.datalore.base.registration.Registration

abstract class SimpleCollectionProperty<ItemT, ValueT>
protected constructor(
    protected val collection: ObservableCollection<ItemT>,
    initialValue: ValueT
) :
    BaseDerivedProperty<ValueT>(initialValue) {
    //        BaseDerivedProperty<ValueT>() {
    private var myRegistration: Registration? = null

    override fun doAddListeners() {
        myRegistration = collection.addListener(object :
            CollectionListener<ItemT> {
            override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {
                somethingChanged()
            }

            override fun onItemSet(event: CollectionItemEvent<out ItemT>) {
                somethingChanged()
            }

            override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {
                somethingChanged()
            }
        })
    }

    override fun doRemoveListeners() {
        myRegistration!!.remove()
    }
}