/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core

import org.jetbrains.letsPlot.livemap.containers.PriorityQueue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PriorityQueueTest {
    @Test
    fun addSorted() {
        val expected = arrayListOf(1, 2, 3, 4, 5)
        val priority = PriorityQueue(compareBy { a: Int -> a })

        expected.forEach { priority.add(it) }

        assertEquals(expected, priority.toArray())
    }

    @Test
    fun addShuffle() {
        val expected = arrayListOf(5, 1, 4, 2, 3)
        val priority = PriorityQueue(compareBy { a: Int -> a })

        expected.forEach { priority.add(it) }

        assertEquals(expected.sorted(), priority.toArray())
    }

    @Test
    fun addEquals() {
        val expected = arrayListOf(2, 1, 3, 1, 4)
        val priority = PriorityQueue(compareBy { a: Int -> a })

        expected.forEach { priority.add(it) }

        assertEquals(expected.sorted(), priority.toArray())
    }

    @Test
    fun peekTest() {
        val source = arrayListOf(2, 5, 3, 1, 4)
        val priority = PriorityQueue(compareBy { a: Int -> a })

        source.forEach { priority.add(it) }

        assertEquals(1, priority.peek())
    }

    @Test
    fun peekFromEmptyTest() {
        val priority = PriorityQueue(compareBy { a: Int -> a })

        assertNull(priority.peek())
    }

    @Test
    fun clearTest() {
        val source = arrayListOf(2, 5, 3, 1, 4)
        val priority = PriorityQueue(compareBy { a: Int -> a })

        source.forEach { priority.add(it) }

        assertNotNull(priority.peek())

        priority.clear()

        assertNull(priority.peek())
    }
}