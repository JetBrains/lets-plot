/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.gcommon.collect

import jetbrains.datalore.base.gcommon.collect.IterablesTest.Companion.iterable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OrderingTest {

    @Test
    fun isOrdered() {
        assertTrue(Ordering.natural<String>().isOrdered(listOf("a", "b", "c")))
        assertTrue(Ordering.natural<String>().isOrdered(iterable("a", "b", "c")))
        assertFalse(Ordering.natural<String>().isOrdered(listOf("a", "c", "b")))
        assertFalse(Ordering.natural<String>().isOrdered(iterable("a", "c", "b")))
    }

    @Test
    fun sortedCopyLi() {
        val sorted = Ordering.natural<String>().sortedCopy(listOf("a", "c", "b"))
        assertEquals(listOf("a", "b", "c"), sorted)
    }

    @Test
    fun sortedCopyIt() {
        val sorted = Ordering.natural<String>().sortedCopy(iterable("a", "c", "b"))
        assertEquals(listOf("a", "b", "c"), sorted)
    }

    @Test
    fun reverseIt() {
        val sorted = Ordering.natural<String>().reverse().sortedCopy(iterable("a", "c", "b"))
        assertEquals(listOf("c", "b", "a"), sorted)
    }

    @Test
    fun reverseLi() {
        val sorted = Ordering.natural<String>().reverse().sortedCopy(iterable("a", "c", "b"))
        assertEquals(listOf("c", "b", "a"), sorted)
    }

    @Test
    fun min() {
        assertEquals("a", Ordering.natural<String>().min(iterable("c", "a", "b")))
    }

    @Test
    fun max() {
        assertEquals("c", Ordering.natural<String>().max(iterable("a", "c", "b")))
    }
}