/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.event.EventHandler

class RecordingCollectionEventHandler<ItemT> : EventHandler<CollectionItemEvent<out ItemT>> {
    private val myEvents = ArrayList<CollectionItemEvent<out ItemT>>()

    val counter: Int
        get() = myEvents.size

    internal val events: List<CollectionItemEvent<out ItemT>>
        get() = myEvents

    override fun onEvent(event: CollectionItemEvent<out ItemT>) {
        myEvents.add(event)
    }
}