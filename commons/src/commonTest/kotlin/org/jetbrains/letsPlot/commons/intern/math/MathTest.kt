/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MathTest {
    @Test
    fun testLineParamsRegularLine() {
        val result = lineParams(1.0, 2.0, 3.0, 4.0)
        assertEquals(1.0 to 1.0, result)
    }

    @Test
    fun testLineParamsVerticalLine() {
        val result = lineParams(1.0, 2.0, 1.0, 4.0)
        assertEquals(result.first, Double.POSITIVE_INFINITY)
        assertEquals(result.second, Double.NEGATIVE_INFINITY)
    }

    @Test
    fun testLineParamsHorizontalLine() {
        val result = lineParams(1.0, 2.0, 3.0, 2.0)
        assertEquals(0.0 to 2.0, result)
    }


    @Test
    fun testYOnLineHorizontal() {
        val result = yOnLine(0.0, 0.0, 10.0, 0.0, 5.0)
        assertEquals(0.0, result)
    }

    @Test
    fun testYOnLineVertical() {
        val result = yOnLine(0.0, 0.0, 0.0, 10.0, 5.0)
        assertNull(result)
    }

    @Test
    fun testYOnLineVerticalXEqualPX() {
        val result = yOnLine(5.0, 0.0, 5.0, 10.0, 5.0)
        assertNull(result)
    }

    @Test
    fun testYOnLinePositiveSlope() {
        val result = yOnLine(0.0, 0.0, 10.0, 10.0, 5.0)
        assertEquals(5.0, result)
    }

    @Test
    fun testYOnLineNegativeSlope() {
        val result = yOnLine(0.0, 10.0, 10.0, 0.0, 5.0)
        assertEquals(5.0, result)
    }

    @Test
    fun testYOnLineNonZeroIntercept() {
        val result = yOnLine(1.0, 1.0, 2.0, 2.0, 3.0)
        assertEquals(3.0, result)
    }

    @Test
    fun testXOnLineHorizontalLine() {
        val x = xOnLine(0.0, 0.0, 10.0, 0.0, 5.0)
        assertEquals(Double.POSITIVE_INFINITY, x)
    }

    @Test
    fun testXOnLineVerticalLine() {
        val x = xOnLine(0.0, 0.0, 0.0, 10.0, 5.0)
        assertEquals(0.0, x)
    }

    @Test
    fun testXOnLineDiagonalLine() {
        val x = xOnLine(0.0, 0.0, 10.0, 10.0, 5.0)
        assertEquals(5.0, x)
    }

    @Test
    fun testXOnLineNullForInfiniteSlope() {
        val x = xOnLine(0.0, 0.0, 0.0, 0.0, 5.0)
        assertNull(x)
    }
}