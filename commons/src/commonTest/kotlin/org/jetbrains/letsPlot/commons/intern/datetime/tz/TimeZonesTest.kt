/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime.tz

import org.jetbrains.letsPlot.commons.intern.datetime.*
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
        assertEquals(
            Date(4, Month.APRIL, 2012),
            Instant(1333497600000L).toDateTime(TimeZone.UTC).date
        )
    }

    @Test
    fun convertFromMoscowToBerlinSummer() {
        val mdt = DateTime(Date(26, Month.APRIL, 2012), Time(21, 13))
        val bdt = DateTime(Date(26, Month.APRIL, 2012), Time(19, 13))
        val bdtConv = mdt
            .toInstant(TZs.moscow)
            .toDateTime(TZs.berlin)
        assertEquals(
            bdt,
            bdtConv
        )
    }

    @Test
    fun convertFromMoscowToBerlinWinter() {
        val mdt = DateTime(Date(26, Month.FEBRUARY, 2012), Time(19, 40))
        val bdt = DateTime(Date(26, Month.FEBRUARY, 2012), Time(16, 40))
        val bdtConv = mdt
            .toInstant(TZs.moscow)
            .toDateTime(TZs.berlin)
        assertEquals(
            bdt,
            bdtConv
        )
    }

    @Test
    fun convertFromMoscowToBostonSummer() {
        val mdt = DateTime(Date(26, Month.APRIL, 2012), Time(20, 57))
        val bdt = DateTime(Date(26, Month.APRIL, 2012), Time(12, 57))
        val bdtConv = mdt
            .toInstant(TZs.moscow)
            .toDateTime(TZs.newYork)
        assertEquals(
            bdt,
            bdtConv
        )
    }

    @Test
    fun convertFromMoscowToBostonWinter() {
        val mdt = DateTime(Date(26, Month.JANUARY, 2012), Time(20, 57))
        val bdt = DateTime(Date(26, Month.JANUARY, 2012), Time(11, 57))
        val bdtConv = mdt
            .toInstant(TZs.moscow)
            .toDateTime(TZs.newYork)
        assertEquals(
            bdt,
            bdtConv
        )
    }

    @Test
    fun convertTimeAtDay() {
        val dsOffDate = Date(19, Month.SEPTEMBER, 2012)
        val dsOnDate = Date(6, Month.NOVEMBER, 2012)

        var t = convertTimeAtDay(Time(15, 0), dsOnDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(18, 0), t)
        t = convertTimeAtDay(Time(21, 0), dsOnDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(0, 0), t)
        t = convertTimeAtDay(Time(22, 0), dsOnDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(1, 0), t)
        t = convertTimeAtDay(Time(15, 0), dsOffDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(17, 0), t)
        t = convertTimeAtDay(Time(22, 0), dsOffDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(0, 0), t)
        t = convertTimeAtDay(Time(23, 0), dsOffDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(1, 0), t)

        t = convertTimeAtDay(Time(18, 0), dsOnDate, TZs.moscow, TZs.berlin)
        assertEquals(Time(15, 0), t)
        t = convertTimeAtDay(Time(0, 0), dsOnDate, TZs.moscow, TZs.berlin)
        assertEquals(Time(21, 0), t)
        t = convertTimeAtDay(Time(2, 0), dsOnDate, TZs.moscow, TZs.berlin)
        assertEquals(Time(23, 0), t)
        t = convertTimeAtDay(Time(3, 0), dsOnDate, TZs.moscow, TZs.berlin)
        assertEquals(Time(0, 0), t)
        t = convertTimeAtDay(Time(17, 0), dsOffDate, TZs.moscow, TZs.berlin)
        assertEquals(Time(15, 0), t)
        t = convertTimeAtDay(Time(0, 0), dsOffDate, TZs.moscow, TZs.berlin)
        assertEquals(Time(22, 0), t)
        t = convertTimeAtDay(Time(2, 0), dsOffDate, TZs.moscow, TZs.berlin)
        assertEquals(Time(0, 0), t)
        t = convertTimeAtDay(Time(3, 0), dsOffDate, TZs.moscow, TZs.berlin)
        assertEquals(Time(1, 0), t)
    }

    @Test
    fun convertTimeAtDSChangeDay() {
        // On October 28, 2012, at 02:00, Berlin was transitioning from CEST (UTC+2) to CET (UTC+1)
        val dsChangeDate = Date(28, Month.OCTOBER, 2012)

        var t = convertTimeAtDay(Time(15, 0), dsChangeDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(18, 0), t)
        t = convertTimeAtDay(Time(15, 0), dsChangeDate.prevDate(), TZs.berlin, TZs.moscow)
        assertEquals(Time(17, 0), t)
        t = convertTimeAtDay(Time(15, 0), dsChangeDate.nextDate(), TZs.berlin, TZs.moscow)
        assertEquals(Time(18, 0), t)
        t = convertTimeAtDay(Time(1, 59), dsChangeDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(3, 59), t)

        // This is tricky because 02:00 actually occurs twice on this day in Berlin
        // due to the time change.

//        t = TimeZone.BERLIN.convertTimeAtDay(Time(2, 0), dsChangeDate, TZs.moscow)
//        assertEquals(Time(5, 0), t)           <-- wrong

        // 1. Before the time change: 02:00 CEST (UTC+2), which is 04:00 MSK (UTC+4)
        t = convertTimeAtDay(Time(2, 0), dsChangeDate, TimeZone("UTC+2"), TZs.moscow)
        assertEquals(Time(4, 0), t)

        // 2. After the time change: 02:00 CET (UTC+1), which is 05:00 MSK (UTC+4)
        t = convertTimeAtDay(Time(2, 0), dsChangeDate, TimeZone("UTC+1"), TZs.moscow)
        assertEquals(Time(5, 0), t)

        // When unspecified time zone is used, the first occurrence of 02:00 is taken
        t = convertTimeAtDay(Time(2, 0), dsChangeDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(4, 0), t)


        t = convertTimeAtDay(Time(22, 0), dsChangeDate, TZs.berlin, TZs.moscow)
        assertEquals(Time(1, 0), t)
        t = convertTimeAtDay(Time(23, 0), dsChangeDate.nextDate(), TZs.berlin, TZs.moscow)
        assertEquals(Time(2, 0), t)

        t = convertTimeAtDay(Time(1, 59), dsChangeDate.nextDate(), TZs.berlin, TZs.moscow)
        assertEquals(Time(4, 59), t)
        t = convertTimeAtDay(Time(2, 0), dsChangeDate.prevDate(), TZs.berlin, TZs.moscow)
        assertEquals(Time(4, 0), t)

        t = convertTimeAtDay(Time(1, 59), dsChangeDate, TZs.berlin, TZs.newYork)
        assertEquals(Time(19, 59), t)
        t = convertTimeAtDay(Time(2, 0), dsChangeDate, TZs.berlin, TZs.newYork)
        assertEquals(Time(20, 0), t)
        t = convertTimeAtDay(Time(2, 0), dsChangeDate, TimeZone("UTC+2"), TZs.newYork)
        assertEquals(Time(20, 0), t)
        t = convertTimeAtDay(Time(2, 0), dsChangeDate, TimeZone("UTC+1"), TZs.newYork)
        assertEquals(Time(21, 0), t)

        t = convertTimeAtDay(Time(1, 59), dsChangeDate.prevDate(), TZs.berlin, TZs.newYork)
        assertEquals(Time(19, 59), t)
        t = convertTimeAtDay(Time(2, 0), dsChangeDate.prevDate(), TZs.berlin, TZs.newYork)
        assertEquals(Time(20, 0), t)

        t = convertTimeAtDay(Time(1, 59), dsChangeDate.nextDate(), TZs.berlin, TZs.newYork)
        assertEquals(Time(20, 59), t)
        t = convertTimeAtDay(Time(2, 0), dsChangeDate.nextDate(), TZs.berlin, TZs.newYork)
        assertEquals(Time(21, 0), t)
    }

    private fun assertConversion(date: Date) {
        val instant = DateTime(date).toInstant(TZs.paris)
        assertEquals(date, instant.toDateTime(TZs.paris).date)
    }

    fun convertTimeAtDay(
        time: Time,
        date: Date,
        timeZone: TimeZone,
        dstTimeZone: TimeZone
    ): Time {
        var dateTime = DateTime(date, time)
        var dateTimeConv = dateTime
            .toInstant(timeZone)
            .toDateTime(dstTimeZone)
        return dateTimeConv.time
    }
}
