/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.*
import org.jetbrains.letsPlot.commons.intern.datetime.tz.TimeZones.offset
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeZonesTest {
    @Test
    fun toFromMillis() {
        assertConversion(Date(5, Month.SEPTEMBER, 1970))
        assertConversion(Date(1, Month.OCTOBER, 1971))
        assertConversion(Date(1, Month.FEBRUARY, 1972))
        assertConversion(Date(1, Month.FEBRUARY, 1973))
        assertConversion(Date(1, Month.DECEMBER, 1973))
        assertConversion(Date(10, Month.SEPTEMBER, 1991))
        assertConversion(Date(21, Month.MAY, 2010))
        assertConversion(Date(4, Month.APRIL, 2012))
    }

    @Test
    fun fromRealMillis() {
        assertEquals(Date(4, Month.APRIL, 2012), TimeZone.UTC.toDateTime(Instant(1333497600000L)).date)
    }

    @Test
    fun offsetFromUtc() {
        val instant = Instant(1335423695381L)
        assertEquals("20120426T110135", offset("", Duration.HOUR.mul(4), TimeZone.UTC).toDateTime(instant).toString())
    }

    @Test
    fun convertFromMoscowToBerlinSummer() {
        val dt = DateTime(Date(26, Month.APRIL, 2012), Time(21, 13))
        assertEquals(DateTime(Date(26, Month.APRIL, 2012), Time(19, 13)), TimeZone.MOSCOW.convertTo(dt, TimeZone.BERLIN))
    }

    @Test
    fun convertFromMoscowToBerlinWinter() {
        val dt = DateTime(Date(26, Month.FEBRUARY, 2012), Time(19, 40))
        assertEquals(
                DateTime(Date(26, Month.FEBRUARY, 2012), Time(16, 40)),
                TimeZone.MOSCOW.convertTo(dt, TimeZone.BERLIN)
        )
    }

    @Test
    fun convertFromMoscowToBostonSummer() {
        val dt = DateTime(Date(26, Month.APRIL, 2012), Time(20, 57))
        assertEquals(
                DateTime(Date(26, Month.APRIL, 2012), Time(12, 57)),
                TimeZone.MOSCOW.convertTo(dt, TimeZone.NY)
        )
    }

    @Test
    fun convertFromMoscowToBostonWinter() {
        val dt = DateTime(Date(26, Month.JANUARY, 2012), Time(20, 57))
        assertEquals(
                DateTime(Date(26, Month.JANUARY, 2012), Time(11, 57)),
                TimeZone.MOSCOW.convertTo(dt, TimeZone.NY)
        )
    }

    @Test
    fun timeDifferences() {
        val dt = DateTime(Date(1, Month.MAY, 2012))
        val instant = TimeZone.MOSCOW.toInstant(dt)

        assertEquals(4, TimeZone.MOSCOW.getTimeZoneShift(instant).div(Duration.HOUR).toInt())

    }

    @Test
    fun convertTimeAtDay() {
        val dsOffDate = Date(19, Month.SEPTEMBER, 2012)
        val dsOnDate = Date(6, Month.NOVEMBER, 2012)

        var t = TimeZone.BERLIN.convertTimeAtDay(Time(15, 0), dsOnDate, TimeZone.MOSCOW)
        assertEquals(Time(18, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(21, 0), dsOnDate, TimeZone.MOSCOW)
        assertEquals(Time(0, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(22, 0), dsOnDate, TimeZone.MOSCOW)
        assertEquals(Time(1, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(15, 0), dsOffDate, TimeZone.MOSCOW)
        assertEquals(Time(17, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(22, 0), dsOffDate, TimeZone.MOSCOW)
        assertEquals(Time(0, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(23, 0), dsOffDate, TimeZone.MOSCOW)
        assertEquals(Time(1, 0), t)

        t = TimeZone.MOSCOW.convertTimeAtDay(Time(18, 0), dsOnDate, TimeZone.BERLIN)
        assertEquals(Time(15, 0), t)
        t = TimeZone.MOSCOW.convertTimeAtDay(Time(0, 0), dsOnDate, TimeZone.BERLIN)
        assertEquals(Time(21, 0), t)
        t = TimeZone.MOSCOW.convertTimeAtDay(Time(2, 0), dsOnDate, TimeZone.BERLIN)
        assertEquals(Time(23, 0), t)
        t = TimeZone.MOSCOW.convertTimeAtDay(Time(3, 0), dsOnDate, TimeZone.BERLIN)
        assertEquals(Time(0, 0), t)
        t = TimeZone.MOSCOW.convertTimeAtDay(Time(17, 0), dsOffDate, TimeZone.BERLIN)
        assertEquals(Time(15, 0), t)
        t = TimeZone.MOSCOW.convertTimeAtDay(Time(0, 0), dsOffDate, TimeZone.BERLIN)
        assertEquals(Time(22, 0), t)
        t = TimeZone.MOSCOW.convertTimeAtDay(Time(2, 0), dsOffDate, TimeZone.BERLIN)
        assertEquals(Time(0, 0), t)
        t = TimeZone.MOSCOW.convertTimeAtDay(Time(3, 0), dsOffDate, TimeZone.BERLIN)
        assertEquals(Time(1, 0), t)
    }

    @Test
    fun convertTimeAtDSChangeDay() {
        val dsChangeDate = Date(28, Month.OCTOBER, 2012)
        var t = TimeZone.BERLIN.convertTimeAtDay(Time(15, 0), dsChangeDate, TimeZone.MOSCOW)
        assertEquals(Time(18, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(15, 0), dsChangeDate.prevDate(), TimeZone.MOSCOW)
        assertEquals(Time(17, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(15, 0), dsChangeDate.nextDate(), TimeZone.MOSCOW)
        assertEquals(Time(18, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(1, 59), dsChangeDate, TimeZone.MOSCOW)
        assertEquals(Time(3, 59), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(2, 0), dsChangeDate, TimeZone.MOSCOW)
        assertEquals(Time(5, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(22, 0), dsChangeDate, TimeZone.MOSCOW)
        assertEquals(Time(0, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(23, 0), dsChangeDate.nextDate(), TimeZone.MOSCOW)
        assertEquals(Time(2, 0), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(1, 59), dsChangeDate.nextDate(), TimeZone.MOSCOW)
        assertEquals(Time(4, 59), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(2, 0), dsChangeDate.prevDate(), TimeZone.MOSCOW)
        assertEquals(Time(4, 0), t)

        t = TimeZone.BERLIN.convertTimeAtDay(Time(1, 59), dsChangeDate, TimeZone.NY)
        assertEquals(Time(20, 59), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(2, 0), dsChangeDate, TimeZone.NY)
        assertEquals(Time(21, 0), t)

        t = TimeZone.BERLIN.convertTimeAtDay(Time(1, 59), dsChangeDate.prevDate(), TimeZone.NY)
        assertEquals(Time(19, 59), t)
        t = TimeZone.BERLIN.convertTimeAtDay(Time(2, 0), dsChangeDate.prevDate(), TimeZone.NY)
        assertEquals(Time(21, 0), t)

    }

    private fun assertConversion(date: Date) {
        val instant = TimeZone.UTC.toInstant(DateTime(date))
        assertEquals(date, TimeZone.UTC.toDateTime(instant).date)
    }
}
