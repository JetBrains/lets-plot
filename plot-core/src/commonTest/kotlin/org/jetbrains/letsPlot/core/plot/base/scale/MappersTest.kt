/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

import jetbrains.datalore.base.assertion.assertEquals
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.test.Test

class MappersTest {
    private fun checkWithZeroDomain(rangeLow: Double, rangeHigh: Double) {
        val zeroDomain = DoubleSpan(10.0, 10.0)
        val mapper = Mappers.linear(zeroDomain, rangeLow, rangeHigh, Double.NaN)
        // The range's midpoint in expected
        assertEquals(1.5, mapper(10.0), 0.0)
        assertEquals(1.5, mapper(9.0), 0.0)
        assertEquals(1.5, mapper(11.0), 0.0)
    }

    @Test
    fun linearWithPositiveInfiniteSlop() {
        checkWithZeroDomain(1.0, 2.0)
    }

    @Test
    fun linearWithNegativeInfiniteSlop() {
        checkWithZeroDomain(2.0, 1.0)
    }

    @Test
    fun linearWithNaInput() {
        val naValue = 888.0
        val mapper = Mappers.linear(DoubleSpan(0.0, 1.0), 0.0, 1.0, naValue)
//        Assert.assertEquals(naValue, mapper(null), 0.0)
        assertEquals(naValue, mapper(Double.NaN), 0.0)
        assertEquals(naValue, mapper(Double.NEGATIVE_INFINITY), 0.0)
        assertEquals(naValue, mapper(Double.POSITIVE_INFINITY), 0.0)
    }

//    @Test
//    fun nullable() {
//        val expected = Any()
//        val notNullable = { n: Double? ->
//            if (n == null) {
//                fail("null argument not expected")
//            }
//            n
//        }
//
//        val result = Mappers.nullable(
//                notNullable,
//                expected)(null)
//        assertEquals(expected, result)
//    }
}