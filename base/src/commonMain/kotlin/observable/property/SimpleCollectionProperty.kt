package jetbrains.datalore.base.observable.property

import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.CollectionListener
import jetbrains.datalore.base.observable.collections.ObservableCollection
import jetbrains.datalore.base.registration.Registration

abstract class SimpleCollectionProperty<ItemT, ValueT> protected constructor(protected val collection: ObservableCollection<ItemT>, initialValue: ValueT) : BaseDerivedProperty<ValueT>(initialValue) {
    private var myRegistration: Registration? = null

    protected fun doAddListeners() {
        myRegistration = collection.addListener(object : CollectionListener<ItemT>() {
            fun onItemAdded(event: CollectionItemEvent<ItemT>) {
                somethingChanged()
            }

            fun onItemSet(event: CollectionItemEvent<ItemT>) {
                somethingChanged()
            }

            fun onItemRemoved(event: CollectionItemEvent<ItemT>) {
                somethingChanged()
            }
        })
    }

    protected fun doRemoveListeners() {
        myRegistration!!.remove()
    }
}