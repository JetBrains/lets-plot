/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.gcommon.collect

import kotlin.test.*

class ClosedRangeTest {
    private fun <T : Comparable<T>> range(lower: T, upper: T): ClosedRange<T> {
        return ClosedRange(lower, upper)
    }

    private fun <T : Comparable<T>> assertSpan(expected: ClosedRange<T>, range0: ClosedRange<T>, range1: ClosedRange<T>) {
        assertEquals(expected, range0.span(range1))
        assertEquals(expected, range1.span(range0))
    }

    private fun <T : Comparable<T>> assertIntersection(expected: ClosedRange<T>, range0: ClosedRange<T>, range1: ClosedRange<T>) {
        assertEquals(expected, range0.intersection(range1))
        assertEquals(expected, range1.intersection(range0))
    }


    @Test
    fun illegalEndpoints() {
        range(0, 1) // ok
        range(0, 0) // ok

        assertFailsWith<IllegalArgumentException> { range(1, 0) }
    }

    @Test
    fun contains() {
        val r = range(-1, 1)
        assertFalse(r.contains(-2))
        assertTrue(r.contains(-1))
        assertTrue(r.contains(0))
        assertTrue(r.contains(1))
        assertFalse(r.contains(2))
    }

    @Test
    fun isConnected() {
        val r = range(-1, 1)
        assertFalse(r.isConnected(range(-3, -2)))
        assertTrue(r.isConnected(range(-3, -1)))
        assertTrue(r.isConnected(range(-3, 0)))
        assertTrue(r.isConnected(range(-1, 0)))
        assertTrue(r.isConnected(range(-2, 0)))
        assertTrue(r.isConnected(range(-2, 2)))
        assertTrue(r.isConnected(range(0, 3)))
        assertTrue(r.isConnected(range(1, 3)))
        assertFalse(r.isConnected(range(2, 3)))
    }

    @Test
    fun encloses() {
        val r = range(-2, 2)
        assertFalse(r.encloses(range(-3, -3)))
        assertFalse(r.encloses(range(-3, -2)))
        assertFalse(r.encloses(range(-3, 0)))
        assertTrue(r.encloses(range(-1, 0)))
        assertTrue(r.encloses(range(-2, 0)))
        assertTrue(r.encloses(range(-2, 2)))
        assertFalse(r.encloses(range(0, 3)))
        assertFalse(r.encloses(range(2, 3)))
        assertFalse(r.encloses(range(3, 3)))
    }

    @Test
    fun span() {
        val r = range(-2, 2)
        val inner = range(-1, 1)
        assertSame(r, r.span(inner))
        assertSame(r, r.span(r))
        assertSame(r, inner.span(r))

        assertSpan(
                range(-3, 2),
                r,
                range(-3, 1)
        )
        assertSpan(
                range(-2, 3),
                r,
                range(-1, 3)
        )
        assertSpan(
                range(-5, 2),
                r,
                range(-5, -3)
        )
        assertSpan(
                range(-2, 5),
                r,
                range(3, 5)
        )
    }


    @Test
    fun intersection() {
        val r = range(-2, 2)
        val inner = range(-1, 1)
        assertSame(inner, r.intersection(inner))
        assertSame(r, r.intersection(r))
        assertSame(inner, inner.intersection(r))

        assertFailsWith<IllegalArgumentException> {
            val outer = range(-5, -3)
            r.intersection(outer)
        }

        assertFailsWith<IllegalArgumentException> {
            val outer = range(3, 5)
            r.intersection(outer)
        }

        assertIntersection(
                range(-2, 1),
                r,
                range(-3, 1)
        )
        assertIntersection(
                range(-1, 2),
                r,
                range(-1, 3)
        )
    }

    @Test
    fun encloseAll() {
        assertFailsWith<NullPointerException> { ClosedRange.encloseAll(listOf(1, null)) }
        assertFailsWith<NoSuchElementException> { ClosedRange.encloseAll<Int>(emptyList()) }

        assertEquals(
                range(-3, 0),
                ClosedRange.encloseAll(listOf(-3, 0)))
    }
}