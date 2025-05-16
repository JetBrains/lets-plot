/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormatUtil.formatterDateUTC
import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month

class YearInterval internal constructor(count: Int) : MeasuredInDays(count) {

    override val tickFormatPattern = TICK_FORMAT

    override fun getFirstDayContaining(dateTime: DateTime): Date {
//        return Date.firstDayOf(instant.year)
        return firstDay(dateTime.year)
    }

    override fun addInterval(dateTime: DateTime): DateTime {
        var result = dateTime
        for (i in 0 until count) {
            result = addYear(result)
        }
        return result
    }

    private fun addYear(dateTime: DateTime): DateTime {
//        val year = dateTime.year
//        return DateTime(Date.firstDayOf(year + 1))

        // A bit weird ?
        return DateTime(
            firstDay(dateTime.year + 1)
        )
    }

    companion object {
        const val TICK_FORMAT = "%Y"
        const val MS = 31536e6
        val TICK_FORMATTER = formatterDateUTC(TICK_FORMAT)

        private fun firstDay(year: Int): Date {
            return Date(1, Month.JANUARY, year)
        }
    }
}
