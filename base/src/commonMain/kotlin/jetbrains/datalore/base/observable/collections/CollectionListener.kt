package jetbrains.datalore.base.observable.collections

interface CollectionListener<ItemT> {
    fun onItemAdded(event: CollectionItemEvent<out ItemT>)
    fun onItemSet(event: CollectionItemEvent<out ItemT>)
    fun onItemRemoved(event: CollectionItemEvent<out ItemT>)
}