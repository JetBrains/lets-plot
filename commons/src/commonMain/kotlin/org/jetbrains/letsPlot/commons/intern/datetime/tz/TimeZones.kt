/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.*
import kotlin.math.abs

internal object TimeZones {
    private const val MILLIS_IN_SECOND: Long = 1000
    private const val MILLIS_IN_MINUTE = MILLIS_IN_SECOND * 60
    private const val MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60
    private const val MILLIS_IN_DAY = MILLIS_IN_HOUR * 24

    private fun toDateTime(instant: Instant, offset: Duration): DateTime {
        @Suppress("NAME_SHADOWING")
        val instant = instant.add(offset)

        val dayDelta = (instant.timeSinceEpoch / MILLIS_IN_DAY).toInt()
        var rest = instant.timeSinceEpoch % MILLIS_IN_DAY
        val hourDelta = (rest / MILLIS_IN_HOUR).toInt()
        rest %= MILLIS_IN_HOUR
        val minuteDelta = (rest / MILLIS_IN_MINUTE).toInt()
        rest %= MILLIS_IN_MINUTE
        val secondDelta = (rest / MILLIS_IN_SECOND).toInt()
        rest %= MILLIS_IN_SECOND
        val millisDelta = (rest % MILLIS_IN_SECOND).toInt()

        return if (instant.timeSinceEpoch >= 0) {
            val date = Date.EPOCH.addDays(dayDelta)
            DateTime(date, Time(hourDelta, minuteDelta, secondDelta, millisDelta))
        } else {
            fun subtract(minuend: Int, subtrahend: Int): Pair<Int, Int> {
                // returns pair - difference and borrowing flag
                return if (subtrahend == 0) { 0 to 0 } else { minuend - subtrahend to 1 }
            }

            val (milliseconds, secondBorrowing) = subtract(1_000, abs(millisDelta))
            val (seconds, minuteBorrowing) = subtract(60, abs(secondDelta) + secondBorrowing)
            val (minutes, hourBorrowing) = subtract(60, abs(minuteDelta) + minuteBorrowing)
            val (hours, dayBorrowing) = subtract(24, abs(hourDelta) + hourBorrowing)
            val days = abs(dayDelta) + dayBorrowing

            val time = Time(hours, minutes, seconds, milliseconds)
            val date = Date.EPOCH.subtractDays(days)
            DateTime(date, time)
        }
    }

    private fun toInstant(dateTime: DateTime, offset: Duration): Instant {
        return Instant(toMillis(dateTime.date) + toMillis(dateTime.time)).sub(offset)
    }

    private fun toMillis(time: Time): Long {
        val minutes = time.hours * 60L + time.minutes
        val seconds = minutes * 60 + time.seconds
        return seconds * 1000 + time.milliseconds
    }

    private fun toMillis(date: Date): Long {
        return date.daysFrom(Date.EPOCH) * MILLIS_IN_DAY
    }

    fun utc(): TimeZone {
        return object : TimeZone("UTC") {
            override fun toDateTime(instant: Instant): DateTime {
                return toDateTime(instant, Duration(0))
            }

            override fun toInstant(dateTime: DateTime): Instant {
                return toInstant(dateTime, Duration(0))
            }
        }
    }

    fun offset(id: String?, offset: Duration, base: TimeZone): TimeZone {
        return object : TimeZone(id) {
            override fun toDateTime(instant: Instant): DateTime {
                return base.toDateTime(instant.add(offset))
            }

            override fun toInstant(dateTime: DateTime): Instant {
                return base.toInstant(dateTime).sub(offset)
            }

        }
    }

    fun withEuSummerTime(id: String, offset: Duration): TimeZone {
        val startSpec = DateSpecs.last(WeekDay.SUNDAY, Month.MARCH)
        val endSpec = DateSpecs.last(WeekDay.SUNDAY, Month.OCTOBER)
        val utcChangeTime = Time(1, 0)
        return object : DSTimeZone(id, offset) {
            override fun getStartInstant(year: Int): Instant {
                return UTC.toInstant(DateTime(startSpec.getDate(year), utcChangeTime))
            }

            override fun getEndInstant(year: Int): Instant {
                return UTC.toInstant(DateTime(endSpec.getDate(year), utcChangeTime))
            }

        }
    }

    fun withUsSummerTime(id: String, offset: Duration): TimeZone {
        val startSpec = DateSpecs.first(WeekDay.SUNDAY, Month.MARCH, 2)
        val endSpec = DateSpecs.first(WeekDay.SUNDAY, Month.NOVEMBER)

        return object : DSTimeZone(id, offset) {
            override fun getStartInstant(year: Int): Instant {
                return UTC.toInstant(DateTime(startSpec.getDate(year), Time(2, 0))).sub(offset)
            }

            override fun getEndInstant(year: Int): Instant {
                return UTC.toInstant(DateTime(endSpec.getDate(year), Time(2, 0))).sub(offset.add(Duration.HOUR))
            }

        }
    }

    private abstract class DSTimeZone(id: String, offset: Duration) : TimeZone(id) {
        private val myTz: TimeZone = offset(null, offset, UTC)
        private val mySummerTz: TimeZone = offset(null, offset.add(Duration.HOUR), UTC)

        override fun toDateTime(instant: Instant): DateTime {
            val tzDt = myTz.toDateTime(instant)
            val start = getStartInstant(tzDt.year)
            val end = getEndInstant(tzDt.year)
            return if (instant > start && instant < end) {
                mySummerTz.toDateTime(instant)
            } else {
                tzDt
            }
        }

        override fun toInstant(dateTime: DateTime): Instant {
            val startDt = toDateTime(getStartInstant(dateTime.year))
            val endDt = toDateTime(getEndInstant(dateTime.year))

            return if (dateTime > startDt && dateTime < endDt) {
                mySummerTz.toInstant(dateTime)
            } else {
                myTz.toInstant(dateTime)
            }
        }

        protected abstract fun getStartInstant(year: Int): Instant

        protected abstract fun getEndInstant(year: Int): Instant
    }
}
