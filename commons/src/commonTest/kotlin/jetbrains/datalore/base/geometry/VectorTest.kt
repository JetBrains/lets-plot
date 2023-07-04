/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class VectorTest {
    @Test
    fun testEquals() {
        assertEquals(Vector(1, 2), Vector(1, 2))
        assertNotEquals(Vector(1, 2), Vector(2, 1))
    }

    @Test
    fun add() {
        assertEquals(Vector(2, 4), Vector(1, 2).add(Vector(1, 2)))
    }

    @Test
    fun sub() {
        assertEquals(Vector(-1, 0), Vector(1, 2).sub(Vector(2, 2)))
    }

    @Test
    fun negate() {
        assertEquals(Vector(-1, -2), Vector(1, 2).negate())
    }

    @Test
    fun max() {
        assertEquals(Vector(2, 2), Vector(1, 2).max(Vector(2, 1)))
    }

    @Test
    fun min() {
        assertEquals(Vector(1, 1), Vector(1, 2).min(Vector(2, 1)))
    }
}
