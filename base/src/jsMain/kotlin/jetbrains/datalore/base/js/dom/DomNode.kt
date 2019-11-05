/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.dom

import org.w3c.dom.Node

typealias DomNode = Node

fun DomNode.insertFirst(child: DomNode) {
    insertBefore(child, firstChild)
}

fun DomNode.insertAfter(newChild: DomNode, refChild: DomNode?) {
    val next = refChild?.nextSibling
    if (next == null) {
        appendChild(newChild)
    } else {
        insertBefore(newChild, next)
    }
}

fun DomNode.prepend(element: DomNode) {
    if (getChildCount() == 0) {
        appendChild(element)
    } else {
        val refChild = getChild(0)
        insertBefore(element, refChild)
    }
}

fun DomNode.removeAllChildren() {
    while (lastChild != null) {
        removeChild(lastChild!!)
    }
}

fun DomNode.removeFromParent() {
    val parent = parentElement
    parent?.removeChild(this)
}

fun DomNode.getChildCount(): Int {
    return childNodes.length
}

fun DomNode.getChildNodesList(): List<DomNode> {
    return DomList(this)
}

fun DomNode.getChild(index: Int): DomNode? {
    return childNodes.item(index)
}

fun DomNode.replaceWith(node: DomNode) {
    val parent = parentNode ?: throw IllegalStateException("Parent node is null")
    parent.replaceChild(node, this)
}
