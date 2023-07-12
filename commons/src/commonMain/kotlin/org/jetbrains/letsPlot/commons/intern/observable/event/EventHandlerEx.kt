/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.event


fun <EventT> handler(onEvent: (EventT) -> Unit): EventHandler<EventT> {
    return object : EventHandler<EventT> {
        override fun onEvent(event: EventT) {
            onEvent(event)
        }
    }
}
