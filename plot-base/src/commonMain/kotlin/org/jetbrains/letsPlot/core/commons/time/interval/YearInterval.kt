/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormatUtil.formatterDateUTC
import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime

class YearInterval internal constructor(count: Int) : MeasuredInDays(count) {

    override val tickFormatPattern = TICK_FORMAT

    override fun getFirstDayContaining(instant: DateTime): Date {
        return Date.firstDayOf(instant.year)
    }

    override fun addInterval(toInstant: DateTime): DateTime {
        var result = toInstant
        for (i in 0 until count) {
            result = addYear(result)
        }
        return result
    }

    private fun addYear(toInstant: DateTime): DateTime {
        val year = toInstant.year
        return DateTime(Date.firstDayOf(year + 1))
    }

    companion object {
        const val TICK_FORMAT = "%Y"
        const val MS = 31536e6
        val TICK_FORMATTER = formatterDateUTC(TICK_FORMAT)
    }
}
