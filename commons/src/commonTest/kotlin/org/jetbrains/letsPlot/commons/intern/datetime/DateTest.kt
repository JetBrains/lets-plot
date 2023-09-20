/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.Month.Companion.DECEMBER
import org.jetbrains.letsPlot.commons.intern.datetime.Month.Companion.FEBRUARY
import org.jetbrains.letsPlot.commons.intern.datetime.Month.Companion.JANUARY
import org.jetbrains.letsPlot.commons.intern.datetime.Month.Companion.MARCH
import org.jetbrains.letsPlot.commons.intern.datetime.Month.Companion.NOVEMBER
import org.jetbrains.letsPlot.commons.intern.datetime.Month.Companion.SEPTEMBER
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DateTest {
    @Test
    fun dateEquality() {
        assertEquals(Date(22, SEPTEMBER, 1984), Date(22, SEPTEMBER, 1984))
        assertNotEquals(Date(22, SEPTEMBER, 1984), Date(22, SEPTEMBER, 1983))
    }

    @Test
    fun dateComparison() {
        assertTrue(Date(22, SEPTEMBER, 1984).compareTo(Date(22, SEPTEMBER, 1983)) > 0)
        assertTrue(Date(20, SEPTEMBER, 1982).compareTo(Date(22, SEPTEMBER, 1983)) < 0)
    }

    @Test
    fun daysFromYearStart() {
        assertEquals(3, Date(3, JANUARY, 1991).daysFromYearStart())
        assertEquals(365, Date(31, DECEMBER, 1991).daysFromYearStart())
    }

    @Test
    fun daysDifference() {
        assertEquals(0, Date(1, DECEMBER, 1991).daysFrom(Date(1, DECEMBER, 1991)))
        assertEquals(5, Date(6, DECEMBER, 1991).daysFrom(Date(1, DECEMBER, 1991)))
        assertEquals(1, Date(1, JANUARY, 1992).daysFrom(Date(31, DECEMBER, 1991)))
        assertEquals(1, Date(2, JANUARY, 1992).daysFrom(Date(1, JANUARY, 1992)))
        assertEquals(31, Date(1, FEBRUARY, 1973).daysFrom(Date(1, JANUARY, 1973)))
        assertEquals(31, Date(1, JANUARY, 1974).daysFrom(Date(1, DECEMBER, 1973)))
        assertEquals(28, Date(1, MARCH, 1973).daysFrom(Date(1, FEBRUARY, 1973)))
        assertEquals(1, Date(1, JANUARY, 1973).daysFrom(Date(31, DECEMBER, 1972)))
    }


    @Test
    fun weekDay() {
        assertEquals(WeekDay.SUNDAY, Date(1, FEBRUARY, 1970).weekDay)
        assertEquals(WeekDay.WEDNESDAY, Date(1, MARCH, 1972).weekDay)
        assertEquals(WeekDay.SUNDAY, Date(31, DECEMBER, 1972).weekDay)
        assertEquals(WeekDay.MONDAY, Date(1, JANUARY, 1973).weekDay)
        assertEquals(WeekDay.THURSDAY, Date(1, FEBRUARY, 1973).weekDay)
        assertEquals(WeekDay.THURSDAY, Date(1, MARCH, 1973).weekDay)
        assertEquals(WeekDay.FRIDAY, Date(1, MARCH, 1974).weekDay)
        assertEquals(WeekDay.WEDNESDAY, Date(25, Month.APRIL, 2012).weekDay)
    }

    @Test
    fun addDays() {
        assertEquals(Date(1, JANUARY, 1991), Date(30, DECEMBER, 1990).addDays(2))
        assertEquals(Date(5, JANUARY, 1991), Date(5, JANUARY, 1990).addDays(365))
        assertEquals(Date(5, SEPTEMBER, 2004), Date(5, SEPTEMBER, 2003).addDays(366))
        assertEquals(Date(1, JANUARY, 2101), Date(31, DECEMBER, 2100).addDays(1))
    }

    @Test
    fun parsing() {
        assertParsed(Date(1, SEPTEMBER, 1973))
        assertParsed(Date(12, Month.APRIL, 1993))
    }

    @Test
    fun subtractDays() {
        assertEquals(Date(30, DECEMBER, 1990), Date(31, DECEMBER, 1990).subtractDays(1))
        assertEquals(Date(1, DECEMBER, 1990), Date(31, DECEMBER, 1990).subtractDays(30))
        assertEquals(Date(30, NOVEMBER, 1990), Date(2, DECEMBER, 1990).subtractDays(2))
        assertEquals(Date(30, DECEMBER, 1990), Date(1, JANUARY, 1991).subtractDays(2))
        assertEquals(Date(5, JANUARY, 1990), Date(5, JANUARY, 1991).subtractDays(365))
        assertEquals(Date(5, SEPTEMBER, 2003), Date(5, SEPTEMBER, 2004).subtractDays(366))
        assertEquals(Date(31, DECEMBER, 1969), Date(1, JANUARY, 1970).subtractDays(1))
    }

    private fun assertParsed(date: Date) {
        assertEquals(date, Date.parse(date.toString()))
    }
}
