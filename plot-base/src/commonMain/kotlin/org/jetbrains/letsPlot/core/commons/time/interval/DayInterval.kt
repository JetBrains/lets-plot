/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.time.interval

import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone

class DayInterval(
    private val count: Int,
) : TimeZoneAwareInterval() {

    override val tickFormatPattern: String = TICK_FORMAT

    override fun atOrBefore(dateTime: DateTime): DateTime {
        return DateTime(dateTime.date)
    }

    override fun addInterval(dateTime: DateTime, tz: TimeZone): DateTime {
        return DateTime(
            dateTime.date.addDays(count)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DayInterval
        if (count != other.count) return false
        return true
    }

    override fun hashCode(): Int {
        return count
    }

    companion object {
        const val TICK_FORMAT = "%b %e"
    }
}
