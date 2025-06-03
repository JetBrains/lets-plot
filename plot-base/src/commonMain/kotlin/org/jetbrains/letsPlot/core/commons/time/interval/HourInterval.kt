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
) : TimeZoneAwareInterval(count) {

    override val tickFormatPattern: String
        get() = "%H:%M"

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
        return dateTime.add(Duration.HOUR.mul(count), tz)
    }
}
