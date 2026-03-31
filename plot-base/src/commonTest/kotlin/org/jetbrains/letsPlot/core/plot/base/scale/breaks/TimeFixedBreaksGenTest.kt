/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.HOUR
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MINUTE
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimeFixedBreaksGenTest {

    @Test
    fun generatesBreaksAtFixedHourIntervals() {
        val gen = TimeFixedBreaksGen(breakWidth = HOUR)
        val domain = DoubleSpan(0.0, 5 * HOUR.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = -1)

        val expectedBreaks = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0).map { it * HOUR.totalMillis }
        val expectedLabels = listOf("0", "1:00", "2:00", "3:00", "4:00", "5:00")
        assertEquals(expectedBreaks, scaleBreaks.transformedValues)
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun generatesBreaksAt30MinIntervals() {
        val gen = TimeFixedBreaksGen(breakWidth = Duration(30 * MINUTE.totalMillis))
        val domain = DoubleSpan(0.0, 2 * HOUR.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = -1)

        val expectedBreaks = listOf(0.0, 0.5, 1.0, 1.5, 2.0).map { it * HOUR.totalMillis }
        val expectedLabels = listOf("0", "0:30", "1:00", "1:30", "2:00")
        assertEquals(expectedBreaks, scaleBreaks.transformedValues)
        assertEquals(expectedLabels, scaleBreaks.labels)
    }

    @Test
    fun fixedBreakWidthIsTrue() {
        val gen = TimeFixedBreaksGen(breakWidth = HOUR)

        assertTrue(gen.fixedBreakWidth)
    }

    @Test
    fun labelsMatchBreaksCount() {
        val gen = TimeFixedBreaksGen(breakWidth = HOUR)
        val domain = DoubleSpan(0.0, 3 * HOUR.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = -1)

        assertEquals(scaleBreaks.domainValues.size, scaleBreaks.labels.size)
    }

    @Test
    fun usesProvidedFormatter() {
        val customFormatter: (Any) -> String = { "fixed" }
        val gen = TimeFixedBreaksGen(breakWidth = HOUR, providedFormatter = customFormatter)
        val domain = DoubleSpan(0.0, 3 * HOUR.totalMillis.toDouble())

        val scaleBreaks = gen.generateBreaks(domain, targetCount = -1)

        assertTrue(scaleBreaks.labels.all { it == "fixed" })
    }

    @Test
    fun startsFromCeilingOfDomain() {
        val gen = TimeFixedBreaksGen(breakWidth = HOUR)
        val domain = DoubleSpan(0.5 * HOUR.totalMillis, 3.5 * HOUR.totalMillis)

        val scaleBreaks = gen.generateBreaks(domain, targetCount = -1)

        val expectedBreaks = listOf(1.0, 2.0, 3.0).map { it * HOUR.totalMillis }
        val expectedLabels = listOf("1:00", "2:00", "3:00")
        assertEquals(expectedBreaks, scaleBreaks.transformedValues)
        assertEquals(expectedLabels, scaleBreaks.labels)
    }
}
