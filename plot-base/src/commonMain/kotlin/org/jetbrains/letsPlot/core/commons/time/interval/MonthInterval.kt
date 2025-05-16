/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month

internal class MonthInterval(count: Int) : MeasuredInDays(count) {

    override val tickFormatPattern: String
        get() = "%b"

    override fun getFirstDayContaining(dateTime: DateTime): Date {
//        var firstDay = dateTime.date
//        firstDay = Date.firstDayOf(firstDay.year, firstDay.month)
//        return firstDay
        return firstDay(dateTime.year, dateTime.month)
    }

    override fun addInterval(dateTime: DateTime): DateTime {
        var result = dateTime
        for (i in 0 until count) {
            result = addMonth(result)
        }
        return result
    }

    private fun addMonth(dateTime: DateTime): DateTime {
        var year = dateTime.year
        val month = dateTime.month
        var next = month.next()
        if (next == null) {
            next = Month.JANUARY
            year++
        }
        return DateTime(firstDay(year, next))
    }

    companion object {
        private fun firstDay(year: Int, month: Month): Date {
            return Date(1, month, year)
        }
    }
}
