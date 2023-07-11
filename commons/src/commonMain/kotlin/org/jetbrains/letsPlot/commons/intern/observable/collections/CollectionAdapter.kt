/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections

open class CollectionAdapter<ItemT> :
    org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionListener<ItemT> {
    override fun onItemAdded(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {}

    override fun onItemSet(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {
        onItemRemoved(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                event.oldItem,
                null,
                event.index,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE
            )
        )
        onItemAdded(
            org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent(
                null,
                event.newItem,
                event.index,
                org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.ADD
            )
        )
    }

    override fun onItemRemoved(event: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>) {}
}