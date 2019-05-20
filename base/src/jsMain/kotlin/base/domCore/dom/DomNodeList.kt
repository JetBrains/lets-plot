package jetbrains.datalore.base.domCore.dom

import org.w3c.dom.NodeList

typealias DomNodeList = NodeList

val DomNodeList.iterator: Iterator<DomNode>
    get() = DomCollectionIterator(this)

fun DomNodeList.toIterable(): Iterable<DomNode> {
    return object : Iterable<DomNode> {
        override fun iterator(): Iterator<DomNode> {
            return iterator
        }
    }
}
