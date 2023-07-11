/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.event

/**
 * Interface which should be implemented by events which you fire via [EventListeners]
 * @param <ListenerT>
</ListenerT> */
interface ListenerEvent<ListenerT> {
    fun dispatch(l: ListenerT)
}