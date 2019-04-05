package jetbrains.datalore.base.observable.collections

open class CollectionAdapter<ItemT> : CollectionListener<ItemT> {
    override fun onItemAdded(event: CollectionItemEvent<ItemT>) {}

    override fun onItemSet(event: CollectionItemEvent<ItemT>) {
        onItemRemoved(
                CollectionItemEvent(event.oldItem, null, event.index, CollectionItemEvent.EventType.REMOVE))
        onItemAdded(
                CollectionItemEvent(null, event.newItem, event.index, CollectionItemEvent.EventType.ADD))
    }

    override fun onItemRemoved(event: CollectionItemEvent<ItemT>) {}
}