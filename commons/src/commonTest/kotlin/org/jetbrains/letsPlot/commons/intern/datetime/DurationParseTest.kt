/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.DAY
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.HOUR
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MINUTE
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.MS
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.SECOND
import org.jetbrains.letsPlot.commons.intern.datetime.Duration.Companion.WEEK
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DurationParseTest {

    @Test
    fun validDurations() {
        val testCases: List<Pair<String, Duration>> = listOf(
            // Milliseconds
            "100 ms" to MS.mul(100),
            "5 millis" to MS.mul(5),
            "5 millisecond" to MS.mul(5),
            "5 milliseconds" to MS.mul(5),

            // Seconds
            "30 sec" to SECOND.mul(30),
            "1 second" to SECOND.mul(1),
            "15 seconds" to SECOND.mul(15),

            // Minutes
            "5 min" to MINUTE.mul(5),
            "1 minute" to MINUTE.mul(1),
            "30 minutes" to MINUTE.mul(30),

            // Hours
            "1 hour" to HOUR.mul(1),
            "12 hours" to HOUR.mul(12),

            // Days
            "1 day" to DAY.mul(1),
            "7 days" to DAY.mul(7),

            // Weeks
            "1 week" to WEEK.mul(1),
            "2 weeks" to WEEK.mul(2),

            // Extra whitespace
            "  2   weeks  " to WEEK.mul(2),

            // Case-insensitive
            "2 WEEKS" to WEEK.mul(2),
            "2 Weeks" to WEEK.mul(2),
        )

        for ((spec, expected) in testCases) {
            val actual = Duration.parse(spec)
            assertEquals(expected, actual, "Failed for spec: '$spec'")
        }
    }

    @Test
    fun invalidDurations() {
        val invalidSpecs = listOf(
            "42",           // missing unit
            "weeks",        // missing count
            "abc weeks",    // invalid count
            "0 weeks",      // zero count
            "-1 weeks",     // negative count
            "2 months",     // unsupported unit (Duration doesn't support months)
            "2 years",      // unsupported unit (Duration doesn't support years)
            "2 centuries",  // unknown unit
            "",             // empty string
            "   ",          // blank string
        )

        for (spec in invalidSpecs) {
            assertFailsWith<IllegalArgumentException>("Expected '$spec' to fail parsing") {
                Duration.parse(spec)
            }
        }
    }
}
