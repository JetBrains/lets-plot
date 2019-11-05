/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.dom

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
