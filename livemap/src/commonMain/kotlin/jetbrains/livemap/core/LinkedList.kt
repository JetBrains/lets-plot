package jetbrains.livemap.core


class LinkedList<E> {

    constructor()

    constructor(elements: List<E>) {
        var current: Node<E>? = null
        for (e in elements) {
            if (current == null) {
                current = Node(e, null, null)
                head = current
            } else {
                current.next = Node(e, current, null)
                current = current.next
            }
        }
    }

    val size: Int
        get() {
            var count = 0
            var node = head
            while (node != null) {
                node = node.next
                count ++
            }

            return count
        }

    private var head: Node<E>? = null

    fun get(index: Int): E {
        return getNodeByIndex(index).item
    }

    fun append(element: E) {
        val node = lastNode()

        if (node == null) {
            head = Node(element, null, null)
        } else {
            node.next = Node(element, node, null)
        }
    }

    fun prepend(element: E) {
        val node = Node(element, null, head)
        head?.prev = node

        head = node
    }

    fun remove(index: Int) {
        if (index == 0) removeFirst()
        else {
            val node = getNodeByIndex(index)

            node.prev?.next = node.next
            node.next?.prev = node.prev
        }
    }

    fun removeFirst() = when (val next = head?.next) {
        null -> head = null
        else -> {
            next.prev = null
            head = next
        }
    }

    fun removeLast() = when (val prev = lastNode()?.prev) {
        null -> head = null
        else -> prev.next = null
    }

    private fun lastNode(): Node<E>? {
        var node: Node<E> = head ?: return null

        while (node.next != null) {
            node = node.next!!
        }

        return node
    }

    private fun getNodeByIndex(index: Int): Node<E> {
        var i = index
        var node: Node<E> = head ?: throw IndexOutOfBoundsException()

        while (i > 0) {
            node = node.next ?: throw IndexOutOfBoundsException()
            i--
        }

        return node
    }

    fun subList(fromIndex: Int, toIndex: Int): List<E> {
        var node: Node<E>? = getNodeByIndex(fromIndex)
        val resultList = ArrayList<E>()

        for (i in fromIndex until toIndex) {
            if (node == null) throw IndexOutOfBoundsException()

            resultList.add(node.item)
            node = node.next
        }

        return resultList
    }

    fun toList(): List<E> {
        val resultList = ArrayList<E>()

        var node: Node<E>? = head ?: return emptyList()
        while (node != null) {
            resultList.add(node.item)

            node = node.next
        }

        return resultList
    }

    fun indexOf(element: E): Int {
        var node: Node<E>? = head
        var index = -1
        var i = index

        while (node != null) {
            i++
            if (node.item == element) {
                index = i
                break
            }
            node = node.next
        }

        return index
    }

    class Node<E> internal constructor(
        internal var item: E,
        internal var prev: Node<E>?,
        internal var next: Node<E>?
    )
}