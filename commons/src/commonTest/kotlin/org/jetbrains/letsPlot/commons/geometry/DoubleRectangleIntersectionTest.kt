/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.geometry

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DoubleRectangleIntersectionTest {
    @Test
    fun simple() {
        val r1 = DoubleRectangle(DoubleVector(0.0, 0.0), DoubleVector(100.0, 100.0))
        val r2 = DoubleRectangle(DoubleVector(50.0, 50.0), DoubleVector(100.0, 100.0))

        assertEquals(DoubleRectangle(DoubleVector(50.0, 50.0), DoubleVector(50.0, 50.0)), r1.intersect(r2))
    }

    @Test
    fun oneInsideOfAnother() {
        val r1 = DoubleRectangle(DoubleVector(0.0, 0.0), DoubleVector(100.0, 100.0))
        val r2 = DoubleRectangle(DoubleVector(50.0, 50.0), DoubleVector(10.0, 10.0))

        assertEquals(DoubleRectangle(DoubleVector(50.0, 50.0), DoubleVector(10.0, 10.0)), r1.intersect(r2))
    }

    @Test
    fun noIntersection() {
        val r1 = DoubleRectangle(DoubleVector(0.0, 0.0), DoubleVector(50.0, 50.0))
        val r2 = DoubleRectangle(DoubleVector(100.0, 100.0), DoubleVector(50.0, 50.0))

        assertNull(r1.intersect(r2))
    }
}