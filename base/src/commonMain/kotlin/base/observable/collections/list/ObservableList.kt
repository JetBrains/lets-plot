package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.observable.collections.ObservableCollection

interface ObservableList<ItemT> : MutableList<ItemT>, ObservableCollection<ItemT>