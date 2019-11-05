/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.dom

import org.w3c.dom.events.Event

typealias DomBaseEvent = Event

val DomBaseEvent.eventTarget: DomEventTarget?
    get() = this.target

val DomBaseEvent.currentEventTarget: DomEventTarget?
    get() = this.currentTarget
