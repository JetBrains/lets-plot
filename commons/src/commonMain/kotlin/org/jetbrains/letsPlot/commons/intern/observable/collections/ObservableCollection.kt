/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.collections

import org.jetbrains.letsPlot.commons.intern.observable.event.EventSource
import org.jetbrains.letsPlot.commons.registration.Registration

interface ObservableCollection<ItemT> : MutableCollection<ItemT>, EventSource<org.jetbrains.letsPlot.commons.intern.observable.collections.CollectionItemEvent<out ItemT>> {

    fun addListener(l: CollectionListener<in ItemT>): Registration
}