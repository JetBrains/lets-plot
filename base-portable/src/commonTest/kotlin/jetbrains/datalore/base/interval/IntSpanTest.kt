/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.interval

import kotlin.test.*

class IntSpanTest {
    private fun range(lower: Number, upper: Number): IntSpan {
        return IntSpan(lower.toInt(), upper.toInt())
    }

    private fun assertUnion(
        expected: IntSpan,
        range0: IntSpan,
        range1: IntSpan
    ) {
        assertEquals(expected, range0.union(range1))
        assertEquals(expected, range1.union(range0))
    }

    private fun assertIntersection(
        expected: IntSpan,
        range0: IntSpan,
        range1: IntSpan
    ) {
        assertEquals(expected, range0.intersection(range1))
        assertEquals(expected, range1.intersection(range0))
    }


    @Test
    fun endpointsNormalization() {
        val r_01 = range(0.0, 1.0)
        val r_10 = range(1.0, 0.0)
        assertTrue(r_01.equals(r_10))
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
        assertFalse(r.connected(range(-3, -2)))
        assertTrue(r.connected(range(-3, -1)))
        assertTrue(r.connected(range(-3, 0)))
        assertTrue(r.connected(range(-1, 0)))
        assertTrue(r.connected(range(-2, 0)))
        assertTrue(r.connected(range(-2, 2)))
        assertTrue(r.connected(range(0, 3)))
        assertTrue(r.connected(range(1, 3)))
        assertFalse(r.connected(range(2, 3)))
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
        assertSame(r, r.union(inner))
        assertSame(r, r.union(r))
        assertSame(r, inner.union(r))

        assertUnion(
            range(-3, 2),
            r,
            range(-3, 1)
        )
        assertUnion(
            range(-2, 3),
            r,
            range(-1, 3)
        )
        assertUnion(
            range(-5, 2),
            r,
            range(-5, -3)
        )
        assertUnion(
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
}