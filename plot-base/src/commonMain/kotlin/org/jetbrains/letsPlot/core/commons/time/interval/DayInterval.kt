/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Duration
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

internal class DayInterval(
    count: Int,
) : TimeZoneAwareInterval(count) {

    override val tickFormatPattern: String
        get() = "%b %e"

    override fun atOrBefore(dateTime: DateTime): DateTime {
        return DateTime(dateTime.date)
    }

    override fun addInterval(dateTime: DateTime, tz: TimeZone): DateTime {
        return DateTime(
            dateTime.date.add(
                Duration.DAY.mul(count)
            )
        )
    }
}
