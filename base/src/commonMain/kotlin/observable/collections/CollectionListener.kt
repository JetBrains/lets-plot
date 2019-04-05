package jetbrains.datalore.base.observable.collections

interface CollectionListener<ItemT> {
    fun onItemAdded(event: CollectionItemEvent<ItemT>)
    fun onItemSet(event: CollectionItemEvent<ItemT>)
    fun onItemRemoved(event: CollectionItemEvent<ItemT>)
}