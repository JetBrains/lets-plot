package jetbrains.datalore.base.domCore.dom

import org.w3c.dom.Element
import org.w3c.dom.HTMLCollection

typealias DomElementList = HTMLCollection

val DomElementList.iterator: Iterator<Element>
    get() = DomCollectionIterator(this)

fun DomElementList.toIterable(): Iterable<Element> {
    return object : Iterable<Element> {
        override fun iterator(): Iterator<Element> {
            return iterator
        }
    }
}
