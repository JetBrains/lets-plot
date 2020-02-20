/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.event

class EventListeners<ListenerT, EventT : ListenerEvent<ListenerT>> : Listeners<ListenerT>() {
    fun fire(event: EventT) {
        fire(object : ListenerCaller<ListenerT> {
            override fun call(l: ListenerT) {
                event.dispatch(l)
            }
        })
    }
}