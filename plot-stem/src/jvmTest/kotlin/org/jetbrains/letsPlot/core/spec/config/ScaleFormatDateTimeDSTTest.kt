/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.config.ScaleFomateDateTimeTestUtil.checkScales
import kotlin.test.Test

/**
 * This test verifies datetime formatting around Berlin DST transition using equally spaced timestamps.
 * It focuses on the Berlin timezone transition on March 30, 2025 at 2:00 AM.
 */
class ScaleFormatDateTimeEquallySpacedDSTTest {

    @Test
    fun `datetime formatting around Berlin DST spring transition`() {
        // Berlin timezone has a DST transition on March 30, 2025 at 2:00 AM
        // The clock jumps from 2:00 AM to 3:00 AM (skipping one hour)

        // Start time: March 29, 2025, 18:00 in Berlin time
        val baseDateTime = DateTime(Date(29, Month.MARCH, 2025), Time(18, 0))
        val startTimeMs = baseDateTime.toEpochMilliseconds(TZ_BERLIN)

        // Create 16 equally spaced timestamps, one hour apart in milliseconds
        // This spans from 18:00 on March 29 to 10:00 on March 30 (with DST jump)
        val timestamps = List(16) { i ->
            (startTimeMs + i * HOUR_IN_MS).toDouble()
        }

        // Convert each timestamp back to DateTime to check the hours
        val formattedTimes = timestamps.map { epochMs ->
            val dt = DateTime.ofEpochMilliseconds(epochMs.toLong(), TZ_BERLIN)
            String.format("%02d:%02d", dt.hours, dt.minutes)
        }

        // Expected formatted labels - note there should be a jump from 01:00 to 03:00
        val expectedLabels = listOf(
            "18:00", "19:00", "20:00", "21:00", "22:00", "23:00",
            "00:00", "01:00", "03:00", "04:00", "05:00", "06:00",
            "07:00", "08:00", "09:00", "10:00"
        )

        // Verify our calculated times match the expected pattern (with DST jump)
        assert(formattedTimes == expectedLabels) {
            "Expected: $expectedLabels\nActual: $formattedTimes"
        }

        checkScales(
            timestamps,
            expectedLabels,
            expectedLabels,
            DATE_TIME_ANNOTATION_PART
        )
    }
    
    @Test
    fun `datetime formatting around Berlin DST fall transition`() {
        // Berlin timezone has a DST fall transition on October 26, 2025 at 3:00 AM
        // The clock goes back from 3:00 AM to 2:00 AM (repeating one hour)

        // Start time: October 25, 2025, 18:00 in Berlin time
        val baseDateTime = DateTime(Date(25, Month.OCTOBER, 2025), Time(18, 0))
        val startTimeMs = baseDateTime.toEpochMilliseconds(TZ_BERLIN)

        // Create 16 equally spaced timestamps, one hour apart in milliseconds
        // This spans from 18:00 on October 25 to 8:00 on October 26
        val timestamps = List(16) { i ->
            (startTimeMs + i * HOUR_IN_MS).toDouble()
        }

        // Convert each timestamp back to DateTime to check the hours
        val formattedTimes = timestamps.map { epochMs ->
            val dt = DateTime.ofEpochMilliseconds(epochMs.toLong(), TZ_BERLIN)
            String.format("%02d:%02d", dt.hours, dt.minutes)
        }

        // Expected formatted labels - note the duplicate 02:00 hour due to the "fall back" (end of DST)
        val expectedLabels = listOf(
            "18:00", "19:00", "20:00", "21:00", "22:00", "23:00",
            "00:00", "01:00", "02:00", "02:00", "03:00", "04:00",
            "05:00", "06:00", "07:00", "08:00"
        )

        // Verify our calculated times match the expected pattern (with repeated hour)
        assert(formattedTimes == expectedLabels) {
            "Expected: $expectedLabels\nActual: $formattedTimes"
        }

        checkScales(
            timestamps,
            expectedLabels,
            expectedLabels,
            DATE_TIME_ANNOTATION_PART
        )
    }

    companion object {
        // Berlin timezone (Europe/Berlin)
        private val TZ_BERLIN = TimeZone("Europe/Berlin")
        private const val HOUR_IN_MS = 60 * 60 * 1000L

        private val DATE_TIME_ANNOTATION_PART = mapOf(
            Option.Meta.SeriesAnnotation.TYPE to Option.Meta.SeriesAnnotation.Types.DATE_TIME,
            Option.Meta.SeriesAnnotation.TIME_ZONE to TZ_BERLIN.id,
        )
    }
}