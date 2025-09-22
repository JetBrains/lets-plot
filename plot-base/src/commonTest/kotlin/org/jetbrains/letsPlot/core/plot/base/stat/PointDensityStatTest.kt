/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import kotlin.test.Test
import kotlin.test.assertEquals

class PointDensityStatTest {
    @Test
    fun testFindIntervalWithOneBreak() {
        val breaks = listOf(1.0)
        assertEquals(0, PointDensityStat.findInterval(0.0, breaks))
        assertEquals(0, PointDensityStat.findInterval(1.0, breaks))
        assertEquals(0, PointDensityStat.findInterval(2.0, breaks))
    }

    @Test
    fun testFindIntervalWithFewBreaks() {
        val breaks = listOf(1.0, 2.0, 3.0)
        assertEquals(0, PointDensityStat.findInterval(0.5, breaks))
        assertEquals(1, PointDensityStat.findInterval(1.0, breaks))
        assertEquals(1, PointDensityStat.findInterval(1.5, breaks))
        assertEquals(2, PointDensityStat.findInterval(2.0, breaks))
        assertEquals(2, PointDensityStat.findInterval(2.5, breaks))
        assertEquals(2, PointDensityStat.findInterval(3.0, breaks))
        assertEquals(2, PointDensityStat.findInterval(3.5, breaks))
    }

    @Test
    fun testFindIntervalWithDuplicateBreaks() {
        val breaks = listOf(1.0, 2.0, 2.0, 2.0, 2.0, 2.0, 3.0)
        assertEquals(0, PointDensityStat.findInterval(0.5, breaks))
        assertEquals(1, PointDensityStat.findInterval(1.0, breaks))
        assertEquals(1, PointDensityStat.findInterval(1.5, breaks))
        assertEquals(6, PointDensityStat.findInterval(2.0, breaks))
        assertEquals(6, PointDensityStat.findInterval(2.5, breaks))
        assertEquals(6, PointDensityStat.findInterval(3.0, breaks))
        assertEquals(6, PointDensityStat.findInterval(3.5, breaks))
    }
}