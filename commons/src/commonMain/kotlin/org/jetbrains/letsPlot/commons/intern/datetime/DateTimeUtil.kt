/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.datetime


object DateTimeUtil {
    const val BASE_YEAR = 1900
    const val MAX_SUPPORTED_YEAR = 2100 //inclusive; defined by LEAP_YEARS_FROM_1969 length
    internal const val MIN_SUPPORTED_YEAR = 1970 //inclusive
    internal val DAYS_IN_YEAR: Int
    internal val DAYS_IN_LEAP_YEAR: Int

    internal val LEAP_YEARS_FROM_1969 = intArrayOf(477, 477, 477, 478, 478, 478, 478, 479, 479, 479, 479, 480, 480, 480, 480, 481, 481, 481, 481, 482, 482, 482, 482, 483, 483, 483, 483, 484, 484, 484, 484, 485, 485, 485, 485, 486, 486, 486, 486, 487, 487, 487, 487, 488, 488, 488, 488, 489, 489, 489, 489, 490, 490, 490, 490, 491, 491, 491, 491, 492, 492, 492, 492, 493, 493, 493, 493, 494, 494, 494, 494, 495, 495, 495, 495, 496, 496, 496, 496, 497, 497, 497, 497, 498, 498, 498, 498, 499, 499, 499, 499, 500, 500, 500, 500, 501, 501, 501, 501, 502, 502, 502, 502, 503, 503, 503, 503, 504, 504, 504, 504, 505, 505, 505, 505, 506, 506, 506, 506, 507, 507, 507, 507, 508, 508, 508, 508, 509, 509, 509, 509, 509)

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
        checkYear(year)
        return LEAP_YEARS_FROM_1969[year - MIN_SUPPORTED_YEAR + 1] - LEAP_YEARS_FROM_1969[year - MIN_SUPPORTED_YEAR] == 1
        //hack for quick load
        //    return leapYearsFromZero(year) - leapYearsFromZero(year - 1) == 1;
    }

    internal fun leapYearsBetween(fromYear: Int, toYear: Int): Int {
        if (fromYear > toYear) {
            throw IllegalArgumentException()
        }
        checkYear(fromYear)
        checkYear(toYear)

        return LEAP_YEARS_FROM_1969[toYear - MIN_SUPPORTED_YEAR] - LEAP_YEARS_FROM_1969[fromYear - MIN_SUPPORTED_YEAR]
        //    return leapYearsFromZero(toYear - 1) - leapYearsFromZero(fromYear - 1);
    }

    private fun leapYearsFromZero(year: Int): Int {
        return year / 4 - year / 100 + year / 400
    }

    private fun checkYear(year: Int) {
        if (year > MAX_SUPPORTED_YEAR || year < MIN_SUPPORTED_YEAR) {
            throw IllegalArgumentException(year.toString() + "")
        }
    }
}
