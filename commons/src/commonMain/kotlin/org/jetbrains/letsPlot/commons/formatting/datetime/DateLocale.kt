/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.datetime

import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.WeekDay

object DateLocale {
    val weekDayAbbr = mapOf(
        WeekDay.MONDAY to "Mon",
        WeekDay.TUESDAY to "Tue",
        WeekDay.WEDNESDAY to "Wed",
        WeekDay.THURSDAY to "Thu",
        WeekDay.FRIDAY to "Fri",
        WeekDay.SATURDAY to "Sat",
        WeekDay.SUNDAY to "Sun"
    )

    val weekDayFull = mapOf(
        WeekDay.MONDAY to "Monday",
        WeekDay.TUESDAY to "Tuesday",
        WeekDay.WEDNESDAY to "Wednesday",
        WeekDay.THURSDAY to "Thursday",
        WeekDay.FRIDAY to "Friday",
        WeekDay.SATURDAY to "Saturday",
        WeekDay.SUNDAY to "Sunday"
    )

    val monthAbbr = mapOf(
        Month.JANUARY to "Jan",
        Month.FEBRUARY to "Feb",
        Month.MARCH to "Mar",
        Month.APRIL to "Apr",
        Month.MAY to "May",
        Month.JUNE to "Jun",
        Month.JULY to "Jul",
        Month.AUGUST to "Aug",
        Month.SEPTEMBER to "Sep",
        Month.OCTOBER to "Oct",
        Month.NOVEMBER to "Nov",
        Month.DECEMBER to "Dec"
    )

    val monthFull = mapOf(
        Month.JANUARY to "January",
        Month.FEBRUARY to "February",
        Month.MARCH to "March",
        Month.APRIL to "April",
        Month.MAY to "May",
        Month.JUNE to "June",
        Month.JULY to "July",
        Month.AUGUST to "August",
        Month.SEPTEMBER to "September",
        Month.OCTOBER to "October",
        Month.NOVEMBER to "November",
        Month.DECEMBER to "December"
    )
}