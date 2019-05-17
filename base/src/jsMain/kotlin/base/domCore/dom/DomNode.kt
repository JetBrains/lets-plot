package jetbrains.datalore.base.domCore.dom

import org.w3c.dom.Node

open class DomNode protected constructor(val node: Node) : DomEventTarget() {

    val nodeType: Short
        get() = node.nodeType

    var nodeValue: String?
        get() = node.nodeValue
        set(value) {
            node.nodeValue = value
        }

    val parentNode: DomNode?
        get() = create(node.parentNode)

    val parentElement: DomElement?
        get() = DomElement.create(node.parentElement)

    val firstChild: DomNode?
        get() = create(node.firstChild)

    val lastChild: DomNode?
        get() = create(node.lastChild)

    val nextSibling: DomNode?
        get() = create(node.nextSibling)

    val ownerDocument: DomDocument?
        get() = node.ownerDocument

    fun appendChild(node: DomNode) {
        this.node.appendChild(node.node)
    }

    fun cloneNode(deep: Boolean): DomNode {
        return create(node.cloneNode(deep))!!
    }

    operator fun contains(node: DomNode): Boolean {
        return this.node.contains(node.node)
    }

    fun replaceChild(newChild: DomNode, oldChild: DomNode) {
        node.replaceChild(newChild.node, oldChild.node)
    }

    fun insertBefore(newChild: DomNode, refChild: DomNode?) {
        node.insertBefore(newChild.node, refChild?.node)
    }

    fun insertFirst(child: DomNode) {
        insertBefore(child, firstChild)
    }

    fun insertAfter(newChild: DomNode, refChild: DomNode?) {
        val next = refChild?.nextSibling
        if (next == null) {
            appendChild(newChild)
        } else {
            insertBefore(newChild, next)
        }
    }

    fun prepend(element: DomNode) {
        if (getChildCount() == 0) {
            appendChild(element)
        } else {
            val refChild = getChild(0)
            insertBefore(element, refChild)
        }
    }

    fun removeChild(oldChild: DomNode): DomNode {
        return create(node.removeChild(oldChild.node))!!
    }

    fun removeAllChildren() {
        while (lastChild != null) {
            removeChild(lastChild!!)
        }
    }

    fun removeFromParent() {
        val parent = parentElement
        parent?.removeChild(this)
    }

    fun getChildCount(): Int {
        return childNodes.length
    }

    fun hasChildNodes(): Boolean {
        return getChildCount() > 0
    }

    val childNodes: DomNodeList
        get() = node.childNodes

    fun getChildNodesList(): List<DomNode> {
        return DomList(this)
    }

    fun getChild(index: Int): DomNode? {
        return create(childNodes.item(index))
    }

    fun replaceWith(node: DomNode) {
        val parent = parentNode ?: throw IllegalStateException("Parent node is null")
        parent.replaceChild(node, this)
    }

    companion object {
        fun create(node: Node?): DomNode? {
            //TODO don't create
            return if (node != null) DomNode(node) else null
        }
    }
}
