package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.collections.ObservableCollection

class UnmodifiableObservableCollection<ItemT>(
        private val myWrappedCollection: ObservableCollection<ItemT>) :
        ObservableCollection<ItemT> by myWrappedCollection
