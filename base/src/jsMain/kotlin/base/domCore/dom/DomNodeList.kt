package jetbrains.datalore.base.domCore.dom

import org.w3c.dom.Node
import org.w3c.dom.NodeList

typealias DomNodeList = NodeList

val DomNodeList.iterator: Iterator<Node>
    get() = DomCollectionIterator(this)

fun DomNodeList.toIterable(): Iterable<Node> {
    return object : Iterable<Node> {
        override fun iterator(): Iterator<Node> {
            return iterator
        }
    }
}
