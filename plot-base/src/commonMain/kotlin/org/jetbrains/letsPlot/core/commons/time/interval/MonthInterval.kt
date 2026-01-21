/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

internal class MonthInterval(
    private val count: Int
) : TimeZoneAwareInterval() {

    override val tickFormatPattern: String = "%b"

    override fun atOrBefore(dateTime: DateTime): DateTime {
        return DateTime(
            Date(
                1,
                dateTime.month,
                dateTime.year
            )
        )
    }

    override fun addInterval(dateTime: DateTime, tz: TimeZone): DateTime {
        return DateTime(
            dateTime.date.addMonths(count),
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MonthInterval
        if (count != other.count) return false
        return true
    }

    override fun hashCode(): Int {
        return count
    }
}
