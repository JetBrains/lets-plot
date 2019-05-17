package jetbrains.datalore.base.domCore.dom

import org.w3c.dom.events.Event

typealias DomBaseEvent = Event

val DomBaseEvent.eventTarget: DomEventTarget?
    get() = this.target

val DomBaseEvent.currentEventTarget: DomEventTarget?
    get() = this.currentTarget

fun <T> DomBaseEvent.cast(): T {
    return DomCoreUtils.uncheckedCast(this)
}
