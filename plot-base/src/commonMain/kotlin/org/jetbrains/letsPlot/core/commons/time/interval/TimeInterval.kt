/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

interface TimeInterval {

    val tickFormatPattern: String

    /**
     * @param start instant
     * @param end   instant
     * @return Returns every time interval after or equal to start and before end.
     */
    fun range(start: Double, end: Double, tz: TimeZone?): List<Double>

    companion object {
        fun milliseconds(count: Int): TimeInterval {
            return DurationInterval(Duration.MS, count)
        }

        fun seconds(count: Int): TimeInterval {
            return DurationInterval(Duration.SECOND, count)
        }

        fun minutes(count: Int): TimeInterval {
            return DurationInterval(Duration.MINUTE, count)
        }

        fun hours(count: Int): TimeInterval {
            return DurationInterval(Duration.HOUR, count)
        }

        fun days(count: Int): TimeInterval {
            return DayInterval(count)
        }

        fun weeks(count: Int): TimeInterval {
            return WeekInterval(count)
        }

        fun months(count: Int): TimeInterval {
            return MonthInterval(count)
        }

        fun years(count: Int): TimeInterval {
            return YearInterval(count)
        }

        /**
         * Parses a time interval specification string.
         *
         * @param spec A string in format "<count> <unit>", e.g., "2 weeks", "3 months", "12 hours".
         *             Supported units: ms/millisecond(s), sec/second(s), min/minute(s),
         *             hour(s), day(s), week(s), month(s), year(s).
         * @return A TimeInterval representing the specified duration.
         * @throws IllegalArgumentException if the format is invalid or the unit is unknown.
         */
        fun parse(spec: String): TimeInterval {
            val trimmed = spec.trim()
            val parts = trimmed.split(Regex("\\s+"), limit = 2)

            if (parts.size != 2) {
                throw IllegalArgumentException(
                    "Invalid time interval format: '$spec'. Expected format: '<count> <unit>' (e.g., '2 weeks', '3 months')."
                )
            }

            val count = parts[0].toIntOrNull()
                ?: throw IllegalArgumentException(
                    "Invalid count in time interval: '${parts[0]}'. Expected an integer."
                )

            if (count <= 0) {
                throw IllegalArgumentException(
                    "Count must be positive: $count."
                )
            }

            return when (val unit = parts[1].lowercase()) {
                "ms", "millisecond", "milliseconds" -> milliseconds(count)
                "sec", "second", "seconds" -> seconds(count)
                "min", "minute", "minutes" -> minutes(count)
                "hour", "hours" -> hours(count)
                "day", "days" -> days(count)
                "week", "weeks" -> weeks(count)
                "month", "months" -> months(count)
                "year", "years" -> years(count)
                else -> throw IllegalArgumentException(
                    "Unknown time unit: '$unit'. Supported units: ms/millisecond(s), sec/second(s), " +
                            "min/minute(s), hour(s), day(s), week(s), month(s), year(s)."
                )
            }
        }
    }
}
