/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.typedGeometry.algorithms

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Polygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Rect
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Ring
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeometryTest {
    private val outerRing = Ring<DoubleVector>(listOf(
        Vec(-2.0, 0.0),
        Vec(4.0, -2.0),
        Vec(9.0, 0.0),
        Vec(9.0, 8.0),
        Vec(-2.0, 8.0),
        Vec(-2.0, 0.0)
    ))

    private val innerRing: Ring<DoubleVector> = Ring(listOf(
        Vec(7.0, 4.0),
        Vec(7.0, 6.0),
        Vec(4.0, 7.0),
        Vec(0.0, 5.0),
        Vec(0.0, 3.0),
        Vec(2.0, 0.0),
        Vec(4.0, 2.0),
        Vec(6.0, 2.0),
        Vec(7.0, 4.0)
    ))

    private val polygon = Polygon(listOf(outerRing, innerRing))

    private val rect = Rect<DoubleVector>(Vec(2.0, 2.0), Vec(3.0, 3.0))

    // Ring

    @Test
    fun testVecWithinRingRegularLine() {
        val vec = Vec<DoubleVector>(2.0, 1.0)
        val out = Vec<DoubleVector>(0.0, 1.0)
        assertTrue { vec.within(innerRing) }
        assertFalse { out.within(innerRing) }
    }

    @Test
    fun testVecWithinRingHorizontalLine() {
        val vec = Vec<DoubleVector>(2.0, 2.0)
        val out = Vec<DoubleVector>(0.0, 2.0)
        assertTrue { vec.within(innerRing) }
        assertFalse { out.within(innerRing) }
    }

    @Test
    fun testVecWithinRingVerticalLine() {
        val vec = Vec<DoubleVector>(2.0, 5.0)
        val out = Vec<DoubleVector>(-1.0, 3.5)
        assertTrue { vec.within(innerRing) }
        assertFalse { out.within(innerRing) }
    }

    /**
     * Expected incorrect result. For point (2, 6) The point lies on the edge,
     * but there is another edge in the ring with which the imaginary ray intersects.
     */
    @Test
    fun testVecWithinRingOnLine() {
        val vec = Vec<DoubleVector>(7.0, 5.0)
        val out = Vec<DoubleVector>(2.0, 6.0)
        assertTrue { vec.within(innerRing) }
        assertFalse { out.within(innerRing) }
    }

    @Test
    fun testVecWithinRingCrossVertex() {
        val vec = Vec<DoubleVector>(4.0, 6.0)
        val out = Vec<DoubleVector>(0.0, 0.0)
        assertTrue { vec.within(innerRing) }
        assertFalse { out.within(innerRing) }
    }

    @Test
    fun testVecWithinRingCrossStartVertex() {
        val vec = Vec<DoubleVector>(2.0, 4.0)
        val out = Vec<DoubleVector>(-1.0, 4.0)
        assertTrue { vec.within(innerRing) }
        assertFalse { out.within(innerRing) }
    }

    /**
     * Expected incorrect result. For a point that is the vertex of a ring.
     * For the case when both edges passing through this point are above or below it.
     */
    @Test
    fun testVecWithinRingInVertex() {
        val vec = Vec<DoubleVector>(2.0, 0.0)
        assertFalse { vec.within(innerRing) }
    }

    @Test
    fun testVecWithinRingInStartVertex() {
        val vec = Vec<DoubleVector>(7.0, 4.0)
        assertTrue { vec.within(innerRing) }
    }

    // Polygon with hole

    @Test
    fun testVecWithinPolygon() {
        val vec = Vec<DoubleVector>(0.0, 1.0)
        assertTrue { vec.within(polygon) }
    }

    @Test
    fun testVecWithinHole() {
        val vec = Vec<DoubleVector>(1.0, 3.0)
        assertFalse { vec.within(polygon) }
    }

    @Test
    fun testVecOutOfPolygon() {
        val vec = Vec<DoubleVector>(-3.0, 2.0)
        assertFalse { vec.within(polygon) }
    }

    @Test
    fun testVeinRectVertices() {
        assertTrue { Vec<DoubleVector>(2.0, 2.0).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(5.0, 2.0).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(5.0, 5.0).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(2.0, 5.0).isOnBorder(rect) }
    }

    @Test
    fun testVecOnEdges() {
        assertTrue { Vec<DoubleVector>(3.0, 2.0).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(5.0, 4.0).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(4.0, 5.0).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(2.0, 3.0).isOnBorder(rect) }
    }

    @Test
    fun testVecIsNotOnRect() {
        assertFalse { Vec<DoubleVector>(3.0, 1.9).isOnBorder(rect) }
        assertFalse { Vec<DoubleVector>(5.1, 4.0).isOnBorder(rect) }
        assertFalse { Vec<DoubleVector>(4.0, 5.1).isOnBorder(rect) }
        assertFalse { Vec<DoubleVector>(1.9, 3.0).isOnBorder(rect) }
    }

    @Test
    fun testVecIsNotOnRect2() {
        assertFalse { Vec<DoubleVector>(1.0, 2).isOnBorder(rect) }
        assertFalse { Vec<DoubleVector>(5.0, 1.0).isOnBorder(rect) }
        assertFalse { Vec<DoubleVector>(5.1, 5.0).isOnBorder(rect) }
        assertFalse { Vec<DoubleVector>(2.0, 1.0).isOnBorder(rect) }
    }

    @Test
    fun testVecIsOnRect() {
        // default epsilon = 1e-5
        assertTrue { Vec<DoubleVector>(3.0, 1.999999).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(5.000001, 4.0).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(4.0, 5.000001).isOnBorder(rect) }
        assertTrue { Vec<DoubleVector>(1.999999, 3.0).isOnBorder(rect) }
    }
}