/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.intern.datetime.*
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.commons.time.interval.TimeInterval
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DateTimeFixedBreaksGenTest {

    @Test
    fun twoWeeksInterval() {
        val gen = DateTimeFixedBreaksGen(
            breakWidth = TimeInterval.weeks(2),
            minInterval = null,
            maxInterval = null,
            tz = TZ_UTC
        )

        // 3-month range
        val start = DateTime(Date(1, Month.JANUARY, 2024), Time.DAY_START)
        val end = DateTime(Date(1, Month.APRIL, 2024), Time.DAY_START)

        val domain = DoubleSpan(
            start.toEpochMilliseconds(TZ_UTC).toDouble(),
            end.toEpochMilliseconds(TZ_UTC).toDouble()
        )

        val breaks = gen.generateBreaks(domain, targetCount = 10)

        // Should have breaks every 2 weeks
        assertTrue(breaks.domainValues.isNotEmpty())
        assertTrue(breaks.fixed, "Breaks should be marked as fixed")

        // Check that breaks are approximately 2 weeks apart
        val breakValues = breaks.domainValues.map { it as Double }
        for (i in 1 until breakValues.size) {
            val diff = breakValues[i] - breakValues[i - 1]
            // 2 weeks = 14 days = 14 * 24 * 60 * 60 * 1000 ms
            val twoWeeksMs = 14.0 * 24 * 60 * 60 * 1000
            assertEquals(twoWeeksMs, diff, 1000.0, "Breaks should be 2 weeks apart")
        }
    }

    @Test
    fun threeMonthsInterval() {
        val gen = DateTimeFixedBreaksGen(
            breakWidth = TimeInterval.months(3),
            minInterval = null,
            maxInterval = null,
            tz = TZ_UTC
        )

        // 1 year range
        val start = DateTime(Date(1, Month.JANUARY, 2024), Time.DAY_START)
        val end = DateTime(Date(1, Month.JANUARY, 2025), Time.DAY_START)

        val domain = DoubleSpan(
            start.toEpochMilliseconds(TZ_UTC).toDouble(),
            end.toEpochMilliseconds(TZ_UTC).toDouble()
        )

        val breaks = gen.generateBreaks(domain, targetCount = 10)

        assertTrue(breaks.domainValues.isNotEmpty())
        assertTrue(breaks.fixed, "Breaks should be marked as fixed")

        // Should have 4 breaks (Jan, Apr, Jul, Oct) or 5 if end is included
        val breakValues = breaks.domainValues.map { it as Double }
        assertTrue(breakValues.size in 4..5, "Expected 4-5 breaks for quarterly intervals over a year")

        // Verify each break is at the start of a quarter
        for (breakValue in breakValues) {
            val dt = DateTime.ofEpochMilliseconds(breakValue, TZ_UTC)
            assertTrue(
                dt.month in listOf(Month.JANUARY, Month.APRIL, Month.JULY, Month.OCTOBER),
                "Break should be at start of quarter, got ${dt.month}"
            )
            assertEquals(1, dt.day, "Break should be on 1st day of month")
        }
    }

    @Test
    fun sixHoursInterval() {
        val gen = DateTimeFixedBreaksGen(
            breakWidth = TimeInterval.hours(6),
            minInterval = null,
            maxInterval = null,
            tz = TZ_UTC
        )

        // 2 days range
        val start = DateTime(Date(1, Month.JANUARY, 2024), Time.DAY_START)
        val end = DateTime(Date(3, Month.JANUARY, 2024), Time.DAY_START)

        val domain = DoubleSpan(
            start.toEpochMilliseconds(TZ_UTC).toDouble(),
            end.toEpochMilliseconds(TZ_UTC).toDouble()
        )

        val breaks = gen.generateBreaks(domain, targetCount = 10)

        assertTrue(breaks.domainValues.isNotEmpty())
        assertTrue(breaks.fixed, "Breaks should be marked as fixed")

        // Check that breaks are 6 hours apart
        val breakValues = breaks.domainValues.map { it as Double }
        for (i in 1 until breakValues.size) {
            val diff = breakValues[i] - breakValues[i - 1]
            // 6 hours = 6 * 60 * 60 * 1000 ms
            val sixHoursMs = 6.0 * 60 * 60 * 1000
            assertEquals(sixHoursMs, diff, 1000.0, "Breaks should be 6 hours apart")
        }

        // Check hours are at 0, 6, 12, or 18
        for (breakValue in breakValues) {
            val dt = DateTime.ofEpochMilliseconds(breakValue, TZ_UTC)
            assertTrue(
                dt.hours in listOf(0, 6, 12, 18),
                "Break should be at 0, 6, 12, or 18 hours, got ${dt.hours}"
            )
        }
    }

    @Test
    fun oneYearInterval() {
        val gen = DateTimeFixedBreaksGen(
            breakWidth = TimeInterval.years(1),
            minInterval = null,
            maxInterval = null,
            tz = TZ_UTC
        )

        // 5 years range
        val start = DateTime(Date(1, Month.MARCH, 2020), Time.DAY_START)
        val end = DateTime(Date(1, Month.MARCH, 2025), Time.DAY_START)

        val domain = DoubleSpan(
            start.toEpochMilliseconds(TZ_UTC).toDouble(),
            end.toEpochMilliseconds(TZ_UTC).toDouble()
        )

        val breaks = gen.generateBreaks(domain, targetCount = 10)

        assertTrue(breaks.domainValues.isNotEmpty())
        assertTrue(breaks.fixed, "Breaks should be marked as fixed")

        // Should have 5 or 6 breaks (2021, 2022, 2023, 2024, 2025)
        val breakValues = breaks.domainValues.map { it as Double }
        assertTrue(breakValues.size in 4..6, "Expected 4-6 breaks for yearly intervals over 5 years")

        // Verify each break is at Jan 1st
        for (breakValue in breakValues) {
            val dt = DateTime.ofEpochMilliseconds(breakValue, TZ_UTC)
            assertEquals(Month.JANUARY, dt.month, "Break should be in January")
            assertEquals(1, dt.day, "Break should be on 1st day")
        }
    }

    @Test
    fun breaksHaveLabels() {
        val gen = DateTimeFixedBreaksGen(
            breakWidth = TimeInterval.weeks(1),
            minInterval = null,
            maxInterval = null,
            tz = TZ_UTC
        )

        val start = DateTime(Date(1, Month.JANUARY, 2024), Time.DAY_START)
        val end = DateTime(Date(1, Month.FEBRUARY, 2024), Time.DAY_START)

        val domain = DoubleSpan(
            start.toEpochMilliseconds(TZ_UTC).toDouble(),
            end.toEpochMilliseconds(TZ_UTC).toDouble()
        )

        val breaks = gen.generateBreaks(domain, targetCount = 10)

        // Each break should have a corresponding label
        assertEquals(breaks.domainValues.size, breaks.labels.size, "Labels count should match breaks count")

        // Labels should not be empty
        for (label in breaks.labels) {
            assertTrue(label.isNotBlank(), "Label should not be blank")
        }
    }

    @Test
    fun customFormatterIsUsed() {
        val customFormatter: (Any) -> String = { "Custom: ${it}" }
        val gen = DateTimeFixedBreaksGen(
            breakWidth = TimeInterval.days(1),
            providedFormatter = customFormatter,
            minInterval = null,
            maxInterval = null,
            tz = TZ_UTC
        )

        val start = DateTime(Date(1, Month.JANUARY, 2024), Time.DAY_START)
        val end = DateTime(Date(5, Month.JANUARY, 2024), Time.DAY_START)

        val domain = DoubleSpan(
            start.toEpochMilliseconds(TZ_UTC).toDouble(),
            end.toEpochMilliseconds(TZ_UTC).toDouble()
        )

        val breaks = gen.generateBreaks(domain, targetCount = 10)

        // Labels should use custom formatter
        for (label in breaks.labels) {
            assertTrue(label.startsWith("Custom:"), "Label should use custom formatter")
        }
    }

    companion object {
        private val TZ_UTC = TimeZone.UTC
    }
}
