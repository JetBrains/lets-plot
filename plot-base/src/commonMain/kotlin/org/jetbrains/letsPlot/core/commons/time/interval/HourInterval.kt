/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

/**
 * Duration interval represents a fixed-length time span (such as "5 minutes" or "2 hours")
 * that can be used for creating regular time-based tick marks on an axis.
 */
internal class HourInterval(
    count: Int
) : TimeZoneAwareInterval() {

    private val duration: Duration = Duration.HOUR.mul(count)
    override val tickFormatPattern: String = TICK_FORMAT

    override fun atOrBefore(dateTime: DateTime): DateTime {
        return DateTime(
            dateTime.date,
            Time(
                dateTime.hours,
                0
            )
        )
    }

    override fun addInterval(dateTime: DateTime, tz: TimeZone): DateTime {
        return dateTime.add(duration, tz)
    }

    companion object {
        const val TICK_FORMAT = "%H:%M"
    }
}
