package jetbrains.datalore.base.observable.collections

class UnmodifiableObservableCollection<ItemT>(
        private val myWrappedCollection: ObservableCollection<ItemT>) :
        ObservableCollection<ItemT> by myWrappedCollection
