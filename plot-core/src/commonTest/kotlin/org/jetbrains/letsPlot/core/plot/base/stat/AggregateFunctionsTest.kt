/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import kotlin.test.Test
import kotlin.test.assertEquals

class AggregateFunctionsTest {
    @Test
    fun emptyData() {
        val values: List<Double> = emptyList()
        assertEquals(0.0, AggregateFunctions.count(values))
        assertEquals(Double.NaN, AggregateFunctions.sum(values))
        assertEquals(Double.NaN, AggregateFunctions.mean(values))
        assertEquals(Double.NaN, AggregateFunctions.median(values))
        assertEquals(Double.NaN, AggregateFunctions.min(values))
        assertEquals(Double.NaN, AggregateFunctions.max(values))
        assertEquals(Double.NaN, AggregateFunctions.quantile(values, 0.25))
    }

    @Test
    fun oneElementData() {
        val value = 1.0
        val values = listOf(value)
        assertEquals(1.0, AggregateFunctions.count(values))
        assertEquals(value, AggregateFunctions.sum(values))
        assertEquals(value, AggregateFunctions.mean(values))
        assertEquals(value, AggregateFunctions.median(values))
        assertEquals(value, AggregateFunctions.min(values))
        assertEquals(value, AggregateFunctions.max(values))
        assertEquals(value, AggregateFunctions.quantile(values, 0.25))
    }

    @Test
    fun checkCountFunction() {
        assertEquals(4.0, AggregateFunctions.count(listOf(-1.0, -1.0, 1.0, 3.0)))
    }

    @Test
    fun checkSumFunction() {
        assertEquals(2.0, AggregateFunctions.sum(listOf(-1.0, -1.0, 1.0, 3.0)))
    }

    @Test
    fun checkMeanFunction() {
        assertEquals(0.5, AggregateFunctions.mean(listOf(-1.0, -1.0, 1.0, 3.0)))
        assertEquals(2.0, AggregateFunctions.mean(listOf(-2.0, 3.0, 5.0)))
    }

    @Test
    fun checkMedianFunction() {
        assertEquals(0.0, AggregateFunctions.median(listOf(-1.0, -1.0, 1.0, 3.0)))
        assertEquals(3.0, AggregateFunctions.median(listOf(-2.0, 3.0, 5.0)))
    }

    @Test
    fun checkMinFunction() {
        assertEquals(-1.0, AggregateFunctions.min(listOf(-1.0, -1.0, 1.0, 3.0)))
    }

    @Test
    fun checkMaxFunction() {
        assertEquals(3.0, AggregateFunctions.max(listOf(-1.0, -1.0, 1.0, 3.0)))
    }

    @Test
    fun checkQuantileFunction() {
        val sortedValues = listOf(-1.0, -1.0, 1.0, 3.0)
        assertEquals(-1.0, AggregateFunctions.quantile(sortedValues, 0.0))
        assertEquals(-1.0, AggregateFunctions.quantile(sortedValues, 0.25))
        assertEquals(-1.0, AggregateFunctions.quantile(sortedValues, 1.0 / 3.0))
        assertEquals(0.0, AggregateFunctions.quantile(sortedValues, 0.5))
        assertEquals(1.0, AggregateFunctions.quantile(sortedValues, 2.0 / 3.0))
        assertEquals(2.0, AggregateFunctions.quantile(sortedValues, 0.75))
        assertEquals(3.0, AggregateFunctions.quantile(sortedValues, 1.0))
    }
}