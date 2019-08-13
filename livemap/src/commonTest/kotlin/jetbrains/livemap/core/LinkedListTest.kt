package jetbrains.livemap.core

import kotlin.test.Test
import kotlin.test.assertEquals

class LinkedListTest {

    @Test
    fun createFromList() {
        val list = listOf(1,2,3,4,5)

        val linked = LinkedList(list)

        assertEquals(list, linked.toList())
    }

    @Test
    fun appendTest() {
        val list = listOf(1,2,3,4,5)

        val linked = LinkedList<Int>()

        list.forEach { linked.append(it) }

        assertEquals(list, linked.toList())
    }

    @Test
    fun prependTest() {
        val list = listOf(1,2,3,4,5)

        val linked = LinkedList<Int>()

        list.forEach { linked.prepend(it) }

        assertEquals(listOf(5,4,3,2,1), linked.toList())
    }

    @Test
    fun sublistStartTest() {
        val list = listOf(1,2,3,4,5)
        val sub = listOf(1,2)

        val linked = LinkedList(list)

        assertEquals(sub, linked.subList(0, 2))
    }

    @Test
    fun sublistMiddleTest() {
        val list = listOf(1,2,3,4,5)
        val sub = listOf(2,3,4)

        val linked = LinkedList(list)

        assertEquals(sub, linked.subList(1, 4))
    }

    @Test
    fun sublistEndTest() {
        val list = listOf(1,2,3,4,5)
        val sub = listOf(4,5)

        val linked = LinkedList(list)

        assertEquals(sub, linked.subList(3, linked.size))
    }

    @Test
    fun sublistAllTest() {
        val list = listOf(1,2,3,4,5)

        val linked = LinkedList(list)

        assertEquals(list, linked.subList(0, linked.size))
    }

    @Test
    fun removeLastTest() {
        val list = listOf(1,2,3,4,5)
        val afterRemove = listOf(1,2,3)

        val linked = LinkedList(list)

        assertEquals(afterRemove, linked.apply {
            removeLast()
            removeLast()
        }.toList())
    }

    @Test
    fun removeAllTest() {
        val list = listOf(1,2,3,4,5)
        val afterRemove = emptyList<Int>()

        val linked = LinkedList(list)

        assertEquals(afterRemove, linked.apply {
            removeLast()
            removeLast()
            removeLast()
            removeLast()
            removeLast()
        }.toList())
    }

    @Test
    fun removeByIndexTest() {
        val list = listOf(1,2,3,4,5)

        val linked = LinkedList(list)

        assertEquals(listOf(1,2,4,5), linked.apply {
            remove(2)
        }.toList())

        assertEquals(listOf(2,4,5), linked.apply {
            remove(0)
        }.toList())
    }

    @Test
    fun sizeTest() {
        val list = listOf(1,2,3,4,5)

        val linked = LinkedList<Int>()

        list.forEach { linked.append(it) }

        linked.removeLast()

        assertEquals(4, linked.size)
    }

    @Test
    fun indexOf() {
        val list = listOf(1,2,3,4,5)

        val linked = LinkedList(list)
        val empty = LinkedList<Int>()

        assertEquals(0, linked.indexOf(1))
        assertEquals(2, linked.indexOf(3))
        assertEquals(-1, linked.indexOf(6))
        assertEquals(-1, empty.indexOf(6))
    }
}