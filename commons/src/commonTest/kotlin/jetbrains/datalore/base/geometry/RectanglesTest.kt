/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RectanglesTest {

    @Test
    fun upperDistance() {
        assertEquals(15, Rectangles.upperDistance(INNER, OUTER))
    }

    @Test
    fun lowerDistance() {
        assertEquals(145, Rectangles.lowerDistance(INNER, OUTER))
    }

    @Test
    fun leftDistance() {
        assertEquals(10, Rectangles.leftDistance(INNER, OUTER))
    }

    @Test
    fun rightDistance() {
        assertEquals(60, Rectangles.rightDistance(INNER, OUTER))
    }

    @Test
    fun badUpperDistance() {
        assertFailsWith<IllegalArgumentException> {
            Rectangles.upperDistance(OUTER, INNER)
        }
    }

    @Test
    fun badLowerDistance() {
        assertFailsWith<IllegalArgumentException> {
            Rectangles.lowerDistance(OUTER, INNER)
        }

    }

    @Test
    fun badLeftDistance() {
        assertFailsWith<IllegalArgumentException> {
            Rectangles.leftDistance(OUTER, INNER)
        }
    }

    @Test
    fun badRightDistance() {
        assertFailsWith<IllegalArgumentException> {
            Rectangles.rightDistance(OUTER, INNER)
        }
    }

    @Test
    fun extendUp() {
        assertEquals(Rectangle(Vector(10, 19), Vector(30, 41)), Rectangles.extendUp(INNER, 1))
    }

    @Test
    fun extendDown() {
        assertEquals(Rectangle(Vector(10, 20), Vector(30, 41)), Rectangles.extendDown(INNER, 1))
    }

    @Test
    fun extendLeft() {
        assertEquals(Rectangle(Vector(9, 20), Vector(31, 40)), Rectangles.extendLeft(INNER, 1))
    }

    @Test
    fun extendRight() {
        assertEquals(Rectangle(Vector(10, 20), Vector(31, 40)), Rectangles.extendRight(INNER, 1))
    }

    @Test
    fun extendSides() {
        assertEquals(Rectangle(Vector(9, 20), Vector(32, 40)), Rectangles.extendSides(1, INNER, 1))
    }

    @Test
    fun shrinkRight() {
        assertEquals(Rectangle(Vector(10, 20), Vector(29, 40)), Rectangles.shrinkRight(INNER, 1))
    }

    @Test
    fun shrinkRightIncorrect() {
        assertFailsWith<IllegalArgumentException> {
            Rectangles.shrinkRight(INNER, INNER.dimension.x + 1)
        }
    }

    companion object {
        private val INNER = Rectangle(10, 20, 30, 40)
        private val OUTER = Rectangle(0, 5, 100, 200)
    }
}