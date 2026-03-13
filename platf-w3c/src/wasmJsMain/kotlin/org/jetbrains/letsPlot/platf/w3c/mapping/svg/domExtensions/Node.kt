/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions

import org.w3c.dom.Node
import org.w3c.dom.get

val Node.childCount: Int
    get() = childNodes.length

fun Node.getChild(index: Int): Node? = childNodes[index]

fun Node.insertFirst(child: Node) = insertBefore(child, firstChild)

fun Node.insertAfter(newChild: Node, refChild: Node?) {
    val next = refChild?.nextSibling
    if (next == null) {
        appendChild(newChild)
    } else {
        insertBefore(newChild, next)
    }
}