/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections

open class CollectionAdapter<ItemT> :
    CollectionListener<ItemT> {
    override fun onItemAdded(event: CollectionItemEvent<out ItemT>) {}

    override fun onItemSet(event: CollectionItemEvent<out ItemT>) {
        onItemRemoved(
            CollectionItemEvent(
                event.oldItem,
                null,
                event.index,
                CollectionItemEvent.EventType.REMOVE
            )
        )
        onItemAdded(
            CollectionItemEvent(
                null,
                event.newItem,
                event.index,
                CollectionItemEvent.EventType.ADD
            )
        )
    }

    override fun onItemRemoved(event: CollectionItemEvent<out ItemT>) {}
}