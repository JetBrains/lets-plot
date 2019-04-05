package jetbrains.datalore.base.observable.collections.set

import jetbrains.datalore.base.observable.collections.ObservableCollection

interface ObservableSet<T> : MutableSet<T>, ObservableCollection<T>