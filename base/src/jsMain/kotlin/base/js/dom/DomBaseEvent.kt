package jetbrains.datalore.base.js.dom

import org.w3c.dom.events.Event

typealias DomBaseEvent = Event

val DomBaseEvent.eventTarget: DomEventTarget?
    get() = this.target

val DomBaseEvent.currentEventTarget: DomEventTarget?
    get() = this.currentTarget
