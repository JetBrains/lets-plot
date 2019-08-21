package jetbrains.livemap.core


class LinkedList<T> : MutableIterable<T> {
    override fun iterator(): MutableIterator<T> {
        return NodeIterator()
    }

    constructor()

    constructor(elements: List<T>) {
        var current: Node<T>? = null
        for (e in elements) {
            if (current == null) {
                current = Node(e, null, null)
                head = current
            } else {
                current.myNext = Node(e, current, null)
                current = current.myNext
            }
        }
    }

    val size: Int
        get() {
            var count = 0
            var node = head
            while (node != null) {
                node = node.myNext
                count ++
            }

            return count
        }

    private var head: Node<T>? = null

    fun get(index: Int): T {
        return getNodeByIndex(index).myItem
    }

    fun append(element: T) {
        val node = lastNode()

        if (node == null) {
            head = Node(element, null, null)
        } else {
            node.myNext = Node(element, node, null)
        }
    }

    fun prepend(element: T) {
        val node = Node(element, null, head)
        head?.myPrev = node

        head = node
    }

    fun isEmpty(): Boolean = head == null

    private fun remove(node: Node<T>) {
        if (node == head) {
            head = head!!.myNext
            head?.myPrev = null
        } else {
            node.myPrev?.myNext = node.myNext
            node.myNext?.myPrev = node.myPrev
        }
    }

    fun remove(index: Int) {
        remove(getNodeByIndex(index))
    }

    fun removeFirst() = remove(0)

    fun removeLast() = remove(size - 1)

    private fun lastNode(): Node<T>? {
        var node: Node<T> = head ?: return null

        while (node.myNext != null) {
            node = node.myNext!!
        }

        return node
    }

    private fun getNodeByIndex(index: Int): Node<T> {
        var node: Node<T> = head ?: throw IndexOutOfBoundsException()

        repeat(index) {
            node = node.myNext ?: throw IndexOutOfBoundsException()
        }

        return node
    }

    fun subList(fromIndex: Int, toIndex: Int): List<T> {
        var node: Node<T>? = getNodeByIndex(fromIndex)
        val resultList = ArrayList<T>()

        for (i in fromIndex until toIndex) {
            if (node == null) throw IndexOutOfBoundsException()

            resultList.add(node.myItem)
            node = node.myNext
        }

        return resultList
    }

    fun toList(): List<T> {
        val resultList = ArrayList<T>()

        var node: Node<T>? = head ?: return emptyList()
        while (node != null) {
            resultList.add(node.myItem)

            node = node.myNext
        }

        return resultList
    }

    fun indexOf(element: T): Int {
        var node: Node<T>? = head
        var index = -1
        var i = index

        while (node != null) {
            i++
            if (node.myItem == element) {
                index = i
                break
            }
            node = node.myNext
        }

        return index
    }

    private class Node<T> internal constructor(
        internal var myItem: T,
        internal var myPrev: Node<T>?,
        internal var myNext: Node<T>?
    )

    inner class NodeIterator : MutableIterator<T> {
        private var node: Node<T>? = null

        private var isFirst = true

        override fun hasNext() = (isFirst && head != null) || node!!.myNext != null

        override fun next(): T {
            node = head!!.takeIf { isFirst } ?: node!!.myNext

            isFirst = false

            return node!!.myItem
        }

        override fun remove() {
            remove(node!!)
        }
    }
}