package jetbrains.datalore.base.js.dom

import org.w3c.dom.HTMLCollection

typealias DomElementList = HTMLCollection

val DomElementList.iterator: Iterator<DomElement>
    get() = DomCollectionIterator(this)

fun DomElementList.toIterable(): Iterable<DomElement> {
    return object : Iterable<DomElement> {
        override fun iterator(): Iterator<DomElement> {
            return iterator
        }
    }
}
