/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime


object DateTimeUtil {
    internal val DAYS_IN_YEAR: Int
    internal val DAYS_IN_LEAP_YEAR: Int

    init {
        var leapYearDays = 0
        var yearDays = 0
        for (m in Month.values()) {
            leapYearDays += m.getDaysInLeapYear()
            yearDays += m.days
        }
        DAYS_IN_YEAR = yearDays
        DAYS_IN_LEAP_YEAR = leapYearDays
    }

    internal fun isLeap(year: Int): Boolean {
        return leapYearsFromZero(year) - leapYearsFromZero(year - 1) == 1
    }

    internal fun leapYearsBetween(fromYear: Int, toYear: Int): Int {
        if (fromYear > toYear) {
            throw IllegalArgumentException()
        }

        return leapYearsFromZero(toYear - 1) - leapYearsFromZero(fromYear - 1)
    }

    private fun leapYearsFromZero(year: Int): Int {
        return year / 4 - year / 100 + year / 400
    }
}
