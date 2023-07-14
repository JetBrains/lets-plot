/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections

interface CollectionListener<ItemT> {
    fun onItemAdded(event: CollectionItemEvent<out ItemT>)
    fun onItemSet(event: CollectionItemEvent<out ItemT>)
    fun onItemRemoved(event: CollectionItemEvent<out ItemT>)
}