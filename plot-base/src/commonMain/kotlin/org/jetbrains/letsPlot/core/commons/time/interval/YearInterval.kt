/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month

class YearInterval internal constructor(count: Int) : MeasuredInDays(count) {

    override val tickFormatPattern = TICK_FORMAT

    override fun getFirstDayContaining(dateTime: DateTime): Date {
        return firstDay(dateTime.year)
    }

    override fun addInterval(dateTime: DateTime): DateTime {
        // Note: returns the first day of the future year after the given date.
        return DateTime(
            firstDay(dateTime.year + count)
        )
    }

    companion object {
        const val TICK_FORMAT = "%Y"
        const val MS = 31536e6

        private fun firstDay(year: Int): Date {
            return Date(1, Month.JANUARY, year)
        }
    }
}
