/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

class YearInterval internal constructor(count: Int) : TimeZoneAwareInterval(count) {

    override val tickFormatPattern = TICK_FORMAT

    override fun atOrBefore(dateTime: DateTime): DateTime {
        return DateTime(
            Date(
                1,
                Month.JANUARY,
                dateTime.year
            )
        )
    }

    override fun addInterval(dateTime: DateTime, tz: TimeZone): DateTime {
        return DateTime(
            Date(
                1,
                Month.JANUARY,
                dateTime.year + count
            )
        )
    }

    companion object {
        const val TICK_FORMAT = "%Y"
        const val MS = 31536e6
    }
}
