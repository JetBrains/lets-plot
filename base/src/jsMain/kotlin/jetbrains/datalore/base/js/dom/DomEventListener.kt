/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.dom

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

class DomEventListener<EventT : DomBaseEvent>(private val handler: (EventT) -> Boolean) : EventListener {
    override fun handleEvent(event: Event) {
        handler(event as EventT)
    }
}
