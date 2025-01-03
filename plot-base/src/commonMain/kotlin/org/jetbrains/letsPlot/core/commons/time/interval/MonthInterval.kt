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

    override fun getFirstDayContaining(instant: DateTime): Date {
        var firstDay = instant.date
        firstDay = Date.firstDayOf(firstDay.year, firstDay.month)
        return firstDay
    }

    override fun addInterval(toInstant: DateTime): DateTime {
        var result = toInstant
        for (i in 0 until count) {
            result = addMonth(result)
        }
        return result
    }

    private fun addMonth(toInstant: DateTime): DateTime {
        var year = toInstant.year
        val month = toInstant.month
        var next = month.next()
        if (next == null) {
            next = Month.JANUARY
            year++
        }
        return DateTime(Date.firstDayOf(year, next))
    }
}
