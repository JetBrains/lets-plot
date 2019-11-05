/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

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
