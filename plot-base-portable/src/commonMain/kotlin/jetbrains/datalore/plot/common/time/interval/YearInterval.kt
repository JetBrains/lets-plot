/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.time.interval

import jetbrains.datalore.base.datetime.Date
import jetbrains.datalore.base.datetime.DateTime
import jetbrains.datalore.plot.common.text.Formatter

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
        val TICK_FORMATTER = Formatter.time(TICK_FORMAT)
    }
}
