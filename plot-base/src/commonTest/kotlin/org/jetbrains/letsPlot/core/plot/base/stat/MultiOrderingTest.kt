/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MultiOrderingTest {
    @Test
    fun emptyKeys() {
        val ordering = MultiOrdering(emptyList<Int>())
        assertTrue(ordering.sortedCopyOfKeys().isEmpty())

        val strings = ordering.sortedCopy(emptyList<String>())
        assertTrue(strings.isEmpty())
    }

    @Test
    fun sortedCopy() {
        val keys = listOf(4, 3, 1, 2, 1)
        val ordering = MultiOrdering(keys)

        val list1 = listOf("Four", "Three", "One", "Two", "One")
        val list2 = listOf('d', 'c', 'a', 'b', 'a')

        assertEquals(listOf("One", "One", "Two", "Three", "Four"), ordering.sortedCopy(list1))
        assertEquals(listOf('a', 'a', 'b', 'c', 'd'), ordering.sortedCopy(list2))
    }

    @Test
    fun sorterCopyOfKeys() {
        val keys = listOf(4, 3, 1, 2, 1)
        val ordering = MultiOrdering(keys)
        assertEquals(listOf(1, 1, 2, 3, 4), ordering.sortedCopyOfKeys())
    }

    @Test
    fun nullKeys() {
        val keys = listOf(4, null, null, 2, 1)
        val ordering = MultiOrdering(keys)
        assertEquals(listOf(null, null, 1, 2, 4), ordering.sortedCopyOfKeys())
    }

    @Test
    fun naKeys() {
        val keys = listOf(4.0, Double.NaN, Double.NaN, 2.0, 1.0)
        val ordering = MultiOrdering(keys)
        assertEquals(listOf(1.0, 2.0, 4.0, Double.NaN, Double.NaN), ordering.sortedCopyOfKeys())
    }
}