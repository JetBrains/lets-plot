/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull

class DoubleRectanglesTest {

    @Test
    fun boundingBox_empty_returnsNull() {
        val points = emptyList<DoubleVector>()
        val box = DoubleRectangles.boundingBox(points)
        assertNull(box)
    }

    @Test
    fun boundingBox_singlePoint_returnsZeroSizeRectangleAtPoint() {
        val p = DoubleVector(1.0, 2.0)
        val box = DoubleRectangles.boundingBox(listOf(p))
        assertNotNull(box)
        assertEquals(1.0, box.origin.x, 1e-9)
        assertEquals(2.0, box.origin.y, 1e-9)
        // span of single point -> zero dimension
        assertEquals(0.0, box.dimension.x, 1e-9)
        assertEquals(0.0, box.dimension.y, 1e-9)
    }

    @Test
    fun boundingBox_multiplePoints_computesMinMax() {
        val pts = listOf(
            DoubleVector(1.0, 2.0),
            DoubleVector(3.0, 4.0),
            DoubleVector(0.0, 5.0)
        )
        val box = DoubleRectangles.boundingBox(pts)
        assertNotNull(box)
        // expected minX = 0.0, minY = 2.0, maxX = 3.0, maxY = 5.0
        assertEquals(0.0, box.origin.x, 1e-9)
        assertEquals(2.0, box.origin.y, 1e-9)
        assertEquals(3.0, box.dimension.x, 1e-9) // width = maxX - minX
        assertEquals(3.0, box.dimension.y, 1e-9) // height = maxY - minY
    }

    @Test
    fun calculateBoundingBox_withCustomPoints_usesGetters() {
        data class P(val a: Double, val b: Double)

        val pts = listOf(P(2.0, 7.0), P(-1.0, 3.0))
        val box = DoubleRectangles.calculateBoundingBox(
            points = pts,
            getX = { it.a },
            getY = { it.b }
        ) { minX, minY, maxX, maxY ->
            DoubleRectangle.span(DoubleVector(minX, minY), DoubleVector(maxX, maxY))
        }

        assertNotNull(box)
        // expected minX = -1.0, minY = 3.0, maxX = 2.0, maxY = 7.0
        assertEquals(-1.0, box.origin.x, 1e-9)
        assertEquals(3.0, box.origin.y, 1e-9)
        assertEquals(3.0, box.dimension.x, 1e-9) // 2 - (-1)
        assertEquals(4.0, box.dimension.y, 1e-9) // 7 - 3
    }

    @Test
    fun extend_methods_adjustOriginAndDimension() {
        val base = DoubleRectangle.span(DoubleVector(1.0, 1.0), DoubleVector(4.0, 5.0))
        // base: origin (1,1), dimension (3,4)

        val up = DoubleRectangles.extendUp(base, 2.0)
        // up: origin.y = 1 - 2 = -1, dimension.y = 4 + 2 = 6
        assertEquals(1.0, up.origin.x, 1e-9)
        assertEquals(-1.0, up.origin.y, 1e-9)
        assertEquals(3.0, up.dimension.x, 1e-9)
        assertEquals(6.0, up.dimension.y, 1e-9)

        val down = DoubleRectangles.extendDown(base, 2.0)
        // down: origin unchanged, dimension.y = 4 + 2 = 6
        assertEquals(1.0, down.origin.x, 1e-9)
        assertEquals(1.0, down.origin.y, 1e-9)
        assertEquals(3.0, down.dimension.x, 1e-9)
        assertEquals(6.0, down.dimension.y, 1e-9)

        val left = DoubleRectangles.extendLeft(base, 2.0)
        // left: origin.x = 1 - 2 = -1, dimension.x = 3 + 2 = 5
        assertEquals(-1.0, left.origin.x, 1e-9)
        assertEquals(1.0, left.origin.y, 1e-9)
        assertEquals(5.0, left.dimension.x, 1e-9)
        assertEquals(4.0, left.dimension.y, 1e-9)

        val right = DoubleRectangles.extendRight(base, 2.0)
        // right: origin unchanged, dimension.x = 3 + 2 = 5
        assertEquals(1.0, right.origin.x, 1e-9)
        assertEquals(1.0, right.origin.y, 1e-9)
        assertEquals(5.0, right.dimension.x, 1e-9)
        assertEquals(4.0, right.dimension.y, 1e-9)
    }

    @Test
    fun extend_methods_withNegativeDistances_produceNegativeDimensions() {
        val base = DoubleRectangle.span(DoubleVector(1.0, 1.0), DoubleVector(4.0, 5.0))
        // base: origin (1,1), dimension (3,4)

        val upNeg = DoubleRectangles.extendUp(base, -6.0)
        // origin.y = 1 - (-6) = 7.0, dimension.y = 4 + (-6) = -2.0
        assertEquals(1.0, upNeg.origin.x, 1e-9)
        assertEquals(7.0, upNeg.origin.y, 1e-9)
        assertEquals(3.0, upNeg.dimension.x, 1e-9)
        assertEquals(-2.0, upNeg.dimension.y, 1e-9)

        val downNeg = DoubleRectangles.extendDown(base, -6.0)
        // origin unchanged, dimension.y = 4 + (-6) = -2.0
        assertEquals(1.0, downNeg.origin.x, 1e-9)
        assertEquals(1.0, downNeg.origin.y, 1e-9)
        assertEquals(3.0, downNeg.dimension.x, 1e-9)
        assertEquals(-2.0, downNeg.dimension.y, 1e-9)

        val leftNeg = DoubleRectangles.extendLeft(base, -6.0)
        // origin.x = 1 - (-6) = 7.0, dimension.x = 3 + (-6) = -3.0
        assertEquals(7.0, leftNeg.origin.x, 1e-9)
        assertEquals(1.0, leftNeg.origin.y, 1e-9)
        assertEquals(-3.0, leftNeg.dimension.x, 1e-9)
        assertEquals(4.0, leftNeg.dimension.y, 1e-9)

        val rightNeg = DoubleRectangles.extendRight(base, -6.0)
        // origin unchanged, dimension.x = 3 + (-6) = -3.0
        assertEquals(1.0, rightNeg.origin.x, 1e-9)
        assertEquals(1.0, rightNeg.origin.y, 1e-9)
        assertEquals(-3.0, rightNeg.dimension.x, 1e-9)
        assertEquals(4.0, rightNeg.dimension.y, 1e-9)
    }
}
