/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import kotlin.test.Test
import kotlin.test.assertEquals


class FiveNumberSummaryTest {
    @Test
    fun simple() {
        val data = listOf(0.0, 1.0, 2.0, 3.0, 4.0)
        val summary = FiveNumberSummary(data)

        assertEquals(FiveNumberSummary(0.0, 4.0, 2.0, 1.0, 3.0), summary)
    }

    @Test
    fun emptyData() {
        val summary = FiveNumberSummary(emptyList())

        assertEquals(
            FiveNumberSummary(
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN,
                Double.NaN
            ), summary)
    }

    @Test
    fun oneElementData() {
        val summary = FiveNumberSummary(listOf(3.3))

        assertEquals(FiveNumberSummary(3.3, 3.3, 3.3, 3.3, 3.3), summary)
    }

    @Test
    fun twoElementData() {
        val summary = FiveNumberSummary(listOf(1.0, 3.0))

        assertEquals(FiveNumberSummary(1.0, 3.0, 2.0, 2.0, 2.0), summary)
    }

    @Test
    fun fiveElementData() {
        val data = listOf(4.0, 3.0, 1.0, 2.0, 1.0)
        val summary = FiveNumberSummary(data)

        assertEquals(FiveNumberSummary(1.0, 4.0, 2.0, 1.0, 3.0), summary)
    }

    @Test
    fun fourElementData() {
        val data = listOf(3.0, 1.0, 2.0, 1.0)
        val summary = FiveNumberSummary(data)

        assertEquals(FiveNumberSummary(1.0, 3.0, 1.5, 1.0, 2.5), summary)
    }
}