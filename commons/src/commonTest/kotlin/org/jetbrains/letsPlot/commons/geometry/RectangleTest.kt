/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import kotlin.test.*


class RectangleTest {
    @Test
    fun equality() {
        assertEquals(Rectangle(Vector(1, 2), Vector(2, 2)), Rectangle(Vector(1, 2), Vector(2, 2)))
        assertNotEquals(Rectangle(Vector(1, 2), Vector(2, 2)), Rectangle(Vector(1, 2), Vector(2, 3)))
    }

    @Test
    fun add() {
        assertEquals(
            Rectangle(Vector(1, 2), Vector(2, 2)),
            Rectangle(Vector(0, 0), Vector(2, 2)).add(Vector(1, 2))
        )
    }

    @Test
    fun sub() {
        assertEquals(
            Rectangle(Vector(0, 0), Vector(2, 2)),
            Rectangle(Vector(1, 2), Vector(2, 2)).sub(Vector(1, 2))
        )
    }

    @Test
    fun contains() {
        val rect = Rectangle(Vector(0, 0), Vector(1, 2))

        assertFalse(rect.contains(Vector(-1, -1)))
        assertTrue(rect.contains(Vector(1, 1)))
    }

    @Test
    fun intersects() {
        assertFalse(Rectangle(0, 0, 1, 1).intersects(Rectangle(2, 2, 1, 1)))
        assertTrue(Rectangle(0, 0, 2, 2).intersects(Rectangle(1, 1, 2, 2)))
    }

    @Test
    fun intersection() {
        assertEquals(Rectangle(1, 1, 1, 1), Rectangle(0, 0, 2, 2).intersect(Rectangle(1, 1, 2, 2)))
        assertEquals(Rectangle(1, 1, 2, 2), Rectangle(0, 0, 3, 3).intersect(Rectangle(1, 1, 2, 2)))
    }

    @Test
    fun union() {
        assertEquals(Rectangle(0, 0, 3, 3), Rectangle(0, 0, 1, 1).union(Rectangle(2, 2, 1, 1)))
    }

    @Test
    fun hashCodeWorks() {
        assertEquals(Rectangle(0, 0, 3, 3).hashCode(), Rectangle(0, 0, 3, 3).hashCode())
    }
}
