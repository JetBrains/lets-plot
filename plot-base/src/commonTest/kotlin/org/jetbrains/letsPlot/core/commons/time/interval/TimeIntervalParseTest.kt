/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TimeIntervalParseTest {

    @Test
    fun validIntervals() {
        val testCases: List<Pair<String, TimeInterval>> = listOf(
            // Milliseconds
            "100 ms" to TimeInterval.milliseconds(100),
            "5 millisecond" to TimeInterval.milliseconds(5),
            "5 milliseconds" to TimeInterval.milliseconds(5),

            // Seconds
            "30 sec" to TimeInterval.seconds(30),
            "1 second" to TimeInterval.seconds(1),
            "15 seconds" to TimeInterval.seconds(15),

            // Minutes
            "5 min" to TimeInterval.minutes(5),
            "1 minute" to TimeInterval.minutes(1),
            "30 minutes" to TimeInterval.minutes(30),

            // Hours
            "1 hour" to TimeInterval.hours(1),
            "12 hours" to TimeInterval.hours(12),

            // Days
            "1 day" to TimeInterval.days(1),
            "7 days" to TimeInterval.days(7),

            // Weeks
            "1 week" to TimeInterval.weeks(1),
            "2 weeks" to TimeInterval.weeks(2),

            // Months
            "1 month" to TimeInterval.months(1),
            "3 months" to TimeInterval.months(3),

            // Years
            "1 year" to TimeInterval.years(1),
            "5 years" to TimeInterval.years(5),

            // Extra whitespace
            "  2   weeks  " to TimeInterval.weeks(2),

            // Case-insensitive
            "2 WEEKS" to TimeInterval.weeks(2),
            "2 Weeks" to TimeInterval.weeks(2),
        )

        for ((spec, expected) in testCases) {
            val actual = TimeInterval.parse(spec)
            assertEquals(expected, actual, "Failed for spec: '$spec'")
        }
    }

    @Test
    fun invalidIntervals() {
        val invalidSpecs = listOf(
            "42",           // missing unit
            "weeks",        // missing count
            "abc weeks",    // invalid count
            "0 weeks",      // zero count
            "-1 weeks",     // negative count
            "2 centuries",  // unknown unit
            "",             // empty string
            "   ",          // blank string
        )

        for (spec in invalidSpecs) {
            assertFailsWith<IllegalArgumentException>("Expected '$spec' to fail parsing") {
                TimeInterval.parse(spec)
            }
        }
    }
}
