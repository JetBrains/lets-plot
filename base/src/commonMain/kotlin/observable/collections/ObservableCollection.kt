package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.event.EventSource
import jetbrains.datalore.base.registration.Registration

interface ObservableCollection<ItemT> : MutableCollection<ItemT>, EventSource<CollectionItemEvent<ItemT>> {

    fun addListener(l: CollectionListener<ItemT>): Registration
}