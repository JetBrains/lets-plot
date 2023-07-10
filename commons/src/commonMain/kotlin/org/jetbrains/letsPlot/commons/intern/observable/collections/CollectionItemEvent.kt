/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections

import org.jetbrains.letsPlot.commons.intern.observable.event.ListenerEvent

class CollectionItemEvent<ItemT>(
    val oldItem: ItemT?,
    val newItem: ItemT?,
    val index: Int,
    val type: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType
) :
    ListenerEvent<org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionListener<ItemT>> {

    init {
        if (org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.ADD == type && oldItem != null || org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.REMOVE == type && newItem != null) {
            throw IllegalStateException()
        }
    }

    override fun dispatch(l: org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionListener<ItemT>) {
        if (org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.ADD == type) {
            l.onItemAdded(this)
        } else if (org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.SET == type) {
            l.onItemSet(this)
        } else {
            l.onItemRemoved(this)
        }
    }

    override fun toString(): String {
        return if (org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.ADD == type) {
            newItem.toString() + " added at " + index
        } else if (org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent.EventType.SET == type) {
            oldItem.toString() + " replaced with " + newItem + " at " + index
        } else {
            oldItem.toString() + " removed at " + index
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<*>

        if (oldItem != other.oldItem) return false
        if (newItem != other.newItem) return false
        if (index != other.index) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = oldItem?.hashCode() ?: 0
        result = 31 * result + (newItem?.hashCode() ?: 0)
        result = 31 * result + index
        result = 31 * result + type.hashCode()
        return result
    }

    enum class EventType {
        ADD, SET, REMOVE
    }

}
