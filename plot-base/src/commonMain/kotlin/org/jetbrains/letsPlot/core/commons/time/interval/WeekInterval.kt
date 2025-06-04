/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

internal class WeekInterval(
    private val count: Int,
) : TimeZoneAwareInterval() {

    override val tickFormatPattern: String = DayInterval.TICK_FORMAT

    override fun atOrBefore(dateTime: DateTime): DateTime {
        // ISO 8601: week starts on Monday.
        val daysFromWeekStart = dateTime.weekDay.ordinal
        val monday = dateTime.date.subtractDays(daysFromWeekStart)

        return DateTime(monday)
    }

    override fun addInterval(dateTime: DateTime, tz: TimeZone): DateTime {
        return DateTime(
            dateTime.date.addDays(count * 7)
        )
    }
}
