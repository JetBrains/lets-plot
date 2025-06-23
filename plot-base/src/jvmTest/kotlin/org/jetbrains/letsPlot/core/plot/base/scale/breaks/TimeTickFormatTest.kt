/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormatUtil
import org.jetbrains.letsPlot.commons.intern.datetime.*
import org.jetbrains.letsPlot.core.commons.time.interval.TimeInterval
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTickFormatTest {

    @Test
    fun intervalSecondsDefault() {
        val sec = format(BASE_DATE_TIME, TimeInterval.seconds(1).tickFormatPattern)
        assertEquals("17:27", sec)
    }

    @Test
    fun intervalMinutesDefault() {
        val min1 = format(BASE_DATE_TIME, TimeInterval.minutes(1).tickFormatPattern)
        assertEquals("07:17", min1)
        val min5 = format(BASE_DATE_TIME, TimeInterval.minutes(5).tickFormatPattern)
        assertEquals("07:17", min5)
    }

    @Test
    fun intervalHoursDefault() {
        val hour = format(BASE_DATE_TIME, TimeInterval.hours(1).tickFormatPattern)
        assertEquals("07:17", hour)
    }

    @Test
    fun intervalDaysDefault() {
        val day = format(BASE_DATE_TIME, TimeInterval.days(1).tickFormatPattern)
        assertEquals("Apr 1", day)
    }

    @Test
    fun intervalWeeksDefault() {
        val week = format(BASE_DATE_TIME, TimeInterval.weeks(1).tickFormatPattern)
        assertEquals("Apr 1", week)
    }

    @Test
    fun intervalMonthsDefault() {
        val month = format(BASE_DATE_TIME, TimeInterval.months(1).tickFormatPattern)
        assertEquals("Apr", month)
    }

    @Test
    fun intervalYearsDefault() {
        val year = format(BASE_DATE_TIME, TimeInterval.years(1).tickFormatPattern)
        assertEquals("2013", year)
    }

    companion object {
        private val TZ = TimeZone.UTC

        private val BASE_DATE = Date(1, Month.APRIL, 2013)
        private val BASE_TIME = Time(7, 17, 27, 77)             // 07:07:07.007
        private val BASE_DATE_TIME = DateTime(
            BASE_DATE,
            BASE_TIME
        )

        private val baseInstant = DateTime(
            BASE_DATE,
            BASE_TIME
        ).toInstant(TZ)

        private fun format(dateTime: DateTime, pattern: String): String {
            return DateTimeFormatUtil.format(dateTime, pattern)
        }
    }
}
